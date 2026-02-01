/* /sw.js  (served as /Mem/sw.js) */

self.addEventListener("install", (event) => {
  self.skipWaiting();
});

self.addEventListener("activate", (event) => {
  event.waitUntil(self.clients.claim());
});

// 푸시 수신
self.addEventListener("push", (event) => {
	console.info( 'push addEventListener', event);
	
	let payload = {};
	try {
	    if (event.data) payload = Object.assign(payload, event.data.json());
	} catch (e) {
		console.error(e);
		return;
	}
	
	const title = payload.title || "알림";
	const options = {
		body: payload.body || "",
		icon: "/Mem/icons/icon-192.png", // 아이콘 경로 맞춰주세요
		badge: "/Mem/icons/icon-192.png",
		data: { url: payload.url || "/Mem/" }
  	};
	
	event.waitUntil(self.registration.showNotification(title, options));
});

// 알림 클릭 시 이동
self.addEventListener("notificationclick", (event) => {
  event.notification.close();
  console.info( 'push notificationclick', event);
  
  const targetUrl = (event.notification?.data && event.notification.data.url) ? event.notification.data.url : "/";

  event.waitUntil(
    (async () => {
      const allClients = await clients.matchAll({ type: "window", includeUncontrolled: true });

      // 이미 열린 탭이 있으면 포커스 + 이동
      for (const client of allClients) {
        if ("focus" in client) {
          client.focus();
          if ("navigate" in client) return client.navigate(targetUrl);
          return;
        }
      }
      // 없으면 새로 열기
      return clients.openWindow(targetUrl);
    })()
  );
});
