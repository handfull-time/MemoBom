(function() {
	// ======== 상태 ========
	const modalEl = document.getElementById('customModal');
	const dialogEl = document.getElementById('modalDialog');
	const msgEl = document.getElementById('modalMessage');
	const inputEl = document.getElementById('modalInput');
	const btnOk = document.getElementById('modalConfirmButton');
	const btnCancel = document.getElementById('modalCancelButton');

	let modalCallback = null;
	let resolvePromise = null; // Promise API용
	let previousActive = null;
	let isPromptMode = false;
	let escHandler = null;
	let keytrapHandler = null;

	// ======== 유틸 추가 ========
	function computeTopModalZ() {
		const overlays = Array.from(document.querySelectorAll('[id^="popupOverlay"]'));
		let maxZ = 0;
		for (const el of overlays) {
			const z = parseInt(getComputedStyle(el).zIndex, 10) || 0;
			if (z > maxZ) maxZ = z;
		}
		return maxZ;
	}

	function bringAlertToFront() {
		const FALLBACK = 12000;
		const topZ = computeTopModalZ();
		const overlayZ = Math.max(FALLBACK, topZ + 2);
		const dialogZ = overlayZ + 1;
		modalEl.style.zIndex = String(overlayZ);
		dialogEl.style.zIndex = String(dialogZ);
	}

	// 중복 초기화 가드
	if (modalEl.dataset.init === '1') return;
	modalEl.dataset.init = '1';

	// ======== 유틸 ========
	function openModal({ showCancel = false, prompt = false, okText = '확인' }) {

		// body 직속 보장
		if (modalEl.parentElement !== document.body) {
			document.body.appendChild(modalEl);
		}

		// 🔹 항상 맨 위로
		bringAlertToFront();

		// 상태
		isPromptMode = prompt;

		// input 가시성
		inputEl.classList.toggle('hidden', !prompt);
		if (!prompt) inputEl.value = '';

		// 버튼 표시
		btnCancel.classList.toggle('hidden', !showCancel);
		btnOk.textContent = okText;

		// 스크롤 잠금
		document.body.dataset._overflowPrev = document.body.style.overflow || '';
		document.body.style.overflow = 'hidden';

		// 표시 + 트랜지션
		modalEl.classList.remove('hidden');
		// 강제 리플로우로 트랜지션 시작
		//        void dialogEl.offsetWidth;
		requestAnimationFrame(() => {
			modalEl.classList.remove('opacity-0');
			dialogEl.classList.remove('opacity-0', 'scale-95');
		});

		// 포커스 관리
		previousActive = document.activeElement;
		const focusTarget = prompt ? inputEl : btnOk;
		focusTarget.focus({ preventScroll: true });

		// 키보드 핸들러
		escHandler = (e) => {
			if (e.key === 'Escape') close(false);
		};
		keytrapHandler = (e) => {
			if (e.key === 'Tab') trapFocus(e);
			if (e.key === 'Enter') {
				// prompt 모드면 Enter로 확인
				if (isPromptMode && !btnOk.disabled) {
					e.preventDefault();
					confirmAction();
				}
			}
		};
		document.addEventListener('keydown', escHandler);
		document.addEventListener('keydown', keytrapHandler);
	}

	let hideTimer = null;

	function close(returnVal) {
		// 트랜지션 아웃
		modalEl.classList.add('opacity-0');
		dialogEl.classList.add('opacity-0', 'scale-95');

		// 이전 타이머 정리
		if (hideTimer) { clearTimeout(hideTimer); hideTimer = null; }

		hideTimer = setTimeout(() => {
			modalEl.classList.add('hidden');
			hideTimer = null;
		}, 120);

		// 스크롤 복구
		document.body.style.overflow = document.body.dataset._overflowPrev || '';
		delete document.body.dataset._overflowPrev;

		// 포커스 복구
		if (previousActive && typeof previousActive.focus === 'function') {
			previousActive.focus({ preventScroll: true });
		}

		// 리스너 해제
		document.removeEventListener('keydown', escHandler);
		document.removeEventListener('keydown', keytrapHandler);
		escHandler = keytrapHandler = null;


		setTimeout(() => {
			if (modalCallback) { modalCallback(returnVal); modalCallback = null; }
			if (resolvePromise) { resolvePromise(returnVal); resolvePromise = null; }
		}, 130);
	}

	function trapFocus(e) {
		const focusables = dialogEl.querySelectorAll(
			'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
		);
		const list = Array.prototype.filter.call(focusables, el => !el.hasAttribute('disabled') && !el.classList.contains('hidden'));
		if (list.length === 0) return;

		const first = list[0];
		const last = list[list.length - 1];

		if (e.shiftKey && document.activeElement === first) {
			e.preventDefault();
			last.focus();
		} else if (!e.shiftKey && document.activeElement === last) {
			e.preventDefault();
			first.focus();
		}
	}

	function confirmAction() {
		if (isPromptMode) {
			close(inputEl.value);
		} else {
			close(true);
		}
	}

	function cancelAction() {
		close(false);
	}

	// 버튼 이벤트 (한 번만 바인딩)
	btnOk.addEventListener('click', confirmAction);
	btnCancel.addEventListener('click', cancelAction);

	// 백드롭 클릭으로 닫기 (필요 시 주석 해제/조정)
	modalEl.addEventListener('click', (e) => {
		if (e.target === modalEl) {
			// confirm/prompt일 때만 취소로 간주
			if (!btnCancel.classList.contains('hidden')) cancelAction();
			else close(true);
		}
	});

	// ======== 공개 API (콜백) ========
	function showCustomAlert(message, callback) {
		msgEl.innerText = message;
		modalCallback = callback || null;
		openModal({ showCancel: false, prompt: false, okText: '확인' });
	}

	function showCustomConfirm(message, callback) {
		msgEl.innerText = message;
		modalCallback = callback || null;
		openModal({ showCancel: true, prompt: false, okText: '확인' });
	}

	function showCustomPrompt(message, callback) {
		msgEl.innerText = message;
		modalCallback = callback || null;
		openModal({ showCancel: true, prompt: true, okText: '입력' });
	}

	// ======== Promise 버전 ========
	function alertP(message) {
		return new Promise((resolve) => {
			resolvePromise = resolve;
			showCustomAlert(message);
		});
	}

	function confirmP(message) {
		return new Promise((resolve) => {
			resolvePromise = resolve;
			showCustomConfirm(message);
		});
	}

	function promptP(message, defaultValue = '') {
		return new Promise((resolve) => {
			resolvePromise = resolve;
			inputEl.value = defaultValue || '';
			showCustomPrompt(message);
		});
	}

	// 전역 바인딩 (기존 코드 호환을 위해)
	window.showCustomAlert = showCustomAlert;
	window.showCustomConfirm = showCustomConfirm;
	window.showCustomPrompt = showCustomPrompt;
	window.alertP = alertP;
	window.confirmP = confirmP;
	window.promptP = promptP;
})();


/*

<script>
  // Confirm 예제
  function openConfirmExample() {
    showCustomConfirm("정말 삭제하시겠습니까?", function(result) {
      if (result) {
        alert("확인 버튼을 눌렀습니다.");
      } else {
        alert("취소 버튼을 눌렀습니다.");
      }
    });
  }

  // Prompt 예제
  function openPromptExample() {
    showCustomPrompt("이름을 입력하세요:", function(result) {
      if (result !== false) {
        alert("입력한 값: " + result);
      } else {
        alert("취소되었습니다.");
      }
    });
  }
</script>


<script>
  async function runExamples() {
    await alertP("알림창! 계속 진행합니다.");

    const ok = await confirmP("정말 진행할까요?");
    if (ok) {
      console.log("사용자가 확인을 선택");
    } else {
      console.log("사용자가 취소를 선택");
    }

    const name = await promptP("이름을 입력하세요", "홍길동");
    if (name !== false) {
      console.log("입력값:", name);
    } else {
      console.log("사용자가 취소");
    }
  }
</script>

<button onclick="runExamples()" 
        class="px-4 py-2 bg-indigo-500 text-white rounded hover:bg-indigo-600">
  Promise 방식 실행
</button>

 */