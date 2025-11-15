window.addEventListener("load", () => {
  const recruitListDiv = document.getElementById('recruitList');
  const paginationDiv = document.getElementById('pagination');
  const startDateInput = document.getElementById('startDate');
  const endDateInput = document.getElementById('endDate');
  const categorySelect = document.getElementById('category');
// 🔥 필터 변경 시 즉시 호출
  startDateInput.addEventListener("change", () => loadRecruits(0));
  endDateInput.addEventListener("change", () => loadRecruits(0));
  categorySelect.addEventListener("change", () => loadRecruits(0));
  if (!recruitListDiv || !paginationDiv) {
    console.error('recruitListDiv 또는 paginationDiv를 찾을 수 없습니다.');
    return;
  }
  window.addEventListener("globalSearch", async (e) => {
    const keyword = e.detail.keyword;

    const url = `/api/v1/recruit/search?title=${keyword}&content=${keyword}&page=0&size=5`;

    try {
      const response = await apiFetch(url);
      const json = await response.json();

      const pageData = json.data;

      renderList(pageData.content || []);
      renderPagination(pageData.totalPages || 1, 0);

    } catch (err) {
      console.error(err);
    }
  });
  const PAGE_SIZE = 5;
  let currentPage = 0;

  // ==============================
  // 🔥 safeFetchJson → apiFetch 버전
  // ==============================
  async function safeFetchJson(url, opts = {}) {
    const res = await apiFetch(url, opts);
    const json = await res.json();
    if (!res.ok) {
      throw new Error(json?.message || "서버 오류");
    }
    return json;
  }

  function buildQuery(page = 0) {
    const params = new URLSearchParams();
    params.append('page', page);
    params.append('size', PAGE_SIZE);

    if (startDateInput.value) {
      params.append('startDate', startDateInput.value);
    }
    if (endDateInput.value) {
      params.append('endDate', endDateInput.value);
    }
    if (categorySelect.value) {
      params.append('category', categorySelect.value);
    }

    return params.toString();
  }

  // ==============================
  // 🔥 전체 모집글 로드
  // ==============================
  async function loadRecruits(page = 0) {
    currentPage = page;
    showLoading();

    try {
      const qs = buildQuery(page);
      const json = await safeFetchJson(`/api/v1/recruit?${qs}`);
      const pageData = json?.data;

      if (!pageData) {
        return renderEmpty('조회 불가');
      }

      const recruits = pageData.content || [];
      const totalPages = pageData.totalPages ?? 0;

      if (recruits.length === 0) {
        renderEmpty("조회 가능한 모집 글이 없습니다.");
      } else {
        renderList(recruits);
      }

      renderPagination(totalPages, pageData.number ?? 0);
    } catch (err) {
      console.error(err);
      renderEmpty("데이터 로드 실패");
      renderPagination(0);
    }
  }

  // ==============================
  // UI Functions
  // ==============================
  function showLoading() {
    recruitListDiv.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">⏳</div>
        <div class="empty-state-text">로딩 중...</div>
      </div>`;
    paginationDiv.innerHTML = '';
  }

  function renderEmpty(msg) {
    recruitListDiv.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">📭</div>
        <div class="empty-state-text">${msg}</div>
      </div>`;
  }

  function renderList(recruits) {
    recruitListDiv.innerHTML = '';
    recruits.forEach(r => {
      const card = document.createElement('div');
      card.className = 'recruit-card';

      const gender = r.memberGender === 'M' ? '남자'
          : r.memberGender === 'F' ? '여자' : '';
      const ageText = r.memberAge ? `${r.memberAge}세 | ` : '';

      card.innerHTML = `
        <div class="card-content">
          <h3 class="card-title">${r.recruitTitle}</h3>
          <p class="card-description">${r.recruitContent}</p>
          <div class="card-info">
            <span class="info-label">여행 기간:</span>
            <span class="info-value">${r.startDate} ~ ${r.endDate}</span>
          </div>
          <div class="card-author">
            <span class="author-name">${r.memberName}</span>
            <span class="author-details">${ageText}${gender}</span>
          </div>
        </div>
      `;

      card.addEventListener('click', () => {
        window.location.href = `/recruit/${r.recruitId}`;
      });

      recruitListDiv.appendChild(card);
    });
  }

  // ==============================
  // 🔥 Pagination
  // ==============================
  function renderPagination(totalPages, current) {
    paginationDiv.innerHTML = '';
    if (totalPages <= 1) {
      return;
    }

    const createBtn = (label, pageIndex, disabled = false, active = false) => {
      const btn = document.createElement('button');
      btn.className = 'page-btn';
      btn.textContent = label;

      if (disabled) {
        btn.classList.add('disabled');
      }
      if (active) {
        btn.classList.add('active');
      }
      btn.disabled = disabled;

      btn.addEventListener('click', e => {
        e.preventDefault();
        if (!btn.disabled) {
          loadRecruits(pageIndex);
        }
      });

      return btn;
    };

    paginationDiv.appendChild(createBtn('«', 0, current === 0));
    paginationDiv.appendChild(
        createBtn('‹', Math.max(0, current - 1), current === 0));

    const maxButtons = 7;
    let start = Math.max(0, current - Math.floor(maxButtons / 2));
    let end = Math.min(totalPages - 1, start + maxButtons - 1);

    if (end - start < maxButtons - 1) {
      start = Math.max(0, end - (maxButtons - 1));
    }

    for (let i = start; i <= end; i++) {
      paginationDiv.appendChild(createBtn(i + 1, i, false, i === current));
    }

    paginationDiv.appendChild(
        createBtn('›', Math.min(totalPages - 1, current + 1),
            current === totalPages - 1));
    paginationDiv.appendChild(
        createBtn('»', totalPages - 1, current === totalPages - 1));
  }

  // ==============================
  // 🔥 특정 관광지 모집글 로드
  // ==============================
  async function loadRecruitListBySpot(touristSpotId, page = 0) {
    try {
      const res = await apiFetch(
          `/api/v1/recruit/touristSpot/${touristSpotId}?page=${page}&size=5`);
      const result = await res.json();

      const pageData = result.data;
      if (!pageData) {
        renderEmpty("조회 결과가 없습니다.");
        renderPagination(0);
        return;
      }

      const recruits = pageData.content || [];
      const totalPages = pageData.totalPages ?? 0;

      if (recruits.length === 0) {
        renderEmpty("이 관광지에는 모집글이 없습니다.");
      } else {
        renderList(recruits);
      }

      renderPagination(totalPages, pageData.number ?? 0);

    } catch (err) {
      console.error("특정 관광지 모집글 불러오기 실패", err);
      renderEmpty("데이터 로드 실패");
      renderPagination(0);
    }
  }

  // ⭐ 마커에서 호출할 수 있도록 전역(window)에 내보냄
  window.loadRecruitListBySpot = loadRecruitListBySpot;

  // 첫 로딩
  loadRecruits(0);
});