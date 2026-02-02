/* ===============================
   PWA Push
   =============================== */

/* Service Worker 등록 */
async function registerServiceWorkerOld() {
	if (!("serviceWorker" in navigator)) {
		console.warn("Service Worker not supported");
		return null;
	}

	const SW_URL = window.contextPath + "/sw.js";

	const reg = await navigator.serviceWorker.register(SW_URL, {
		scope: SW_URL.substring(0, SW_URL.lastIndexOf("/") + 1)
	});

	return reg;
}

async function registerServiceWorker() {
    if (!("serviceWorker" in navigator)) {
        console.warn("Service Worker not supported");
        return null;
    }

    // 파일이 루트에 있으므로 경로가 명확해집니다.
    const SW_URL = window.contextPath + "/sw.js";

    try {
        // 루트에 위치하므로 별도의 scope 설정 없이도  앱 전체(/)를 제어할 수 있는 권한을 가집니다.
        const reg = await navigator.serviceWorker.register(SW_URL, {
				scope: SW_URL.substring(0, SW_URL.lastIndexOf("/") + 1)
			});
        
        console.info("Service Worker registered with scope:", reg.scope);
        return reg;
    } catch (error) {
        console.error("Service Worker registration failed:", error);
        return null;
    }
}

/* 알림 권한 요청 */
async function requestNotificationPermission() {
	if (!("Notification" in window)) return "denied";
	return await Notification.requestPermission();
}

async function loadVapidPublicKey() {
    const url = window.contextPath + '/Push/vapid-public-key';
    
    // apiGet은 내부적으로 res.ok 확인, 401/403 처리, Content-Type별 파싱을 모두 수행합니다.
    const res = await apiGet(url);
	const vapidPublicKey = res.message;
	console.info( 'public key', vapidPublicKey);
	
	const bytesKey = urlBase64ToUint8Array(vapidPublicKey)
	
	return bytesKey;
}
/* 푸시 구독 + 서버 저장 */
async function subscribePush() {
	/* 🔹 서버에서 내려준 VAPID Public Key */
	const vapidPublicKey = loadVapidPublicKey();

	if (!vapidPublicKey) {
		console.error("VAPID public key not provided");
		return { ok: false };
	}

	const permission = await requestNotificationPermission();
	if (permission !== "granted") {
		return { ok: false, reason: "permission_denied" };
	}

	const reg = await navigator.serviceWorker.ready;

	let subscription = await reg.pushManager.getSubscription();
	if (!subscription) {
		subscription = await reg.pushManager.subscribe({
			userVisibleOnly: true,
			applicationServerKey: vapidPublicKey
		});
	}

	/* 🔹 구독 저장 API (Security 인증 필요) */
	const subscribeApi = window.contextPath + '/Push/subscription';

	/* 서버에 구독 정보 저장 (로그인 사용자) */
	try {
	    // apiPost는 내부적으로 JSON.stringify(subscription)를 수행하고 headers를 설정함
	    await apiPost(subscribeApi, subscription);
	    
	    return { ok: true };
	} catch (error) {
	    // apiRequest에서 발생한 ApiError가 여기까지 전파됨
	    console.error('구독 저장 실패:', error);
	    return { ok: false, error: error.message };
	}
}

/* Base64URL → Uint8Array */
function urlBase64ToUint8Array(base64UrlString) {
	const padding = "=".repeat((4 - (base64UrlString.length % 4)) % 4);
	const base64 = (base64UrlString + padding)
		.replace(/-/g, "+")
		.replace(/_/g, "/");

	const raw = atob(base64);
	const output = new Uint8Array(raw.length);

	for (let i = 0; i < raw.length; i++) {
		output[i] = raw.charCodeAt(i);
	}
	return output;
}

/* 🔔 버튼에서 호출할 함수 */
async function enablePush() {
	try {
		await registerServiceWorker();
		const result = await subscribePush();

		if (result.ok) {
			await alertP("푸시 알림이 활성화되었습니다.");
		} else {
			await alertP("푸시 알림 권한이 거부되었습니다.");
		}
	} catch (e) {
		console.error(e);
		await alertP("푸시 설정 중 오류가 발생했습니다.");
	}
}