// ======================= 전역 설정 =======================
document.addEventListener("DOMContentLoaded", async () => {

  // ❗ 우선 로그인 체크
  const token = localStorage.getItem("accessToken");
  if (!token) {
    alert("로그인이 필요합니다.");
    window.location.href = "/loginForm";
    return;
  }

  const size = 3;
  let myRecruitPage = 0;
  let myApplyPage = 0;

  const myRecruitTableBody = document.querySelector("#myRecruitTable tbody");
  const myApplyTableBody = document.querySelector("#appliedRecruitTable tbody");

  // ====================== 페이징 ======================
  function renderPagination(containerId, currentPage, totalPages,
      onPageChange) {
    const container = document.getElementById(containerId);
    container.innerHTML = "";

    if (totalPages <= 1) {
      return;
    }

    const btn = (html, disabled, active, onClick) => {
      const el = document.createElement("button");
      el.innerHTML = html;
      if (disabled) {
        el.disabled = true;
      }
      if (active) {
        el.classList.add("active");
      }
      el.addEventListener("click", onClick);
      return el;
    };

    container.appendChild(
        btn("&laquo;", currentPage === 0, false, () => onPageChange(0)));
    container.appendChild(btn("&lsaquo;", currentPage === 0, false,
        () => onPageChange(currentPage - 1)));

    const maxVisible = 5;
    let start = Math.max(0, currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(totalPages, start + maxVisible);
    if (end - start < maxVisible) {
      start = Math.max(0, end - maxVisible);
    }

    for (let i = start; i < end; i++) {
      container.appendChild(
          btn(i + 1, false, i === currentPage, () => onPageChange(i)));
    }

    container.appendChild(btn("&rsaquo;", currentPage >= totalPages - 1, false,
        () => onPageChange(currentPage + 1)));
    container.appendChild(btn("&raquo;", currentPage >= totalPages - 1, false,
        () => onPageChange(totalPages - 1)));
  }

  // ================= 승 인 / 거 절 =================
  async function updateStatus(recruitId, memberNo, action) {
    try {
      const res = await apiFetch(`/api/v1/approval/${action}`, {
        method: "POST",
        body: JSON.stringify({recruitId, memberNo}),
      });
      const result = await res.json();

      if (res.ok) {
        alert(result.message || "처리 완료");
        loadMyRecruitApprovals();
      } else {
        alert(result.message || "처리 실패");
      }
    } catch (err) {
      console.error(`❌ ${action} 실패:`, err);
    }
  }

  // ================= 삭제 =================
  async function deleteApproval(approvalId) {
    if (!confirm("정말 삭제하시겠습니까?")) {
      return;
    }

    try {
      const res = await apiFetch(`/api/v1/approval/${approvalId}`, {
        method: "DELETE",
      });
      const result = await res.json();

      if (res.ok) {
        alert(result.message || "삭제 완료");
        loadMyRecruitApprovals();
        loadMyApprovals();
      } else {
        alert(result.message || "삭제 실패");
      }
    } catch (err) {
      console.error("❌ 삭제 실패:", err);
    }
  }

  // 삭제 버튼 이벤트 위임
  document.addEventListener("click", (e) => {
    const btn = e.target.closest(".btn-delete");
    if (btn && btn.dataset.id) {
      deleteApproval(btn.dataset.id);
    }
  });

  // =================== 내 모집글 승인 리스트 ===================
  async function loadMyRecruitApprovals() {
    try {
      const res = await apiFetch(`/api/v1/approval/MyRecruit`);
      const result = await res.json();
      const list = result.data?.content || [];
      const totalPages = result.data?.totalPages ?? 1;

      myRecruitTableBody.innerHTML = "";

      if (list.length === 0) {
        myRecruitTableBody.innerHTML = `<tr><td colspan="5" class="empty">신청 내역이 없습니다.</td></tr>`;
      } else {
        list.forEach((item, index) => {
          const row = document.createElement("tr");
          row.innerHTML = `
            <td>${myRecruitPage * size + index + 1}</td>
            <td>${item.memberNickName}</td>
            <td>
              <button class="tourist-btn" data-id="${item.recruitId}">
                ${item.touristSpot}
              </button>
            </td>
            <td>
              <button class="btn btn-approve">승인</button>
              <button class="btn btn-reject">거절</button>
            </td>
            <td><button class="btn btn-delete" data-id="${item.approvalId}">제거</button></td>
          `;

          row.querySelector(".tourist-btn").addEventListener("click", () =>
              window.location.href = `/recruit/${item.recruitId}`
          );

          row.querySelector(".btn-approve").addEventListener("click", () =>
              updateStatus(item.recruitId, item.memberNo, "approved")
          );

          row.querySelector(".btn-reject").addEventListener("click", () =>
              updateStatus(item.recruitId, item.memberNo, "rejected")
          );

          myRecruitTableBody.appendChild(row);
        });
      }

      renderPagination("myRecruitPagination", myRecruitPage, totalPages,
          (p) => {
            myRecruitPage = p;
            loadMyRecruitApprovals();
          });

    } catch (err) {
      console.error("❌ 내 모집글 조회 실패:", err);
      myRecruitTableBody.innerHTML = `<tr><td colspan="5" class="empty">데이터 불러오기 실패</td></tr>`;
    }
  }

  // =================== 내가 신청한 모집 ===================
  async function loadMyApprovals() {
    try {
      const res = await apiFetch(
          `/api/v1/approval/My?page=${myApplyPage}&size=${size}`);
      const result = await res.json();
      const list = result.data?.content || [];
      const totalPages = result.data?.totalPages ?? 1;

      myApplyTableBody.innerHTML = "";

      if (list.length === 0) {
        myApplyTableBody.innerHTML = `<tr><td colspan="5" class="empty">신청한 모집이 없습니다.</td></tr>`;
      } else {
        list.forEach((item, index) => {
          const row = document.createElement("tr");
          row.innerHTML = `
            <td>${myApplyPage * size + index + 1}</td>
            <td>${item.memberNickName}</td>
            <td>
              <button class="tourist-btn" data-id="${item.recruitId}">
                ${item.touristSpot}
              </button>
            </td>
            <td>
              <span class="status-tag ${item.status.toLowerCase()}">${item.status}</span>
            </td>
            <td><button class="btn btn-delete" data-id="${item.approvalId}">제거</button></td>
          `;

          row.querySelector(".tourist-btn").addEventListener("click", () =>
              window.location.href = `/recruit/${item.recruitId}`
          );

          myApplyTableBody.appendChild(row);
        });
      }

      renderPagination("myApplyPagination", myApplyPage, totalPages, (p) => {
        myApplyPage = p;
        loadMyApprovals();
      });

    } catch (err) {
      console.error("❌ 내가 신청한 모집 조회 실패:", err);
      myApplyTableBody.innerHTML = `<tr><td colspan="5" class="empty">데이터 불러오기 실패</td></tr>`;
    }
  }

  // =================== 첫 로딩 ===================
  await loadMyRecruitApprovals();
  await loadMyApprovals();
});