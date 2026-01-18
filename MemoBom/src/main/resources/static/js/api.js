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

async function apiRequest(url, options = {}, { timeoutMs = 15000, debug = false } = {}) {
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
