/* /sw.js  (served as /Mem/sw.js) */

self.addEventListener("install", (event) => {
  self.skipWaiting();
});

self.addEventListener("activate", (event) => {
  event.waitUntil(self.clients.claim());
});

// 푸시 수신
self.addEventListener("push", (event) => {
  let payload = { title: "알림", body: "새 소식이 있어요", url: "/Mem/" };

  try {
    if (event.data) payload = Object.assign(payload, event.data.json());
  } catch (e) {
    // text payload일 수도 있음
    if (event.data) payload.body = event.data.text();
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
  const targetUrl = (event.notification?.data && event.notification.data.url) ? event.notification.data.url : "/Mem/";

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
