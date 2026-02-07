/* /sw.js */

self.addEventListener("install", (event) => {
	self.skipWaiting();
});

self.addEventListener("activate", (event) => {
	event.waitUntil(self.clients.claim());
});

// 푸시 수신
self.addEventListener("push", (event) => {
	console.info('push addEventListener', event);

	let payload = {};
	try {
		if (event.data) payload = Object.assign(payload, event.data.json());
	} catch (e) {
		console.error(e);
		return;
	}

	const title = payload.title || "알림";
	const icon = payload.icon || "/images/favicon/favicon_192.png";
	const options = {
		body: payload.message || "",
		icon: icon,
		badge: icon,
		data: payload.data
	};

	event.waitUntil(self.registration.showNotification(title, options));
});

// 알림 클릭 시 이동
self.addEventListener("notificationclick", (event) => {
	event.notification.close();

	const notiData = event.notification?.data || {};
	console.info('push notificationclick', notiData);
	
	const ctx = (notiData.contextPath ?? "");   // ""면 루트
	const clickId = notiData.clickId;

	// targetUrl 처리
	let targetUrl = notiData.url;
	const isAbs = typeof targetUrl === "string" && /^https?:\/\//i.test(targetUrl);

	// url이 "/My/.." 같이 상대경로면 contextPath 보정
	if (targetUrl && !isAbs) {
		if (targetUrl.startsWith("/")) {
			targetUrl = ctx + targetUrl;
		} else {
			// "My/.." 형태면 안전하게 "/" 붙여줌
			targetUrl = ctx + "/" + targetUrl;
		}
	}

	// 클릭 로그 (best-effort: 실패해도 무시)
	const clickPromise = (clickId != null)
		? fetch(ctx + "/Push/Click.json", {
			method: "POST",
			credentials: "include",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ clickId })
		}).catch(() => { })
		: Promise.resolve();

	// url이 없으면 “그냥 알림 확인”으로 종료
	if (!targetUrl) {
		event.waitUntil(clickPromise);
		return;
	}

	const navPromise = (async () => {
		const allClients = await clients.matchAll({ type: "window", includeUncontrolled: true });

		for (const client of allClients) {
			if ("focus" in client) {
				await client.focus();
				if ("navigate" in client) return client.navigate(targetUrl);
				return;
			}
		}
		return clients.openWindow(targetUrl);
	})();

	// ✅ waitUntil은 한 번만(여러 Promise 합치기)
	event.waitUntil(Promise.all([clickPromise, navPromise]));
});

