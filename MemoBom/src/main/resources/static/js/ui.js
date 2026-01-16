// 상단 닉네임 영역을 검색 입력으로 토글 (기획서: 🔍 클릭 시)
(function () {
  const toggle = document.getElementById('searchToggle');
  const nick = document.getElementById('nicknameArea');
  const search = document.getElementById('searchArea');
  const input = document.getElementById('searchInput');

  if (!toggle || !nick || !search || !input) return;

  function open() {
    nick.classList.add('hidden');
    search.classList.remove('hidden');
    input.focus();
  }
  function close() {
    nick.classList.remove('hidden');
    search.classList.add('hidden');
    input.value = '';
  }

  toggle.addEventListener('click', () => {
    const isOpen = !search.classList.contains('hidden');
    if (isOpen) close(); else open();
  });

  // 바깥 클릭 시 닫기
  document.addEventListener('click', (e) => {
    const within = search.contains(e.target) || toggle.contains(e.target);
    if (!within && !search.classList.contains('hidden')) {
      close();
    }
  });

  // Enter 시 검색 페이지로 이동
  input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      const key = encodeURIComponent(input.value.trim());
      if (!key) return;
      window.location.href = `/Memo/Search.html?key=${key}`;
    }
  });
})();
