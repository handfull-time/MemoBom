/* /sw.js */

/**
 * Service Worker 설치(Install) 이벤트 핸들러입니다.
 * 새로운 서비스 워커가 발견되면 대기 상태를 거치지 않고 즉시 활성화를 시도합니다.
 */
self.addEventListener("install", (event) => {
	self.skipWaiting();
});

/**
 * Service Worker 활성화(Activate) 이벤트 핸들러입니다.
 * 활성화 즉시 현재 페이지(Clients)들에 대한 제어권을 획득하여 
 * 페이지 새로고침 없이도 서비스 워커 기능을 적용합니다.
 */
self.addEventListener("activate", (event) => {
	event.waitUntil(self.clients.claim());
});

/**
 * 푸시 알림 수신(Push) 이벤트 핸들러입니다.
 * 서버로부터 수신한 페이로드를 파싱하여 브라우저 시스템 알림을 생성합니다.
 * * @param {PushEvent} event 푸시 이벤트 객체. event.data.json()을 통해 페이로드 추출.
 */
self.addEventListener("push", (event) => {
	console.info('push addEventListener', event);

	let payload = {};
	try {
		if (event.data) payload = Object.assign(payload, event.data.json());
	} catch (e) {
		console.error("Push payload parsing failed:", e);
		return;
	}

	const title = payload.title || "알림";
	const icon = payload.icon || "/images/favicon/favicon_192.png";
	
	/**
     * @property {string} body 알림 본문 내용
     * @property {string} icon 알림 아이콘 이미지 경로
     * @property {string} badge 상태 표시줄 등에 표시될 작은 아이콘
     * @property {Object} data 알림 클릭 시 사용할 커스텀 데이터 (url, clickId 등)
     */
	const options = {
		body: payload.message || "",
		icon: icon,
		badge: icon,
		data: payload.data
	};

	event.waitUntil(self.registration.showNotification(title, options));
});

/**
 * 알림 클릭(Notification Click) 이벤트 핸들러입니다.
 * 알림 창을 닫고, 전달된 URL로 이동하거나 통계용 클릭 로그를 서버로 전송합니다.
 * * @param {NotificationEvent} event 알림 이벤트 객체.
 */
self.addEventListener("notificationclick", (event) => {
	event.notification.close();

	const notiData = event.notification?.data || {};
	console.info('push notificationclick', notiData);
	
	const ctx = (notiData.contextPath ?? "");   // Context Path 보정
	const clickId = notiData.clickId;

	// targetUrl 처리
	let targetUrl = notiData.url;
	const isAbs = typeof targetUrl === "string" && /^https?:\/\//i.test(targetUrl);

	// 상대 경로일 경우 Context Path를 결합하여 절대 경로 생성
	if (targetUrl && !isAbs) {
		if (targetUrl.startsWith("/")) {
			targetUrl = ctx + targetUrl;
		} else {
			targetUrl = ctx + "/" + targetUrl;
		}
	}

	/**
     * 통계용 클릭 로그 전송 (Best-effort 방식)
     * @returns {Promise} Fetch 프로미스
     */
	const clickPromise = (clickId != null)
		? fetch(ctx + "/Push/Click.json", {
			method: "POST",
			credentials: "include",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ clickId })
		}).catch((err) => { console.warn("Click log failed:", err); })
		: Promise.resolve();

	// 이동할 URL이 없는 경우 로그 전송만 수행하고 종료
	if (!targetUrl) {
		event.waitUntil(clickPromise);
		return;
	}

	/**
     * 브라우저 창(Window) 제어 로직
     * 이미 열려 있는 해당 사이트의 창이 있다면 포커스 후 이동하고, 없으면 새 창을 엽니다.
     * @async
     */
	const navPromise = (async () => {
		const allClients = await clients.matchAll({ type: "window", includeUncontrolled: true });

		for (const client of allClients) {
			// 해당 서비스 도메인의 창이 이미 열려 있는 경우
			if ("focus" in client) {
				await client.focus();
				if ("navigate" in client) return client.navigate(targetUrl);
				return;
			}
		}
		
		// 열린 창이 없으면 새 창 생성
		return clients.openWindow(targetUrl);
	})();

	// 모든 비동기 작업(로그 전송 및 페이지 이동)이 완료될 때까지 서비스 워커 유지
	event.waitUntil(Promise.all([clickPromise, navPromise]));
});

