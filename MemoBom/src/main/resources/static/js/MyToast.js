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
		// 반투명 + 그라데이션(유리 느낌)
		bg: "bg-gradient-to-br from-sky-500/40 via-blue-500/30 to-indigo-600/40",
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
		bg: "bg-gradient-to-br from-rose-500/45 via-red-500/30 to-fuchsia-600/40",
		bar: "bg-rose-100/90",
		ring: "ring-rose-300/30",
	},
};

// 토스트 표시
function showToast(title, message, type = ToastType.INFO, duration = 2000, buttons = [], onClose = null) {

	if (message === undefined && typeof title === "string") {
		message = title;
		title = "";
	}

	// container 생성/획득
	let toastContainer = document.getElementById("toastContainer");
	if (!toastContainer) {
		toastContainer = document.createElement("div");
		toastContainer.id = "toastContainer";
		toastContainer.className =
			"fixed inset-0 z-50 flex items-center justify-center pointer-events-none px-4";
		document.body.appendChild(toastContainer);
	}

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

	// container가 중앙정렬하므로 absolute/top/left 불필요
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

		// 애니메이션: 살짝 아래에서 올라오며 페이드 인/아웃
		"opacity-0 translate-y-2 scale-[0.98]",
		"transition-all duration-200 ease-out",
	].join(" ");

	// ProgressBar
	const progressBar = document.createElement("div");
	progressBar.className = `absolute top-0 left-0 h-[3px] rounded-t-2xl ${theme.bar}`;
	progressBar.style.width = "100%";
	progressBar.style.transition = `width linear ${duration}ms`;

	// title
	const titleElement = document.createElement("div");
	titleElement.className = "font-semibold text-base tracking-tight text-gray-900 drop-shadow-sm";
	titleElement.innerText = title ?? "";

	// message
	const messageElement = document.createElement("div");
	messageElement.className = "mt-1 text-sm text-gray-800 leading-relaxed drop-shadow-sm";
	messageElement.innerText = message ?? "";

	// buttons
	const buttonContainer = document.createElement("div");
	buttonContainer.className = "mt-4 flex justify-center gap-2";

	buttons.forEach((btn) => {
		const button = document.createElement("button");
		button.type = "button";
		button.className =
			"rounded-lg bg-white/85 px-3 py-1.5 text-sm font-medium text-gray-900 hover:bg-white transition";
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

	// 조립
	toast.appendChild(progressBar);
	toast.appendChild(titleElement);
	toast.appendChild(messageElement);
	if (buttons.length > 0) toast.appendChild(buttonContainer);

	toastContainer.appendChild(toast);

	// show animation
	requestAnimationFrame(() => {
		toast.classList.remove("opacity-0", "translate-y-2", "scale-[0.98]");
		toast.classList.add("opacity-100", "translate-y-0", "scale-100");
		progressBar.style.width = "0%";
	});

	// auto close
	const timer = window.setTimeout(() => closeToast(toastId, onClose), duration);

	// 필요 시 외부에서 강제로 닫을 수 있게 반환(선택)
	return () => {
		window.clearTimeout(timer);
		closeToast(toastId, onClose);
	};
}

// 닫기
function closeToast(toastId, onClose = null) {
	const toast = document.getElementById(toastId);
	if (!toast) return;

	toast.classList.remove("opacity-100", "translate-y-0", "scale-100");
	toast.classList.add("opacity-0", "translate-y-2", "scale-[0.98]");

	window.setTimeout(() => {
		toast.remove();
		if (typeof onClose === "function") onClose();
	}, 220);
}
