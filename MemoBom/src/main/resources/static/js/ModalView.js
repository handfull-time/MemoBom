let popupIndex = 0;
const modalStack = [];   // 열린 모달 overlay id 스택
let lastFocusedEl = null;

// Method 종류 
const MethodType = Object.freeze({ Get: "GET", Post: "POST" });

// ContentsType 종류 
const ContentsType = Object.freeze({ Form: "x-www-form-urlencoded", Json: "application/json", Multipart: "multipart/form-data" });

// z-index 계산기
function zFor(index, type) { // type: 'overlay' | 'window'
	const base = 1000 + index * 2;
	return type === 'overlay' ? base : base + 1;
}

// 스크롤 잠금/해제
function lockScroll() { document.documentElement.classList.add('overflow-hidden'); }
function unlockScroll() { document.documentElement.classList.remove('overflow-hidden'); }

function loadingShow(){
	const loading = document.getElementById("loadingOverlay");
	loading?.classList.remove("hidden");
}

function loadingHide(){
	const loading = document.getElementById("loadingOverlay");
	loading?.classList.add("hidden");
}

// 최상단 모달 id 반환
function topModalId() { return modalStack[modalStack.length - 1] || null; }

// 공통 요청 옵션 빌더
function buildRequestOptions({ method, contentType, data }) {
	const opts = { method, headers: {} };

	if (method === 'GET' || method === 'HEAD') return opts;

	if (contentType === 'application/json') {
		opts.headers['Content-Type'] = 'application/json';
		opts.body = data ? JSON.stringify(data) : undefined;
	} else if (contentType === 'application/x-www-form-urlencoded') {
		opts.headers['Content-Type'] = 'application/x-www-form-urlencoded';
		opts.body = (typeof data === 'string') ? data : new URLSearchParams(data).toString();
	} else if (contentType === 'multipart/form-data') {
		// 절대 Content-Type을 수동 지정하지 말 것 (boundary 필요)
		opts.body = data; // data는 FormData 인스턴스여야 함
	} else {
		// 기본값: 그냥 body만
		opts.body = data;
	}
	return opts;
}

async function openPopup(sendObj) {
	const defaults = {
		url: null,
		method: MethodType.Post,
		contentType: ContentsType.Json,
		NotCheck: false,
		data: null,
		popupId: null,
		width: '800px',
		onLoadFunction: null
	};
	const sendValue = { ...defaults, ...sendObj };

	if (!sendValue.url) throw new Error("❌ [openPopup Error] 'url' 값이 필수입니다.");
	if (!sendValue.popupId) throw new Error("❌ [openPopup Error] 'popupId' 값이 필수입니다.");

	if (!sendValue.NotCheck) {
		const refresh = await checkAndRefreshToken();
		if (!refresh) return;
	}

	// 포커스 복원용 저장
	lastFocusedEl = document.activeElement;

	loadingHide();

	try {
		const opts = buildRequestOptions({
			method: sendValue.method,
			contentType: sendValue.contentType,
			data: sendValue.data
		});

		const response = await apiRequest(sendValue.url, opts);
		if (!response.ok) throw new Error(`HTTP 오류: ${response.status} ${response.statusText}`);
		const html = await response.text();

		// 스택 인덱스
		popupIndex++;
		const index = popupIndex;

		// 오버레이 복제
		const originalPopup = document.getElementById("popupOverlay");
		const newPopup = originalPopup.cloneNode(true);

		const newPopupId = `popupOverlay_${index}`;
		const newWindowId = `popupWindow_${index}`;
		const newContentId = `popupContentContainer_${index}`;

		newPopup.id = newPopupId;
		newPopup.style.zIndex = String(zFor(index, 'overlay'));

		const popupWindow = newPopup.querySelector("#popupWindow");
		popupWindow.id = newWindowId;
		popupWindow.style.width = sendValue.width;
		popupWindow.style.zIndex = String(zFor(index, 'window'));
		popupWindow.setAttribute('role', 'dialog');
		popupWindow.setAttribute('aria-modal', 'true');

		const contentEl = newPopup.querySelector("#popupContentContainer");
		contentEl.id = newContentId;

		// 드래그 상태 & 핸들러 저장용 객체
		popupWindow.dragObj = {
			isDragging: false,
			offsetX: 0,
			offsetY: 0,
			moveHandler: null,
			upHandler: null
		};

		// 보이기
		newPopup.classList.remove("hidden");

		// 추가 & 스택 push
		document.getElementById("popupContainer").appendChild(newPopup);
		modalStack.push(newPopupId);
		lockScroll(); // 첫 모달이든 중첩이든 락 유지

		// 서버 HTML 삽입
		const container = document.getElementById(newContentId);
		container.innerHTML = html;

		// 스크립트 실행 (가능하면 지양)
		container.querySelectorAll("script").forEach(oldScript => {
			const s = document.createElement("script");
			if (oldScript.src) s.src = oldScript.src;
			else s.textContent = oldScript.textContent;
			[...oldScript.attributes].forEach(a => s.setAttribute(a.name, a.value));
			s.dataset.popupId = newPopupId;
			oldScript.remove();
			document.body.appendChild(s);
		});

		// onLoad 콜백
		if (sendValue.onLoadFunction) {
			(typeof sendValue.onLoadFunction === 'function'
				? sendValue.onLoadFunction
				: window[sendValue.onLoadFunction]
			)?.(newPopup);
		}

		// 포커스 이동
		popupWindow.tabIndex = -1;
		popupWindow.focus();

		// Esc로 최상단 모달 닫기
		newPopup.addEventListener('keydown', (ev) => {
			if (ev.key === 'Escape' && topModalId() === newPopupId) {
				const closeBtn = newPopup.querySelector('button[onclick^="closePopup"]');
				closeBtn?.click();
			}
		});

		// 오버레이 클릭으로 닫고 싶다면(옵션)
		// newPopup.addEventListener('mousedown', (e) => {
		//   if (e.target === newPopup && topModalId() === newPopupId) closePopup(newPopup.querySelector('button[onclick^="closePopup"]'));
		// });

	} catch (err) {
		console.error(err);
	} finally {
		loadingShow();
	}
}

// 드래그 시작
function startDrag(event, headerElement) {
	const popup = headerElement.closest(".modalTop");
	if (!popup) return;

	const rect = popup.getBoundingClientRect();
	popup.dragObj.isDragging = true;
	popup.dragObj.offsetX = event.clientX - rect.left;
	popup.dragObj.offsetY = event.clientY - rect.top;

	popup.style.position = "fixed"; // 뷰포트 기준으로 이동
	// 동일 참조의 핸들러 저장
	popup.dragObj.moveHandler = (e) => doDrag(e, popup);
	popup.dragObj.upHandler = (e) => stopDrag(e, popup);

	document.addEventListener("mousemove", popup.dragObj.moveHandler);
	document.addEventListener("mouseup", popup.dragObj.upHandler);

	// 선택 방지
	document.body.classList.add('select-none');
}

function doDrag(event, popup) {
	if (!popup.dragObj.isDragging) return;
	const vw = window.innerWidth;
	const vh = window.innerHeight;
	const w = popup.offsetWidth;
	const h = popup.offsetHeight;

	let left = event.clientX - popup.dragObj.offsetX;
	let top = event.clientY - popup.dragObj.offsetY;

	// 화면 밖으로 나가지 않도록 clamp
	left = Math.max(0, Math.min(left, vw - w));
	top = Math.max(0, Math.min(top, vh - h));

	popup.style.left = left + "px";
	popup.style.top = top + "px";
}

function stopDrag(event, popup) {
	popup.dragObj.isDragging = false;
	document.removeEventListener("mousemove", popup.dragObj.moveHandler);
	document.removeEventListener("mouseup", popup.dragObj.upHandler);
	popup.dragObj.moveHandler = null;
	popup.dragObj.upHandler = null;

	document.body.classList.remove('select-none');
}

// 닫기
function closePopup(buttonElementOrPopup) {
	// button 요소 또는 overlay 요소 둘 다 지원
	const modal = (buttonElementOrPopup instanceof Element && buttonElementOrPopup.classList.contains('fixed'))
		? buttonElementOrPopup
		: buttonElementOrPopup.closest(".fixed");

	if (!modal) return;

	const modalId = modal.id;

	// 스크립트 제거
	const scripts = document.getElementsByTagName('script');
	for (let i = scripts.length - 1; i >= 0; i--) {
		const s = scripts[i];
		if ((s.src && s.src.includes(modalId)) || (s.dataset && s.dataset.popupId === modalId)) {
			s.parentNode.removeChild(s);
		}
	}

	// DOM 제거
	modal.remove();

	// 스택 팝 & 스크롤 잠금 해제 여부
	const idx = modalStack.indexOf(modalId);
	if (idx >= 0) modalStack.splice(idx, 1);
	if (modalStack.length === 0) unlockScroll();

	// 포커스 복원
	lastFocusedEl?.focus?.();
	lastFocusedEl = null;
}
