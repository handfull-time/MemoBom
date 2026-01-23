// ✅ 중복 로드 방지 (fragment/include가 여러 번 되어도 안전)
if (!customElements.get('pp-toggle')) {

	// 사용 예)  <pp-toggle name="marketing" off-text="False" on-text="True" checked disabled></pp-toggle>

	class PpToggle extends HTMLElement {
		static get observedAttributes() {
			return ['checked', 'disabled', 'on-text', 'off-text', 'name'];
		}

		connectedCallback() {
			if (this.dataset.inited) return;
			this.dataset.inited = '1';

			// 초기 렌더 (label은 바깥 레이아웃에서 처리하는 방식)
			this.render();

			// 초기 상태 반영
			this.syncUI(this.checked);

			// 이벤트 연결
			const btn = this.querySelector('button[data-role="switch"]');
			btn.addEventListener('click', () => {
				if (this.disabled) return;
				this.checked = !this.checked; // setter에서 syncUI까지 처리

				this.dispatchEvent(new CustomEvent('change', {
					bubbles: true,
					detail: {
						name: this.name,
						checked: this.checked,
						value: this.value
					}
				}));
			});

			btn.addEventListener('keydown', (e) => {
				if (this.disabled) return;
				if (e.key === ' ' || e.key === 'Enter') {
					e.preventDefault();
					btn.click();
				}
			});
		}

		attributeChangedCallback() {
			// 외부에서 속성을 바꿔도 UI가 따라오도록
			if (!this.dataset.inited) return;
			this.syncUI(this.checked);
		}

		// --- props ---
		get name() { return this.getAttribute('name') ?? ''; }
		get disabled() { return this.hasAttribute('disabled'); }

		get checked() { return this.hasAttribute('checked'); }
		set checked(val) {
			this.toggleAttribute('checked', !!val);
			this.syncUI(!!val);
		}

		get value() { return this.checked ? 'Y' : 'N'; }

		get onText() { return this.getAttribute('on-text') ?? 'ON'; }
		get offText() { return this.getAttribute('off-text') ?? 'OFF'; }

		// --- UI ---
		render() {
			this.innerHTML = `
	           <button
	             type="button"
	             data-role="switch"
	             role="switch"
	             aria-checked="false"
	             class="relative inline-flex h-7 w-16 items-center rounded-full
	                    ring-1 ring-black/5 transition-colors duration-300
	                    focus:outline-none focus:ring-2 focus:ring-green-500/40"
	           >
	             <span class="onTxt absolute left-1.5 text-[11px] font-semibold text-white transition-opacity duration-200 opacity-0">${this.escape(this.onText)}</span>
	             <span class="offTxt absolute right-1.5 text-[11px] font-semibold text-gray-900 transition-opacity duration-200 opacity-100">${this.escape(this.offText)}</span>
	             <span class="knob absolute left-1 inline-block h-5 w-5 rounded-full bg-white shadow-sm transform transition-transform duration-300 translate-x-0"></span>
	           </button>

	           <input type="hidden" id="${this.escape(this.name)}" name="${this.escape(this.name)}" value="N">
	         `;
		}

		syncUI(on) {
			const btn = this.querySelector('button[data-role="switch"]');
			const knob = this.querySelector('.knob');
			const onTxt = this.querySelector('.onTxt');
			const offTxt = this.querySelector('.offTxt');
			const hidden = this.querySelector('input[type="hidden"]');

			// 상태/접근성
			btn.setAttribute('aria-checked', String(on));

			// 색상
			btn.classList.toggle('bg-green-500', on);
			btn.classList.toggle('bg-gray-400', !on);

			// 손잡이 이동 (w-14 기준)
			knob.classList.toggle('translate-x-9', on);
			knob.classList.toggle('translate-x-0', !on);

			// 텍스트 show/hide
			onTxt.classList.toggle('opacity-100', on);
			onTxt.classList.toggle('opacity-0', !on);

			offTxt.classList.toggle('opacity-0', on);
			offTxt.classList.toggle('opacity-100', !on);

			// disabled 처리
			if (this.disabled) {
				btn.setAttribute('aria-disabled', 'true');
				btn.classList.add('opacity-60', 'cursor-not-allowed');
			} else {
				btn.removeAttribute('aria-disabled');
				btn.classList.remove('opacity-60', 'cursor-not-allowed');
			}

			// hidden input value
			hidden.value = on ? 'Y' : 'N';
		}

		escape(s) {
			return String(s)
				.replaceAll('&', '&amp;')
				.replaceAll('<', '&lt;')
				.replaceAll('>', '&gt;')
				.replaceAll('"', '&quot;')
				.replaceAll("'", '&#39;');
		}
	}

	customElements.define('pp-toggle', PpToggle);
}