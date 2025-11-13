document.addEventListener("DOMContentLoaded", async () => {
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

  // ✅ 페이지네이션
  function renderPagination(containerId, currentPage, totalPages,
      onPageChange) {
    const container = document.getElementById(containerId);
    container.innerHTML = "";
    if (totalPages <= 1) {
      return;
    }

    const createBtn = (html, disabled, active, onClick) => {
      const btn = document.createElement("button");
      btn.innerHTML = html;
      if (disabled) {
        btn.disabled = true;
      }
      if (active) {
        btn.classList.add("active");
      }
      btn.addEventListener("click", onClick);
      return btn;
    };

    container.appendChild(
        createBtn("&laquo;", currentPage === 0, false, () => onPageChange(0)));
    container.appendChild(createBtn("&lsaquo;", currentPage === 0, false,
        () => onPageChange(currentPage - 1)));

    const maxVisible = 5;
    let start = Math.max(0, currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(totalPages, start + maxVisible);
    if (end - start < maxVisible) {
      start = Math.max(0, end - maxVisible);
    }

    for (let i = start; i < end; i++) {
      container.appendChild(
          createBtn(i + 1, false, i === currentPage, () => onPageChange(i)));
    }

    container.appendChild(
        createBtn("&rsaquo;", currentPage >= totalPages - 1, false,
            () => onPageChange(currentPage + 1)));
    container.appendChild(
        createBtn("&raquo;", currentPage >= totalPages - 1, false,
            () => onPageChange(totalPages - 1)));
  }

  // ✅ 승인/거절 API
  async function updateStatus(recruitId, memberNo, action) {
    try {
      const res = await fetch(`/api/v1/approval/${action}`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({recruitId, memberNo}),
      });
      const result = await res.json();
      if (res.ok) {
        alert(result.message || "처리 완료");
        await loadMyRecruitApprovals();
      } else {
        alert(result.message || "처리 실패");
      }
    } catch (err) {
      console.error(`❌ ${action} 실패:`, err);
    }
  }

  // ✅ 삭제 API
  async function deleteApproval(approvalId) {
    if (!confirm("정말 삭제하시겠습니까?")) {
      return;
    }
    try {
      const res = await fetch(`/api/v1/approval/${approvalId}`, {
        method: "DELETE",
        headers: {Authorization: `Bearer ${token}`},
      });
      const result = await res.json();
      if (res.ok) {
        alert(result.message || "삭제 완료");
        await loadMyRecruitApprovals();
        await loadMyApprovals();
      } else {
        alert(result.message || "삭제 실패");
      }
    } catch (err) {
      console.error("❌ 삭제 실패:", err);
    }
  }

  // ✅ 삭제 버튼 이벤트 위임 (두 테이블 모두 커버)
  document.addEventListener("click", (e) => {
    const btn = e.target.closest(".btn-delete");
    if (btn && btn.dataset.id) {
      deleteApproval(btn.dataset.id);
    }
  });

  // ✅ 내 모집글 승인 관리
  async function loadMyRecruitApprovals() {
    try {
      const res = await fetch(`/api/v1/approval/MyRecruit`, {
        headers: {Authorization: `Bearer ${token}`},
      });
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
            <td><button class="tourist-btn" data-id="${item.recruitId}">${item.touristSpot}</button></td>
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
          (page) => {
            myRecruitPage = page;
            loadMyRecruitApprovals();
          });
    } catch (err) {
      console.error("❌ 내 모집글 조회 실패:", err);
      myRecruitTableBody.innerHTML = `<tr><td colspan="5" class="empty">데이터 불러오기 실패</td></tr>`;
    }
  }

  // ✅ 내가 신청한 모집
  async function loadMyApprovals() {
    try {
      const res = await fetch(
          `/api/v1/approval/My?page=${myApplyPage}&size=${size}`, {
            headers: {Authorization: `Bearer ${token}`},
          });
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
            <td><button class="tourist-btn" data-id="${item.recruitId}">${item.touristSpot}</button></td>
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

      renderPagination("myApplyPagination", myApplyPage, totalPages, (page) => {
        myApplyPage = page;
        loadMyApprovals();
      });
    } catch (err) {
      console.error("❌ 내가 신청한 모집 조회 실패:", err);
      myApplyTableBody.innerHTML = `<tr><td colspan="5" class="empty">데이터 불러오기 실패</td></tr>`;
    }
  }

  // ✅ 첫 로딩
  await loadMyRecruitApprovals();
  await loadMyApprovals();
});