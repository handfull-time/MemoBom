class ApiError extends Error {
	constructor(message, { status, url, data, headers } = {}) {
		super(message);
		this.name = 'ApiError';
		this.status = status;
		this.url = url;
		this.data = data;     // 서버가 준 에러 본문(가능하면 JSON)
		this.headers = headers;
	}
}

async function apiRequest(url, options = {}, { timeoutMs = 15000, debug = true } = {}) {
	const controller = new AbortController();
	const t = setTimeout(() => controller.abort(new Error('timeout')), timeoutMs);

	// options mutate 방지
	const finalOptions = {
		credentials: 'include',
		...options,
		headers: {
			Accept: 'application/json',
			...(options.headers || {}),
		},
		signal: controller.signal,
	};

	if (debug) console.info('🚀 request', url, finalOptions);

	try {
		const res = await fetch(url, finalOptions);
		if (debug) console.info('✅ response', res);
		
		if (res.status === 401 || res.status === 403) {
		    const cp = window.contextPath || ''; // 슬래시가 없는 상태 
		    const returnUrl = encodeURIComponent(window.location.href);
			console.info('returnUrl', returnUrl);
		    
		    window.location.href = cp + "/Auth/Login.html?returnUrl=" + returnUrl;
		    return;
		}

		// 204 / 205 / HEAD 등 바디 없음
		if (res.status === 204 || res.status === 205 || finalOptions.method === 'HEAD') {
			if (!res.ok) throw new ApiError(`HTTP ${res.status}`, { status: res.status, url });
			return null;
		}

		// content-type 따라 파싱
		const ct = res.headers.get('content-type') || '';
		const isJson = ct.includes('application/json');

		const data = isJson ? await res.json().catch(() => null) : await res.text().catch(() => null);

		if (!res.ok) {
			const msg =
				(data && typeof data === 'object' && (data.message || data.error)) ||
				(typeof data === 'string' && data.slice(0, 200)) ||
				`HTTP ${res.status}`;

			throw new ApiError(msg, { status: res.status, url, data, headers: res.headers });
		}

		return data;
	} catch (err) {
		// AbortError 등도 구분 가능
		if (err?.name === 'AbortError') {
			throw new ApiError('Request timeout', { status: 0, url });
		}
		throw err;
	} finally {
		clearTimeout(t);
	}
}

async function apiGetWithParam(baseUrl, params, opts) {

	// 1. URLSearchParams 객체 생성
	const searchParams = new URLSearchParams();

	// 2. 데이터 추가 (반복문 활용)
	Object.entries(params).forEach(([key, value]) => {
		if (Array.isArray(value)) {
			value.forEach(v => searchParams.append(key, v)); // 배열인 경우 같은 키로 여러 번 추가
		} else if (value !== null && value !== undefined) {
			searchParams.append(key, value);
		}
	});

	// 3. 완성된 쿼리 스트링 확인
	const queryString = searchParams.toString();

	// 4. 최종 URL 구성
	const finalUrl = `${baseUrl}?${queryString}`;

	return apiGet(finalUrl, opts);
}

async function apiGet(url, opts) {
	return apiRequest(url, { method: 'GET', ...opts });
}


async function apiPost(url, body, opts) {
	return apiRequest(url, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json', ...(opts?.headers || {}) },
		body: body === undefined ? undefined : JSON.stringify(body),
		...opts,
	});
}
