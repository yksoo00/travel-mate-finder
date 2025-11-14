// =======================
//  ⭐ 공통 fetch wrapper ⭐
// =======================

async function apiFetch(url, options = {}) {
  let accessToken = localStorage.getItem("accessToken");
  let refreshToken = localStorage.getItem("refreshToken");

  options.headers = options.headers || {};

  // FormData 아닌 경우 Content-Type 자동 추가
  if (!(options.body instanceof FormData)) {
    options.headers["Content-Type"] = "application/json";
  }

  options.headers["Authorization"] = `Bearer ${accessToken}`;

  // --- 1차 요청 ---
  let response = await fetch(url, options);

  if (response.status === 401) {
    console.warn("⚠ Access Token 만료 → Refresh Token 사용 시도");

    const refreshResponse = await fetch("/auth/refresh", {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${refreshToken}`
      }
    });

    if (refreshResponse.status === 200) {
      const data = await refreshResponse.json();

      const newAccess = data.data.accessToken;
      const newRefresh = data.data.refreshToken;

      console.log("🔄 새로운 토큰 재발급 완료");

      // 저장
      localStorage.setItem("accessToken", newAccess);
      localStorage.setItem("refreshToken", newRefresh);

      // 원래 요청 재시도
      options.headers["Authorization"] = `Bearer ${newAccess}`;
      return await fetch(url, options);
    }

    console.warn("❌ Refresh Token도 만료됨");
    alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
    window.location.href = "/loginForm";
    return;
  }

  return response;
}