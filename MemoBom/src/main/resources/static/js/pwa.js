(function () {

	/**
	 * 애플리케이션의 Context Path를 반환합니다.
	 * @returns {string} window.contextPath 또는 빈 문자열
	 */
	const cp = () => (window.contextPath || '');

	/**
	 * Service Worker 등록 Promise를 캐시합니다.
	 * @type {Promise<ServiceWorkerRegistration|null>|null}
	 */
	let swRegisterPromise = null;

	/**
	 * 현재 브라우저가 PWA 및 Push 알림 기능을 지원하는지 여부를 확인합니다.
	 * @returns {boolean} 지원 여부
	 */
	function supported() {
		return ('serviceWorker' in navigator)
			&& ('PushManager' in window)
			&& ('Notification' in window);
	}

	/**
	 * 현재 설정된 알림 권한 상태를 확인합니다.
	 * @returns {NotificationPermission} 현재 권한 상태
	 */
	function permission() {
		return ('Notification' in window) ? Notification.permission : 'denied';
	}

	/**
	 * Service Worker를 등록합니다.
	 * @async
	 * @returns {Promise<ServiceWorkerRegistration|null>} 등록 성공 시 Registration 객체, 실패 시 null
	 */
	async function registerServiceWorker() {
		if (!('serviceWorker' in navigator)) return null;

		const SW_URL = cp() + '/sw.js';

		try {
			const reg = await navigator.serviceWorker.register(SW_URL, {
				scope: SW_URL.substring(0, SW_URL.lastIndexOf('/') + 1)
			});
			return reg;
		} catch (e) {
			console.error('Service Worker registration failed:', e);
			return null;
		}
	}

	/**
	 * Service Worker를 1회만 등록하도록 보장합니다.
	 * @async
	 * @returns {Promise<ServiceWorkerRegistration|null>} 등록 성공 시 Registration 객체, 실패 시 null
	 */
	function ensureServiceWorkerRegistered() {
		if (!swRegisterPromise) {
			swRegisterPromise = registerServiceWorker();
		}
		return swRegisterPromise;
	}

	/**
	 * Service Worker Registration을 반환합니다.
	 * @async
	 * @throws {Error} Service Worker 준비 실패 시 에러 발생
	 * @returns {Promise<ServiceWorkerRegistration>} 준비된 Registration 객체
	 */
	async function getServiceWorkerRegistration() {
		const reg = await ensureServiceWorkerRegistered();
		if (!reg) {
			throw new Error('service_worker_registration_failed');
		}
		return await navigator.serviceWorker.ready;
	}

	/**
	 * 사용자에게 알림 권한을 요청합니다.
	 * @async
	 * @returns {Promise<NotificationPermission>} "granted", "denied", 또는 "default"
	 */
	async function requestPermission() {
		if (!('Notification' in window)) return 'denied';
		return await Notification.requestPermission();
	}

	/**
	 * 서버로부터 VAPID Public Key를 로드하여 Uint8Array 형식으로 변환합니다.
	 * @async
	 * @throws {Error} API 호출 실패 시 에러 발생
	 * @returns {Promise<Uint8Array>} 변환된 VAPID Public Key
	 */
	async function loadVapidPublicKey() {
		const url = cp() + '/Push/vapid-public-key.json';
		const res = await apiGet(url);
		const vapidPublicKey = res?.message;
		if (!vapidPublicKey) {
			throw new Error('no_vapid');
		}
		return urlBase64ToUint8Array(vapidPublicKey);
	}

	/**
	 * 현재 활성화된 Push 구독 정보를 조회합니다.
	 * @async
	 * @returns {Promise<PushSubscription|null>} 구독 정보 객체 또는 null
	 */
	async function getSubscription() {
		if (!supported()) return null;

		const reg = await getServiceWorkerRegistration();
		return await reg.pushManager.getSubscription();
	}

	/**
	 * PushSubscription의 applicationServerKey가 현재 서버의 VAPID Key와 일치하는지 확인합니다.
	 * @param {PushSubscription} subscription 현재 구독 객체
	 * @param {Uint8Array} vapidPublicKey 서버 VAPID Public Key
	 * @returns {boolean} 일치 여부
	 */
	function isSameApplicationServerKey(subscription, vapidPublicKey) {
		const currentSubOptions = subscription?.options;
		const appServerKey = currentSubOptions?.applicationServerKey;
		if (!appServerKey) return false;

		const existingKey = new Uint8Array(appServerKey);
		return existingKey.length === vapidPublicKey.length
			&& existingKey.every((v, i) => v === vapidPublicKey[i]);
	}

	/**
	 * ArrayBuffer를 Base64URL 문자열로 인코딩합니다.
	 * @param {ArrayBuffer|null} arrayBuffer 변환 대상 버퍼
	 * @returns {string} Base64URL 문자열
	 */
	function encodeKey(arrayBuffer) {
		if (!arrayBuffer) return '';
		return btoa(String.fromCharCode(...new Uint8Array(arrayBuffer)))
			.replace(/\+/g, '-')
			.replace(/\//g, '_')
			.replace(/=+$/, '');
	}

	/**
	 * PushSubscription을 서버 저장용 payload로 변환합니다.
	 * @param {PushSubscription} subscription Push 구독 객체
	 * @returns {object} 서버 저장용 payload
	 */
	function buildSubscriptionPayload(subscription) {
		const ua = navigator.userAgent || '';

		return {
			endpoint: subscription.endpoint,
			expirationTime: subscription.expirationTime ?? 0,
			keys: {
				p256dh: encodeKey(subscription.getKey('p256dh')),
				auth: encodeKey(subscription.getKey('auth'))
			},
			deviceId: getOrCreateDeviceId(),
			userAgent: ua,
			browser: detectBrowser(ua),
			os: detectOs(ua)
		};
	}

	/**
	 * PushSubscription을 서버에 저장합니다.
	 * @async
	 * @param {PushSubscription} subscription Push 구독 객체
	 * @returns {Promise<void>}
	 */
	async function saveSubscriptionToServer(subscription) {
		const payload = buildSubscriptionPayload(subscription);
		await apiPost(cp() + '/Push/Subscription.json', payload);
	}

	/**
	 * endpoint 기준으로 서버에 저장된 Push 구독 정보를 삭제합니다.
	 * @async
	 * @param {string} endpoint Push endpoint
	 * @returns {Promise<void>}
	 */
	async function deleteSubscriptionFromServer(endpoint) {
		if (!endpoint) return;
		await apiDelete(
			cp() + '/Push/Subscription.json?endpoint=' + encodeURIComponent(endpoint),
			{}
		);
	}

	/**
	 * 브라우저에 유효한 Push 구독이 존재하도록 보장하고 서버와 동기화합니다.
	 * 서버의 VAPID Key와 기존 구독 키가 다를 경우 자동으로 재구독을 수행합니다.
	 *
	 * @async
	 * @returns {Promise<{ok: boolean, reason?: string, subscription?: PushSubscription}>} 처리 결과
	 */
	async function subscribeDevice() {
		if (!supported()) {
			return { ok: false, reason: 'not_supported' };
		}

		const perm = await requestPermission();
		if (perm !== 'granted') {
			return { ok: false, reason: 'permission_denied' };
		}

		let vapidPublicKey;
		let reg;
		let subscription;

		try {
			vapidPublicKey = await loadVapidPublicKey();
			reg = await getServiceWorkerRegistration();
			subscription = await reg.pushManager.getSubscription();
		} catch (e) {
			console.error('Push precondition failed:', e);
			return { ok: false, reason: e?.message || 'precondition_failed' };
		}

		if (subscription && !isSameApplicationServerKey(subscription, vapidPublicKey)) {
			try {
				console.warn('VAPID Key mismatch detected. Re-subscribing...');
				await subscription.unsubscribe();
				await deleteSubscriptionFromServer(subscription.endpoint);
			} catch (e) {
				console.error('Failed to clean old subscription:', e);
			}
			subscription = null;
		}

		if (!subscription) {
			try {
				subscription = await reg.pushManager.subscribe({
					userVisibleOnly: true,
					applicationServerKey: vapidPublicKey
				});
			} catch (e) {
				console.error('Push subscription failed:', e);
				return { ok: false, reason: 'subscribe_failed' };
			}
		}

		try {
			await saveSubscriptionToServer(subscription);
			return { ok: true, subscription };
		} catch (e) {
			console.error('Failed to save subscription to server:', e);
			return { ok: false, reason: 'server_save_failed' };
		}
	}

	/**
	 * 기기의 Push 구독을 해제하고 서버에서 해당 구독 정보를 삭제합니다.
	 * 구독이 이미 없으면 성공으로 간주합니다.
	 *
	 * @async
	 * @returns {Promise<{ok: boolean, unsubscribed: boolean, reason?: string}>} 해제 결과
	 */
	async function unsubscribeDevice() {
		if (!supported()) {
			return { ok: false, reason: 'not_supported', unsubscribed: false };
		}

		let sub;
		try {
			sub = await getSubscription();
		} catch (e) {
			console.error('Failed to get current subscription:', e);
			return { ok: false, reason: 'subscription_lookup_failed', unsubscribed: false };
		}

		if (!sub) {
			return { ok: true, unsubscribed: false };
		}

		const endpoint = sub.endpoint;

		try {
			await sub.unsubscribe();
		} catch (e) {
			console.error('Failed to unsubscribe browser subscription:', e);
			return { ok: false, reason: 'unsubscribe_failed', unsubscribed: false };
		}

		try {
			await deleteSubscriptionFromServer(endpoint);
		} catch (e) {
			console.error('Failed to delete subscription from server:', e);
			return { ok: false, reason: 'server_delete_failed', unsubscribed: true };
		}

		return { ok: true, unsubscribed: true };
	}

	/**
	 * 서버로부터 현재 사용자의 Push 수신 설정 상태를 조회합니다.
	 * @async
	 * @throws {Error} API 호출 실패 시 에러 발생
	 * @returns {Promise<boolean>} Push 수신 활성화 여부
	 */
	async function getUserPushStatus() {
		const param = {
			deviceId: getOrCreateDeviceId()
		};

		const res = await apiGetWithParam(cp() + '/Push/Status.json', param);
		if (res?.code !== '0') {
			throw new Error(res?.message || 'status_error');
		}
		
		const result = !!res.data;
		console.info('GET /Push/Status.json', result );
		
		return result;
	}

	/**
	 * 사용자의 Push 수신 설정(ON/OFF)을 서버에 저장합니다.
	 * @async
	 * @param {boolean} enabled 활성화 여부
	 * @throws {Error} API 호출 실패 시 에러 발생
	 * @returns {Promise<boolean>} 설정 완료 후의 상태값
	 */
	async function setUserPushStatus(enabled) {
		console.info('SET /Push/Status.json', enabled );
		
		const url = cp() + '/Push/Status.json?enabled=' + (enabled ? 'true' : 'false');
		const res = await apiRequest(url, { method: 'POST' });
		if (res?.code !== '0') {
			throw new Error(res?.message || 'set_status_error');
		}
		return !!res.data;
	}

	/**
	 * 현재 Push 관련 종합 상태를 조회합니다.
	 * @async
	 * @returns {Promise<{
	 *   supported: boolean,
	 *   permission: NotificationPermission,
	 *   userEnabled: boolean,
	 *   subscribed: boolean,
	 *   ready: boolean
	 * }>} 종합 상태
	 */
	async function getPushState() {
		const result = {
			supported: supported(),
			permission: permission(),
			userEnabled: false,
			subscribed: false,
			ready: false
		};

		if (!result.supported) {
			return result;
		}

		try {
			result.userEnabled = await getUserPushStatus();
		} catch (e) {
			console.error('getUserPushStatus failed:', e);
		}

		try {
			result.subscribed = !!(await getSubscription());
		} catch (e) {
			console.error('getSubscription failed:', e);
		}

		result.ready = result.permission === 'granted'
			&& result.userEnabled
			&& result.subscribed;

		return result;
	}

	/**
	 * Push 활성화/비활성화를 일관되게 처리합니다.
	 * 외부에서는 이 함수만 호출하면 되도록 구성합니다.
	 *
	 * 활성화:
	 * 1. 브라우저 구독 확보
	 * 2. 서버 사용자 설정 ON 저장
	 * 3. 설정 저장 실패 시 구독 롤백
	 *
	 * 비활성화:
	 * 1. 서버 사용자 설정 OFF 저장
	 * 2. 브라우저 구독 해제
	 *
	 * @async
	 * @param {boolean} enabled 활성화 여부
	 * @returns {Promise<{
	 *   ok: boolean,
	 *   reason?: string,
	 *   state: {
	 *     supported: boolean,
	 *     permission: NotificationPermission,
	 *     userEnabled: boolean,
	 *     subscribed: boolean,
	 *     ready: boolean
	 *   }
	 * }>} 처리 결과 및 최종 상태
	 */
	async function setPushEnabled(enabled) {
		if (!supported()) {
			return {
				ok: false,
				reason: 'not_supported',
				state: await getPushState()
			};
		}

		if (enabled) {
	        // 1) 실제 구독 먼저 성공시킨 후
			const subResult = await subscribeDevice();
			if (!subResult.ok) {
				return {
					ok: false,
					reason: subResult.reason || 'subscribe_failed',
					state: await getPushState()
				};
			}

	        // 2) 사용자 설정 반영
			try {
				await setUserPushStatus(true);
				return {
					ok: true,
					state: await getPushState()
				};
			} catch (e) {
				console.error('setUserPushStatus(true) failed:', e);

	            // 롤백
				try {
					await unsubscribeDevice();
				} catch (rollbackError) {
					console.error('rollback unsubscribeDevice failed:', rollbackError);
				}

				return {
					ok: false,
					reason: 'status_update_failed',
					state: await getPushState()
				};
			}
		}

		try {
			await setUserPushStatus(false);
		} catch (e) {
			console.error('setUserPushStatus(false) failed:', e);
			return {
				ok: false,
				reason: 'status_update_failed',
				state: await getPushState()
			};
		}

		const unsubResult = await unsubscribeDevice();
		if (!unsubResult.ok) {
			return {
				ok: false,
				reason: unsubResult.reason || 'unsubscribe_failed',
				state: await getPushState()
			};
		}

		return {
			ok: true,
			state: await getPushState()
		};
	}

	/**
	 * Base64URL 형식의 문자열을 Uint8Array로 변환합니다.
	 * @param {string} base64UrlString 변환할 Base64URL 문자열
	 * @returns {Uint8Array} 변환된 바이너리 데이터
	 */
	function urlBase64ToUint8Array(base64UrlString) {
		const padding = '='.repeat((4 - (base64UrlString.length % 4)) % 4);
		const base64 = (base64UrlString + padding)
			.replace(/-/g, '+')
			.replace(/_/g, '/');

		const raw = atob(base64);
		const output = new Uint8Array(raw.length);
		for (let i = 0; i < raw.length; i++) {
			output[i] = raw.charCodeAt(i);
		}
		return output;
	}

	/**
	 * 로컬 스토리지에서 기기 고유 ID를 가져오거나 없으면 생성합니다.
	 * @returns {string} 기기 고유 ID
	 */
	function getOrCreateDeviceId() {
		const key = 'memoBom_device_id';

		try {
			let value = localStorage.getItem(key);
			if (!value) {
				value = crypto?.randomUUID?.()
					|| (Date.now() + '-' + Math.random().toString(16).slice(2));
				localStorage.setItem(key, value);
			}
			return value;
		} catch (e) {
			return 'na';
		}
	}

	/**
	 * UserAgent 문자열을 분석하여 브라우저 종류를 판별합니다.
	 * @param {string} ua UserAgent 문자열
	 * @returns {string} 브라우저 명칭
	 */
	function detectBrowser(ua) {
		ua = ua || '';
		if (/edg/i.test(ua)) return 'Edge';
		if (/opr\//i.test(ua) || /opera/i.test(ua)) return 'Opera';
		if (/chrome/i.test(ua) && !/edg|opr/i.test(ua)) return 'Chrome';
		if (/safari/i.test(ua) && !/chrome|crios|edg|opr/i.test(ua)) return 'Safari';
		if (/firefox/i.test(ua)) return 'Firefox';
		return 'Unknown';
	}

	/**
	 * UserAgent 문자열을 분석하여 운영체제(OS) 종류를 판별합니다.
	 * @param {string} ua UserAgent 문자열
	 * @returns {string} OS 명칭
	 */
	function detectOs(ua) {
		ua = ua || '';
		if (/windows nt/i.test(ua)) return 'Windows';
		if (/android/i.test(ua)) return 'Android';
		if (/iphone|ipad|ipod/i.test(ua)) return 'iOS';
		if (/mac os x/i.test(ua)) return 'macOS';
		if (/linux/i.test(ua)) return 'Linux';
		return 'Unknown';
	}

	// ---- public API ----
	window.PWA_PUSH = Object.freeze({
		supported,
		permission,
		requestPermission,
		registerServiceWorker,

		getPushState,
		setPushEnabled,

		// 필요 시 내부 동작 점검용으로 유지
		getSubscription,
		subscribeDevice,
		unsubscribeDevice,

		getUserPushStatus,
		setUserPushStatus
	});
})();