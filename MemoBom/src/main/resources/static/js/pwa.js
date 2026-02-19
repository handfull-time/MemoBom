/* pwa.js (refactor) */
(function() {
	const cp = () => (window.contextPath || '');

	function supported() {
		return ('serviceWorker' in navigator) && ('PushManager' in window) && ('Notification' in window);
	}

	async function registerServiceWorker() {
		if (!("serviceWorker" in navigator)) return null;

		const SW_URL = cp() + "/sw.js";
		try {
			const reg = await navigator.serviceWorker.register(SW_URL, {
				scope: SW_URL.substring(0, SW_URL.lastIndexOf("/") + 1)
			});
			return reg;
		} catch (e) {
			console.error("Service Worker registration failed:", e);
			return null;
		}
	}

	async function requestPermission() {
		if (!("Notification" in window)) return "denied";
		return await Notification.requestPermission();
	}

	function permission() {
		return ("Notification" in window) ? Notification.permission : "denied";
	}

	async function loadVapidPublicKey() {
		const url = cp() + '/Push/vapid-public-key.json';
		const res = await apiGet(url);
		const vapidPublicKey = res.message;
		return urlBase64ToUint8Array(vapidPublicKey);
	}

	async function getSubscription() {
		if (!supported()) return null;
		await registerServiceWorker();
		const reg = await navigator.serviceWorker.ready;
		return await reg.pushManager.getSubscription();
	}

	async function subscribeDevice() {
		if (!supported()) return { ok: false, reason: 'not_supported' };

		const perm = await requestPermission();
		if (perm !== "granted") return { ok: false, reason: "permission_denied" };

		// 1. 서버로부터 최신 VAPID PublicKey 로드 (Uint8Array)
		const vapidPublicKey = await loadVapidPublicKey();
		if (!vapidPublicKey) return { ok: false, reason: 'no_vapid' };

		const reg = await navigator.serviceWorker.ready;
		let subscription = await reg.pushManager.getSubscription();

		// 기존 구독이 있는 경우 서버의 최신 키와 일치하는지 검증
		if (subscription) {
			const currentSubOptions = subscription.options;
			// 기존 구독의 applicationServerKey를 Uint8Array로 변환
			const existingKey = new Uint8Array(currentSubOptions.applicationServerKey);

			// 서버 키와 비교 (바이트 단위 일치 여부 확인)
			const isKeyMatch = existingKey.length === vapidPublicKey.length &&
				existingKey.every((v, i) => v === vapidPublicKey[i]);

			if (!isKeyMatch) {
				console.warn("VAPID Key mismatch detected. Re-subscribing...");
				await subscription.unsubscribe(); // 구형 키 기반 구독 해제
				subscription = null; // 아래에서 재구독 유도
			}
		}

		if (!subscription) {
			try {
				subscription = await reg.pushManager.subscribe({
					userVisibleOnly: true,
					applicationServerKey: vapidPublicKey
				});
			} catch (e) {
				console.error("Push subscription failed:", e);
				return { ok: false, reason: 'subscribe_failed' };
			}
		}

		// 서버로 보낼 payload 구성
		const ua = navigator.userAgent || "";
		const payload = {
			endpoint: subscription.endpoint,
			expirationTime: subscription.expirationTime ?? 0,
			keys: {
				p256dh: btoa(String.fromCharCode.apply(null, new Uint8Array(subscription.getKey('p256dh'))))
					.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, ''),
				auth: btoa(String.fromCharCode.apply(null, new Uint8Array(subscription.getKey('auth'))))
					.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
			},
			deviceId: getOrCreateDeviceId(),
			userAgent: ua,
			browser: detectBrowser(ua),
			os: detectOs(ua)
		};

		// 서버 저장
		await apiPost(cp() + '/Push/Subscription.json', payload);
		return { ok: true, subscription };
	}

	async function unsubscribeDevice() {
		if (!supported()) return { ok: false, reason: 'not_supported' };

		const sub = await getSubscription();
		if (!sub) return { ok: true, unsubscribed: false };

		const endpoint = sub.endpoint;
		await sub.unsubscribe();

		// 서버 삭제(현재 MyPage가 호출하던 delete 경로를 여기로 이동)【:contentReference[oaicite:3]{index=3}】
		await apiDelete(cp() + '/Push/Subscription.json?endpoint=' + encodeURIComponent(endpoint), {});

		return { ok: true, unsubscribed: true };
	}

	// ---- 계정 단위 수신 설정(Status.json) ----
	async function getUserPushStatus() {
		const res = await apiGet(cp() + '/Push/Status.json');
		if (res?.code !== '0') throw new Error(res?.message || 'status error');
		return !!res.data;
	}

	async function setUserPushStatus(enabled) {
		const url = cp() + '/Push/Status.json?enabled=' + (enabled ? 'true' : 'false');
		const res = await apiRequest(url, { method: 'POST' });
		if (res?.code !== '0') throw new Error(res?.message || 'setStatus error');
		return !!res.data;
	}

	// ---- util (기존 pwa.js 그대로) ----
	function urlBase64ToUint8Array(base64UrlString) {
		const padding = "=".repeat((4 - (base64UrlString.length % 4)) % 4);
		const base64 = (base64UrlString + padding).replace(/-/g, "+").replace(/_/g, "/");
		const raw = atob(base64);
		const output = new Uint8Array(raw.length);
		for (let i = 0; i < raw.length; i++) output[i] = raw.charCodeAt(i);
		return output;
	}

	function getOrCreateDeviceId() {
		const key = "memoBom_device_id";
		try {
			let v = localStorage.getItem(key);
			if (!v) {
				v = (crypto?.randomUUID?.() || (Date.now() + "-" + Math.random().toString(16).slice(2)));
				localStorage.setItem(key, v);
			}
			return v;
		} catch (e) {
			return "na";
		}
	}

	function detectBrowser(ua) {
		ua = ua || "";
		if (/edg/i.test(ua)) return "Edge";
		if (/opr\//i.test(ua) || /opera/i.test(ua)) return "Opera";
		if (/chrome/i.test(ua) && !/edg|opr/i.test(ua)) return "Chrome";
		if (/safari/i.test(ua) && !/chrome|crios|edg|opr/i.test(ua)) return "Safari";
		if (/firefox/i.test(ua)) return "Firefox";
		return "Unknown";
	}

	function detectOs(ua) {
		ua = ua || "";
		if (/windows nt/i.test(ua)) return "Windows";
		if (/android/i.test(ua)) return "Android";
		if (/iphone|ipad|ipod/i.test(ua)) return "iOS";
		if (/mac os x/i.test(ua)) return "macOS";
		if (/linux/i.test(ua)) return "Linux";
		return "Unknown";
	}

	// ---- public API ----
	window.PWA_PUSH = Object.freeze({
		supported,
		permission,
		requestPermission,
		registerServiceWorker,

		getSubscription,
		subscribeDevice,
		unsubscribeDevice,

		getUserPushStatus,
		setUserPushStatus,
	});
})();
