// Enum처럼 사용할 객체
const ToastType = Object.freeze({
	INFO: "info",
	SUCCESS: "success",
	WARNING: "warning",
	ERROR: "error",
});

// 타입별 스타일(그라데이션/프로그레스 컬러)
const ToastTheme = {
	[ToastType.INFO]: {
		bg: "bg-gradient-to-br from-sky-500/85 via-blue-500/80 to-indigo-600/85",
		bar: "bg-sky-200/80",
		ring: "ring-sky-300/30",
	},
	[ToastType.SUCCESS]: {
		bg: "bg-gradient-to-br from-emerald-500/40 via-green-500/30 to-teal-600/40",
		bar: "bg-emerald-200/80",
		ring: "ring-emerald-300/30",
	},
	[ToastType.WARNING]: {
		bg: "bg-gradient-to-br from-amber-400/45 via-yellow-500/30 to-orange-500/40",
		bar: "bg-amber-100/90",
		ring: "ring-amber-300/30",
	},
	[ToastType.ERROR]: {
		bg: "bg-rose-600/80 bg-gradient-to-br from-rose-500/45 via-red-500/30 to-fuchsia-600/40",
		bar: "bg-rose-100/90",
		ring: "ring-rose-300/30",
	},
};

// ====== 내부 상태 관리(중복 close 방지/타이머 정리) ======
const ToastState = (() => {
	const timers = new Map();   // toastId -> timeoutId
	const closers = new Map();  // toastId -> () => void (ESC 핸들 등 정리)
	return {
		setTimer(id, t) { timers.set(id, t); },
		clearTimer(id) {
			const t = timers.get(id);
			if (t) window.clearTimeout(t);
			timers.delete(id);
		},
		setCleanup(id, fn) { closers.set(id, fn); },
		cleanup(id) {
			const fn = closers.get(id);
			try { fn?.(); } finally { closers.delete(id); }
		}
	};
})();

// ====== 컨테이너 생성/획득 (스택 옵션 지원) ======
function getToastContainer({ position = "center" } = {}) {
	let toastContainer = document.getElementById("toastContainer");
	if (!toastContainer) {
		toastContainer = document.createElement("div");
		toastContainer.id = "toastContainer";
		document.body.appendChild(toastContainer);
	}

	// position: "center" | "top-right"
	if (position === "top-right") {
		toastContainer.className =
			"fixed top-4 right-4 z-50 flex flex-col gap-2 items-stretch pointer-events-none px-4";
	} else {
		// center (기존 동작 + 여러개 쌓이게만 개선)
		toastContainer.className =
			"fixed inset-0 z-50 flex flex-col gap-2 items-center justify-center pointer-events-none px-4";
	}

	return toastContainer;
}

// 토스트 표시
function showToast(
	title,
	message,
	type = ToastType.INFO,
	duration = 2000,
	buttons = [],
	onClose = null,
	options = {} // { position: "center"|"top-right", closable: true|false }
) {
	// title만 넣은 케이스 지원
	if (message === undefined && typeof title === "string") {
		message = title;
		title = "";
	}

	// duration 방어
	duration = Number(duration);
	if (!Number.isFinite(duration)) duration = 2000;
	duration = Math.max(0, duration);

	const { position = "center", closable = true } = options;

	// container 생성/획득
	const toastContainer = getToastContainer({ position });

	// type 검증
	if (!Object.values(ToastType).includes(type)) {
		console.warn(`Invalid toast type: "${type}". Defaulting to INFO.`);
		type = ToastType.INFO;
	}
	const theme = ToastTheme[type];

	// id 충돌 방지
	const toastId = `toast-${Date.now()}-${Math.random().toString(16).slice(2)}`;

	// toast element
	const toast = document.createElement("div");
	toast.id = toastId;

	// a11y
	const isAlert = (type === ToastType.ERROR || type === ToastType.WARNING);
	toast.setAttribute("role", isAlert ? "alert" : "status");
	toast.setAttribute("aria-live", isAlert ? "assertive" : "polite");
	toast.setAttribute("aria-atomic", "true");

	toast.className = [
		"pointer-events-auto",
		"relative",
		"w-full max-w-md",
		"rounded-2xl",
		"px-6 py-4",
		"text-gray-900",
		"shadow-2xl shadow-black/20",
		"backdrop-blur-md",
		"ring-1",
		theme.ring,
		theme.bg,
		// 애니메이션
		"opacity-0 translate-y-2 scale-[0.98]",
		"transition-all duration-200 ease-out",
	].join(" ");

	// ProgressBar
	const progressBar = document.createElement("div");
	progressBar.className = `absolute top-0 left-0 h-[3px] rounded-t-2xl ${theme.bar}`;
	progressBar.style.width = "100%";
	if (duration > 0) {
		progressBar.style.transition = `width linear ${duration}ms`;
	} else {
		// duration=0이면 진행바 애니메이션 의미 없으니 숨김
		progressBar.style.display = "none";
	}

	// title
	const titleElement = document.createElement("div");
	titleElement.className = "font-semibold text-dynamic-base tracking-tight text-gray-900 drop-shadow-sm";
	titleElement.innerText = title ?? "";

	// message
	const messageElement = document.createElement("div");
	messageElement.className = "mt-1 text-dynamic-sm text-gray-800 leading-relaxed drop-shadow-sm";
	messageElement.innerHTML = message ?? "";

	// buttons
	const buttonContainer = document.createElement("div");
	buttonContainer.className = "mt-4 flex justify-center gap-2";

	buttons.forEach((btn) => {
		const button = document.createElement("button");
		button.type = "button";
		button.className =
			"rounded-lg bg-white/85 px-3 py-1.5 text-dynamic-sm font-medium text-gray-900 hover:bg-white transition";
		button.innerText = btn.label ?? "OK";
		button.addEventListener("click", () => {
			try {
				btn.onClick?.();
			} finally {
				closeToast(toastId, onClose);
			}
		});
		buttonContainer.appendChild(button);
	});

	// (선택) 우측 상단 닫기 버튼
	let closeBtn = null;
	if (closable) {
		closeBtn = document.createElement("button");
		closeBtn.type = "button";
		closeBtn.className =
			"absolute right-3 top-3 rounded-md px-2 py-1 text-gray-900/70 hover:text-gray-900 hover:bg-white/40 transition";
		closeBtn.setAttribute("aria-label", "Close");
		closeBtn.innerText = "×";
		closeBtn.addEventListener("click", () => closeToast(toastId, onClose));
	}

	// 조립
	toast.appendChild(progressBar);
	if (closable && closeBtn) toast.appendChild(closeBtn);

	// title이 비어있으면 영역 자체를 제거(여백 최적화)
	if (title && String(title).trim().length > 0) {
		toast.appendChild(titleElement);
		messageElement.classList.remove("mt-1");
		messageElement.classList.add("mt-2");
	}
	toast.appendChild(messageElement);

	if (buttons.length > 0) toast.appendChild(buttonContainer);

	toastContainer.appendChild(toast);

	// show animation
	requestAnimationFrame(() => {
		toast.classList.remove("opacity-0", "translate-y-2", "scale-[0.98]");
		toast.classList.add("opacity-100", "translate-y-0", "scale-100");
		if (duration > 0) progressBar.style.width = "0%";
	});

	// ESC로 닫기(클로저 정리 포함)
	const onKeyDown = (e) => {
		if (e.key === "Escape") closeToast(toastId, onClose);
	};
	document.addEventListener("keydown", onKeyDown);
	ToastState.setCleanup(toastId, () => {
		document.removeEventListener("keydown", onKeyDown);
	});

	// auto close
	if (duration > 0) {
		const timer = window.setTimeout(() => closeToast(toastId, onClose), duration);
		ToastState.setTimer(toastId, timer);
	}

	// 필요 시 외부에서 강제로 닫을 수 있게 반환
	return () => closeToast(toastId, onClose);
}

// 닫기
function closeToast(toastId, onClose = null) {
	const toast = document.getElementById(toastId);
	if (!toast) return;

	// 중복 close 방지(레이스 대응)
	if (toast.dataset.closing === "1") return;
	toast.dataset.closing = "1";

	// 타이머/리스너 정리
	ToastState.clearTimer(toastId);
	ToastState.cleanup(toastId);

	toast.classList.remove("opacity-100", "translate-y-0", "scale-100");
	toast.classList.add("opacity-0", "translate-y-2", "scale-[0.98]");

	window.setTimeout(() => {
		toast.remove();
		if (typeof onClose === "function") onClose();
	}, 220);
}

// showToast("완료", "저장되었습니다", ToastType.SUCCESS, 2000);

//	showToast(
//	  "경고",
//	  "이 작업은 되돌릴 수 없어요",
//	  ToastType.WARNING,
//	  0,
//	  [{ label: "확인", onClick: () => console.log("ok") }],
//	  () => console.log("closed"),
//	  { position: "top-right", closable: true }
//	);
