function safeText(v, fallback = "") {
	const s = (v === null || v === undefined) ? "" : String(v);
	return s.trim() || fallback;
}

function safeNum(v) {
	const n = Number(v);
	return Number.isFinite(n) ? n : 0;
}

// color: null이면 흰색
function normalizeColor(v) {
	const s = safeText(v, "");
	return s ? s : "#ffffff";
}


function getFormObject(formId) {
	const form = document.getElementById(formId);
	if (!form) {
		throw new Error(`Form with ID "${formId}" not found.`);
	}

	const result = {};
	const elements = Array.from(form.elements);

	for (const el of elements) {
		if (!el.name || el.disabled) continue;

		const { name, type, value, checked, tagName } = el;
		const isCheckbox = type === 'checkbox';
		const isRadio = type === 'radio';

		if (isCheckbox) {
			if (checked) _setDeepValue(result, name, value, true);
		} else if (isRadio) {
			if (checked) _setDeepValue(result, name, value);
		} else if (tagName === 'SELECT' && el.multiple) {
			const selectedValues = Array.from(el.selectedOptions).map(opt => opt.value);
			_setDeepValue(result, name, selectedValues, true);
		} else {
			_setDeepValue(result, name, value);
		}
	}

	console.log(JSON.stringify(result, null, 4));
	return result;

	// 👇 내부 유틸 함수
	function _setDeepValue(obj, path, value, forceArray = false) {
		const keys = path.split('.');
		let current = obj;

		keys.forEach((key, index) => {
			const isLast = index === keys.length - 1;

			if (isLast) {
				if (key in current) {
					if (!Array.isArray(current[key])) {
						current[key] = [current[key]];
					}
					current[key].push(value);
				} else {
					current[key] = forceArray ? [value] : value;
				}
			} else {
				if (!(key in current) || typeof current[key] !== 'object') {
					current[key] = {};
				}
				current = current[key];
			}
		});
	}
}

/* MemoBom A안 JS: API 헬퍼 + Board 무한스크롤 */

window.MemoBomApi = {
	async getJson(url) {
		const res = await fetch(url, { credentials: 'include' });
		if (!res.ok) throw new Error(`HTTP ${res.status}`);
		return await res.json();
	},
	async postJson(url, body) {
		const res = await fetch(url, {
			method: 'POST',
			credentials: 'include',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(body || {})
		});
		if (!res.ok) {
			// 서버가 401/404 등을 반환할 때도 body가 json이면 읽어보기
			const text = await res.text().catch(() => '');
			try { return JSON.parse(text); } catch { return { code: res.status, message: text || `HTTP ${res.status}` }; }
		}
		return await res.json();
	},
	fileToBase64(file) {
		return new Promise((resolve, reject) => {
			const reader = new FileReader();
			reader.onload = () => resolve(String(reader.result || ''));
			reader.onerror = reject;
			reader.readAsDataURL(file);
		});
	}
};

window.MemoBomBoard = {
	mount({ listElId, sentinelElId, boardUrl }) {
		const listEl = document.getElementById(listElId);
		const sentinel = document.getElementById(sentinelElId);
		if (!listEl || !sentinel) return;

		let pageNo = 0;
		let loading = false;
		let done = false;

		const baseUrl = (() => {
			// boardUrl이 이미 pageNo=0을 포함할 수 있어서 보정
			const u = new URL(boardUrl, location.origin);
			if (u.searchParams.has('pageNo')) {
				u.searchParams.set('pageNo', String(pageNo));
			} else {
				u.searchParams.set('pageNo', String(pageNo));
			}
			return u;
		})();

		const renderMemo = (m) => {
			const memoNo = m.memoNo;
			const el = document.createElement('article');
			el.className = 'border rounded-xl p-4 space-y-2';
			el.innerHTML = `
        <div class="flex items-center justify-between text-xs text-slate-500">
          <div class="flex items-center gap-2">
            <img class="w-7 h-7 rounded-full" src="${m.profileImg || '/img/profile-placeholder.png'}" alt="profile" />
            <span>${escapeHtml(m.nickname || '작성자')}</span>
          </div>
          <time>${escapeHtml(m.date || '')}</time>
        </div>
        <div class="text-sm leading-6 select-none">${escapeHtml(m.memo || '')}</div>
        <div class="flex items-center justify-between">
          <a class="inline-flex items-center gap-2 text-xs" href="/Topic/index.html?topicNo=${encodeURIComponent(m.topic?.topicNo ?? '')}">
            <span class="text-lg">🚩</span>
            <span>${escapeHtml(m.topic?.name || 'Topic')}</span>
          </a>
          <button class="text-lg" type="button" title="Scrap" data-action="scrap" data-memo-no="${memoNo}" data-scrap="${!!m.scrap}">📌</button>
        </div>
        <div class="flex items-center justify-between gap-2 pt-2 border-t">
          <div class="flex items-center gap-2 text-lg">
            <button type="button" data-action="emotion" data-memo-no="${memoNo}" data-emotion="joy">😎</button>
            <button type="button" data-action="emotion" data-memo-no="${memoNo}" data-emotion="pleasure">🙂</button>
            <button type="button" data-action="emotion" data-memo-no="${memoNo}" data-emotion="normal">😐</button>
            <button type="button" data-action="emotion" data-memo-no="${memoNo}" data-emotion="anger">😠</button>
            <button type="button" data-action="emotion" data-memo-no="${memoNo}" data-emotion="sadness">😢</button>
            <button type="button" data-action="toggleComments" data-memo-no="${memoNo}">💬</button>
          </div>
          <div class="text-xs text-slate-500">${formatCounts(m.emotions)}</div>
        </div>
        <div class="comments hidden pt-3 space-y-3" data-memo-no="${memoNo}">
          <div class="space-y-2" data-role="commentList"></div>
          <div class="flex items-center gap-2">
            <input class="flex-1 border rounded-lg px-3 h-10 text-sm" placeholder="댓글 작성" data-role="commentInput" />
            <button class="h-10 px-3 rounded-lg bg-slate-900 text-white" data-action="sendComment" data-memo-no="${memoNo}">📝</button>
          </div>
        </div>
      `;
			return el;
		};

		const loadNext = async () => {
			if (loading || done) return;
			loading = true;
			sentinel.textContent = '불러오는 중…';

			const u = new URL(baseUrl.toString());
			u.searchParams.set('pageNo', String(pageNo));

			try {
				const data = await MemoBomApi.getJson(u.toString());
				const list = data?.list || [];
				if (!Array.isArray(list) || list.length === 0) {
					done = true;
					sentinel.textContent = '더 이상 내용이 없습니다.';
					return;
				}
				list.forEach(m => listEl.appendChild(renderMemo(m)));
				pageNo += 1;
				bindActions(listEl);
				sentinel.textContent = '스크롤하면 더 불러옵니다';
			} catch (e) {
				sentinel.textContent = '불러오기 실패';
				console.error(e);
			} finally {
				loading = false;
			}
		};

		const io = new IntersectionObserver((entries) => {
			entries.forEach(e => {
				if (e.isIntersecting) loadNext();
			});
		}, { rootMargin: '300px' });

		io.observe(sentinel);
		// 첫 로드
		loadNext();

		function bindActions(root) {
			if (root.dataset.bound) return;
			root.dataset.bound = '1';

			root.addEventListener('click', async (ev) => {
				const t = ev.target;
				if (!(t instanceof HTMLElement)) return;

				const action = t.getAttribute('data-action');
				if (!action) return;

				if (action === 'toggleComments') {
					const memoNo = Number(t.getAttribute('data-memo-no'));
					const box = root.querySelector(`.comments[data-memo-no="${memoNo}"]`);
					if (!box) return;
					box.classList.toggle('hidden');
					if (!box.classList.contains('hidden')) {
						await loadComments(box, memoNo);
					}
				}

				if (action === 'sendComment') {
					const memoNo = Number(t.getAttribute('data-memo-no'));
					const box = root.querySelector(`.comments[data-memo-no="${memoNo}"]`);
					const input = box?.querySelector('[data-role="commentInput"]');
					const text = (input?.value || '').trim();
					if (!text) return;
					const res = await MemoBomApi.postJson('/Memo/WriteComment.json', { memoNo, memo: text });
					if (res?.code === '0' || res?.code === 0) {
						input.value = '';
						await loadComments(box, memoNo, true);
					} else {
						alert(res?.message || '댓글 저장 실패');
					}
				}

				if (action === 'scrap') {
					const memoNo = Number(t.getAttribute('data-memo-no'));
					const res = await MemoBomApi.postJson('/Memo/Scrap.json', { memoNo });
					if (res?.code === '0' || res?.code === 0) {
						// 기획: scrap true면 빨간색, false면 흰색
						const on = !!res.scrap;
						t.textContent = on ? '📌' : '📌';
						t.style.filter = on ? 'hue-rotate(330deg) saturate(3)' : '';
					} else {
						alert(res?.message || '스크랩 실패');
					}
				}

				if (action === 'emotion') {
					const memoNo = Number(t.getAttribute('data-memo-no'));
					const emotion = String(t.getAttribute('data-emotion') || '');
					const res = await MemoBomApi.postJson('/Memo/Emotion.json', { memoNo, emotion });
					if (!(res?.code === '0' || res?.code === 0)) {
						alert(res?.message || '이모션 실패');
					}
				}
			});
		}

		async function loadComments(box, memoNo, clearFirst = false) {
			const listEl = box.querySelector('[data-role="commentList"]');
			if (!listEl) return;
			if (clearFirst) listEl.innerHTML = '';
			// 단순: 1페이지 로드만(기획대로라면 무한스크롤 가능)
			const data = await MemoBomApi.getJson(`/Memo/Comments.json?pageNo=0&memoNo=${encodeURIComponent(memoNo)}`);
			const list = data?.list || [];
			listEl.innerHTML = '';
			for (const c of list) {
				const row = document.createElement('div');
				row.className = 'border rounded-xl p-3';
				row.innerHTML = `
          <div class="flex items-center justify-between text-xs text-slate-500">
            <div class="flex items-center gap-2"><span>🙂</span><span>${escapeHtml(c.nickname || '작성자')}</span></div>
            <time>${escapeHtml(c.date || '')}</time>
          </div>
          <div class="pt-2 text-sm select-none">${escapeHtml(c.comment || '')}</div>
          <div class="pt-2 flex items-center justify-between">
            <div class="flex items-center gap-2 text-lg">
              <button type="button" data-action="emotionComment" data-comment-no="${c.commentNo}" data-emotion="joy">😎</button>
              <button type="button" data-action="emotionComment" data-comment-no="${c.commentNo}" data-emotion="pleasure">🙂</button>
              <button type="button" data-action="emotionComment" data-comment-no="${c.commentNo}" data-emotion="normal">😐</button>
              <button type="button" data-action="emotionComment" data-comment-no="${c.commentNo}" data-emotion="anger">😠</button>
              <button type="button" data-action="emotionComment" data-comment-no="${c.commentNo}" data-emotion="sadness">😢</button>
            </div>
            <div class="text-xs text-slate-500">${formatCounts(c.emotions)}</div>
          </div>
        `;
				listEl.appendChild(row);
			}

			// 댓글 이모션
			listEl.addEventListener('click', async (ev) => {
				const t = ev.target;
				if (!(t instanceof HTMLElement)) return;
				if (t.getAttribute('data-action') !== 'emotionComment') return;
				const commentNo = Number(t.getAttribute('data-comment-no'));
				const emotion = String(t.getAttribute('data-emotion') || '');
				const res = await MemoBomApi.postJson('/Memo/Emotion.json', { commentNo, emotion });
				if (!(res?.code === '0' || res?.code === 0)) alert(res?.message || '실패');
			}, { once: true });
		}

		function formatCounts(emotions) {
			const e = emotions || {};
			return `${e.joy ?? 0} ${e.anger ?? 0} ${e.sadness ?? 0} ${e.pleasure ?? 0} ${e.normal ?? 0}`;
		}

		function escapeHtml(s) {
			return String(s)
				.replaceAll('&', '&amp;')
				.replaceAll('<', '&lt;')
				.replaceAll('>', '&gt;')
				.replaceAll('"', '&quot;')
				.replaceAll("'", '&#39;');
		}
	}
};
