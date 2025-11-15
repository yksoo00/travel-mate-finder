document.addEventListener("DOMContentLoaded", async () => {
  const authArea = document.getElementById("authArea");
  const token = localStorage.getItem("accessToken");
  const memberId = localStorage.getItem("memberId");

  // ===========================
  // 🔍 전역 검색 이벤트 설정
  // ===========================
  const searchInput = document.querySelector(".search-input");
  const searchIcon = document.querySelector(".search-icon");

  function emitSearchEvent() {
    const keyword = searchInput.value.trim();
    if (!keyword) {
      return;
    }

    // 🔥 모든 페이지가 들을 수 있는 전역 검색 이벤트
    const event = new CustomEvent("globalSearch", {detail: {keyword}});
    window.dispatchEvent(event);
  }

  if (searchInput) {
    // 엔터 입력 → 검색 실행
    searchInput.addEventListener("keypress", (e) => {
      if (e.key === "Enter") {
        emitSearchEvent();
      }
    });

    // 검색 아이콘 클릭 → 검색 실행
    searchIcon?.addEventListener("click", emitSearchEvent);
  }

  // ===========================
  // 🔐 로그인 상태 체크
  // ===========================
  if (token && memberId) {
    authArea.innerHTML = `
      <div style="display:flex; align-items:center; gap:10px;">
        <span style="color:#00BFFF; font-weight:600;">${memberId}님, 환영합니다 🎉</span>
        <button id="logoutBtn" class="btn-register" style="background:#ff6b6b;">로그아웃</button>
      </div>
    `;

    // 🔐 로그아웃 기능 (apiFetch 적용)
    document.getElementById("logoutBtn").addEventListener("click", async () => {
      try {
        const res = await apiFetch("/auth/logout", {
          method: "GET"
        });

        const result = await res.json();

        if (res.ok) {
          alert(result.message || "로그아웃 되었습니다.");

          // 🔥 토큰 제거
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
          localStorage.removeItem("memberId");

          window.location.href = "/loginForm";
        } else {
          alert(result.message || "로그아웃 실패");
        }
      } catch (err) {
        console.error(err);
        alert("로그아웃 중 오류가 발생했습니다.");
      }
    });

  } else {
    // 비로그인 UI
    authArea.innerHTML = `
      <a href="/signin" class="btn-signin">Sign in →</a>
      <a href="/register" class="btn-register">Register</a>
    `;
  }
});