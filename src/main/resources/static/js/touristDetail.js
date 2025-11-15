// =====================================================
// 🚀 1) 전역 변수 선언 (중복 금지)
// =====================================================
let touristId = null;
let reviewListContainer = null;
let paginationContainer = null;

const DEFAULT_PROFILE_IMG = '/img/default-avatar.png';

let currentMemberId = null;


// =====================================================
// 🚀 2) DOMContentLoaded — touristId 주입 및 초기화
// =====================================================
document.addEventListener("DOMContentLoaded", async() => {

    const container = document.getElementById('touristDetailContainer');
    touristId = container?.dataset.touristId ?? null;
    console.log("console.log(touristId)")
    console.log(touristId)
    reviewListContainer = document.getElementById('review-list');
    paginationContainer = document.getElementById('pagination-container');

    console.log("🔥 touristId:", touristId);

    if (!touristId) {
        console.error("❌ touristId가 존재하지 않습니다.");
        return;
    }

    // 2. 서버에서 주입받는 대신, API로 직접 사용자 ID를 가져옵니다.
    try {
        const userId = await getCurrentMemberId(); //
        if (userId) {
            currentMemberId = String(userId).trim();
            console.log("🔥 currentMemberId (API 응답):", currentMemberId);
        } else {
            currentMemberId = '';
            console.log("🔥 currentMemberId (API 응답):", "NULL (비로그인)");
        }
    } catch (error) {
        console.warn("사용자 ID 로드 실패 (비로그인 상태일 수 있음):", error);
        currentMemberId = '';
    }

    // 상세 + 리뷰 로드
    fetchDetails(touristId);
    fetchReviewsAndRender(0);

    // 버튼 이벤트 연결
    setupButtonListeners();
});

// =====================================================
// 🚀 3) 리뷰 목록 가져오기 + 렌더링
// =====================================================
async function fetchReviewsAndRender(page = 0) {
    if (!touristId) {
        return;
    }

    reviewListContainer.innerHTML =
        `<div style="text-align:center;padding:20px;">리뷰를 불러오는 중입니다...</div>`;
    paginationContainer.innerHTML = '';

    try {
        const response = await apiFetch(
            `/api/v1/reviews/${touristId}?page=${page}&size=5`);
        console.log("🔎 리뷰 요청 URL:",
            `/api/v1/reviews/${touristId}?page=${page}&size=5`);
        if (!response.ok) {
            const errorText = await response.text();
            console.error("Status:", response.status);
            console.error("Response:", errorText);
        }

        const data = await response.json();
        const reviews = data.data?.content || [];

        renderReviews(reviews);
        renderPagination({
            currentPage: data.data.number + 1,
            totalPages: data.data.totalPages
        });

    } catch (error) {
        console.error(error);
        reviewListContainer.innerHTML =
            `<div style="color:red;text-align:center;padding:20px;">리뷰 불러오기 실패</div>`;
    }
}

// =====================================================
// 🚀 4) 리뷰 렌더링
// =====================================================
function renderReviews(reviews) {
    reviewListContainer.innerHTML = '';

    if (reviews.length === 0) {
        reviewListContainer.innerHTML =
            `<div style="text-align:center;padding:20px;color:#777;">아직 리뷰가 없습니다.</div>`;
        return;
    }

    reviews.forEach(review => {

        // ReviewResDTO에서 받은 memberId를 문자열로 변환하고 공백 제거
        const reviewAuthorId = String(review.memberId || '').trim();

        // 전역 변수 currentMemberId를 사용하고 공백 제거 (안전 확보)
        const currentUserId = String(currentMemberId || '').trim();

        // 두 값이 일치하고, 현재 사용자 ID가 비어있지 않은지 확인
        const isAuthor = currentUserId.length > 0 && reviewAuthorId === currentUserId;

        // 💡 디버깅 로그: 이 로그를 꼭 확인해 주세요!
        console.log(`Review ID: ${review.id}, Author ID: [${reviewAuthorId}], Current User ID: [${currentUserId}], Is Author: ${isAuthor}`);

        const isModified = review.updatedAt && review.updatedAt
            !== review.createdAt;
        const dateSource = isModified ? review.updatedAt : review.createdAt;
        const dateObj = new Date(dateSource);

        const formatted = `${dateObj.getFullYear()}-${String(
            dateObj.getMonth() + 1).padStart(2, '0')}-${String(
            dateObj.getDate()).padStart(2, '0')}
                       ${String(dateObj.getHours()).padStart(2, '0')}:${String(
            dateObj.getMinutes()).padStart(2, '0')}`;

        const modifiedBadge = isModified
            ? ' <span class="review-modified-badge">(수정됨)</span>' : '';

        const profileImg = review.profImg || DEFAULT_PROFILE_IMG;

        const actionsHtml = isAuthor ? `
          <div class="review-actions">
            <button class="btn-review-edit" onclick="handleReviewEdit(${review.id})">수정</button>
            <button class="btn-review-delete" onclick="handleReviewDelete(${review.id})">삭제</button>
          </div>
        ` : `<div class="review-actions"></div>`;

        const html = `
      <div class="review-item" data-review-id="${review.id}">
        <div class="review-meta-row">
          <div class="review-meta">
            <img class="review-profile-img" src="${profileImg}">
            <div class="review-text-meta">
              <p class="review-profile">${review.nickNm || '익명'}</p>
              <p class="review-date">${formatted}${modifiedBadge}</p>
            </div>
          </div>
          ${actionsHtml}
        </div>

        <p class="review-content-display">${review.content}</p>

        <div class="review-edit-form" style="display:none;">
          <textarea class="review-edit-textarea">${review.content}</textarea>
          <div class="edit-actions">
            <button class="btn-review-save" onclick="handleReviewSave(${review.id})">저장</button>
            <button class="btn-review-cancel" onclick="handleReviewCancel(${review.id})">취소</button>
          </div>
        </div>
      </div>
    `;

        reviewListContainer.insertAdjacentHTML("beforeend", html);
    });
}

// =====================================================
// 🚀 5) 리뷰 페이지네이션
// =====================================================
function renderPagination(pagination) {
    // pagination 객체에서 currentPage와 totalPages를 추출
    const {currentPage, totalPages} = pagination; // currentPage는 1-index

    paginationContainer.innerHTML = '';
    if (totalPages <= 1) {
        return;
    }

    const PAGE_GROUP_SIZE = 5; // 한 번에 표시할 페이지 버튼 수
    // 1-index인 currentPage를 기준으로 그룹 시작 페이지를 계산
    const currentGroup = Math.ceil(currentPage / PAGE_GROUP_SIZE);
    const startPage = (currentGroup - 1) * PAGE_GROUP_SIZE + 1; // 1, 6, 11, ...
    const endPage = Math.min(startPage + PAGE_GROUP_SIZE - 1, totalPages); // 5, 10, 15, ... 또는 totalPages

    /**
     * 페이지 링크 HTML 요소를 생성하는 헬퍼 함수
     * @param {string} label - 버튼에 표시될 텍스트
     * @param {number} targetPage - 실제로 이동할 페이지 번호 (1-index)
     * @param {boolean} isDisabled - 비활성화 여부
     * @param {boolean} isActive - 활성화(현재 페이지) 여부
     * @param {string} actionType - 'page', 'prev-group', 'next-group' 중 하나
     */
    const createPageLinkHtml = (label, targetPage, isDisabled, isActive, actionType = 'page') => {
        const activeClass = isActive ? ' active' : '';
        const disabledStyle = isDisabled ? ' style="opacity: 0.5; pointer-events: none;"' : '';
        const targetAttr = `data-${actionType === 'page' ? 'page' : 'target-page'}="${targetPage}"`;
        const actionAttr = actionType !== 'page' ? `data-action="${actionType}"` : '';

        return `<span class="page-link${activeClass}" ${targetAttr} ${actionAttr} ${disabledStyle}>${label}</span>`;
    };

    // -------------------------------------------------------------
    // 1. [« 처음] 버튼: 항상 1페이지(0-index: 0)로 이동
    // -------------------------------------------------------------
    const firstPageHtml = createPageLinkHtml('«', 1, currentPage === 1, false);
    paginationContainer.insertAdjacentHTML('beforeend', firstPageHtml);

    // -------------------------------------------------------------
    // 2. [< 이전] 그룹 버튼: 이전 그룹의 첫 페이지(startPage - 1)로 이동
    // -------------------------------------------------------------
    const prevGroupPage = startPage - 1;
    const isPrevGroupDisabled = startPage === 1;
    const prevGroupHtml = createPageLinkHtml('‹', prevGroupPage, isPrevGroupDisabled, false, 'prev-group');
    paginationContainer.insertAdjacentHTML('beforeend', prevGroupHtml);

    // -------------------------------------------------------------
    // 3. 페이지 번호 버튼: startPage 부터 endPage 까지 출력
    // -------------------------------------------------------------
    for (let i = startPage; i <= endPage; i++) {
        const pageHtml = createPageLinkHtml(i, i, false, i === currentPage, 'page');
        paginationContainer.insertAdjacentHTML('beforeend', pageHtml);
    }

    // -------------------------------------------------------------
    // 4. [다음 >] 그룹 버튼: 다음 그룹의 첫 페이지(endPage + 1)로 이동
    // -------------------------------------------------------------
    const nextGroupPage = endPage + 1;
    const isNextGroupDisabled = endPage === totalPages;
    const nextGroupHtml = createPageLinkHtml('›', nextGroupPage, isNextGroupDisabled, false, 'next-group');
    paginationContainer.insertAdjacentHTML('beforeend', nextGroupHtml);

    // -------------------------------------------------------------
    // 5. [마지막 »] 버튼: 항상 totalPages로 이동
    // -------------------------------------------------------------
    const lastPageHtml = createPageLinkHtml('»', totalPages, currentPage === totalPages, false);
    paginationContainer.insertAdjacentHTML('beforeend', lastPageHtml);

    // 새로 생성된 요소에 이벤트 리스너 연결
    setupPaginationListeners();
}

/**
 * 페이지네이션 링크에 이벤트 리스너를 연결하는 함수
 */
function setupPaginationListeners() {
    // 모든 .page-link 요소에 리스너 연결
    paginationContainer.querySelectorAll('.page-link').forEach(link => {
        link.addEventListener('click', (event) => {
            const link = event.currentTarget; // 클릭된 요소 자체

            // 1. 그룹 이동 버튼 처리 (이전 그룹, 다음 그룹)
            const action = link.dataset.action;
            if (action === 'prev-group' || action === 'next-group') {
                const targetPage = parseInt(link.dataset.targetPage);
                // Spring Pageable은 0-index이므로 1을 빼서 전달
                if (!isNaN(targetPage) && targetPage >= 1) {
                    fetchReviewsAndRender(targetPage - 1);
                }
            }
            // 2. 개별 페이지 번호 또는 처음/마지막 버튼 처리
            else {
                const pageNumber = parseInt(link.dataset.page); // 1-index
                // Spring Pageable은 0-index이므로 1을 빼서 전달
                if (!isNaN(pageNumber) && pageNumber >= 1) {
                    fetchReviewsAndRender(pageNumber - 1);
                }
            }
        });
    });
}

// =====================================================
// 🚀 6) 리뷰 수정 / 취소 / 저장 / 삭제
// =====================================================
function handleReviewEdit(id) {
    const item = document.querySelector(`.review-item[data-review-id="${id}"]`);
    item.querySelector('.review-content-display').style.display = 'none';
    item.querySelector('.review-actions').style.display = 'none';
    item.querySelector('.review-edit-form').style.display = 'flex';
}

function handleReviewCancel(id) {
    const item = document.querySelector(`.review-item[data-review-id="${id}"]`);
    item.querySelector('.review-edit-form').style.display = 'none';
    item.querySelector('.review-content-display').style.display = 'block';
    item.querySelector('.review-actions').style.display = 'flex';
}

async function handleReviewSave(id) {
    const item = document.querySelector(`.review-item[data-review-id="${id}"]`);
    const newContent = item.querySelector('.review-edit-textarea').value;

    if (!newContent.trim()) {
        alert("내용을 입력해주세요.");
        return;
    }

    const res = await apiFetch(`/api/v1/reviews/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({content: newContent})
    });

    if (res.ok) {
        alert("수정 완료");
        fetchReviewsAndRender(0);
    } else {
        alert("수정 실패");
    }
}

async function handleReviewDelete(id) {
    if (!confirm("리뷰를 삭제하시겠습니까?")) {
        return;
    }

    const res = await apiFetch(`/api/v1/reviews/${id}`, {method: 'DELETE'});

    if (res.ok) {
        alert("삭제 완료");
        fetchReviewsAndRender(0);
    } else {
        alert("삭제 실패");
    }
}

// =====================================================
// 🚀 7) 관광지 상세 + 이미지 로드
// =====================================================
async function fetchDetails(id) {
    try {
        const res = await fetch(`/api/v1/tourist-spots/${id}`);

        if (!res.ok) {
            throw new Error("상세정보 호출 실패");
        }

        const {data: spot} = await res.json();

        document.getElementById('spot-title').textContent = spot.title;
        document.getElementById('spot-address').textContent = spot.address;
        document.getElementById('spot-phone').textContent = spot.phone;
        document.getElementById('spot-description').textContent = spot.description;

        // 이미지 검색 키워드 정제
        let keyword = spot.title.replace(/해양광장|광장/g, '').trim();

        const mainImage = await fetchImages(keyword);

        if (mainImage) {
            document.querySelector('.image-slider-container').innerHTML =
                `<img src="${mainImage}" style="width:100%;height:100%;object-fit:cover;border-radius:8px;">`;
        }

        // 지도 이동
        if (typeof geocoder !== 'undefined' && spot.address) {
            geocoder.addressSearch(spot.address, (result, status) => {
                if (status === kakao.maps.services.Status.OK) {
                    const lat = result[0].y;
                    const lng = result[0].x;

                    clearMarkers();
                    moveMap(lat, lng, 3);
                    addMarker(lat, lng, spot.title);
                }
            });
        }

    } catch (e) {
        console.error(e);
    }
}

// =====================================================
// 🚀 8) 관광지 이미지 API
// =====================================================
async function fetchImages(keyword) {
    const gallery = document.querySelector('.gallery-images');
    const status = document.getElementById('image-loading-status');

    gallery.innerHTML = '';
    status.textContent = '이미지 로딩 중...';

    try {
        const res = await fetch(
            `/api/v1/tourist-spots/images?keyword=${encodeURIComponent(keyword)}`);

        if (!res.ok) {
            throw new Error("이미지 API 실패");
        }

        const data = await res.json();

        let items = data.response?.body?.items?.item || [];

        if (!Array.isArray(items)) {
            items = [items];
        }

        if (items.length === 0) {
            status.textContent = "이미지 없음";
            return null;
        }

        status.style.display = 'none';

        items.slice(0, 5).forEach(imgItem => {
            const img = document.createElement('img');
            img.src = imgItem.galWebImageUrl;
            img.alt = imgItem.galTitle;
            img.referrerPolicy = "no-referrer";
            gallery.appendChild(img);
        });

        return items[0].galWebImageUrl;

    } catch (e) {
        console.error(e);
        status.textContent = "이미지 로딩 실패";
        return null;
    }
}

// =====================================================
// 🚀 9) 수정 / 삭제 버튼 리스너 연결
// =====================================================
function setupButtonListeners() {
    document.querySelector('.btn-spot-edit')?.addEventListener('click', () => {
        window.location.href = `/tourist/touristUpdatePage?id=${touristId}`;
    });

    document.querySelector('.btn-spot-delete')?.addEventListener('click',
        async () => {
            if (!confirm("정말 삭제하시겠습니까?")) {
                return;
            }

            const res = await apiFetch(`/api/v1/tourist-spots/${touristId}`, {
                method: 'DELETE'
            });

            if (res.ok) {
                alert("삭제 완료");
                window.location.href = "/tourist/touristListPage";
            } else {
                alert("삭제 실패");
            }
        });
}

// ===============================
// 🔵 리뷰 작성 이벤트 등록
// ===============================
document.getElementById('submit-review-btn')?.addEventListener('click',
    async function () {
        const content = document.getElementById('review-content').value.trim();

        if (!content) {
            alert("리뷰 내용을 입력해주세요.");
            return;
        }

        try {
            const memberNo = 1; // 🔥 실제 로그인 사용자 번호로 교체해야 함
            const url = `/api/v1/reviews/${touristId}`;

            // 🔥 apiFetch 사용 (토큰 자동 포함됨)
            const response = await apiFetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({content})
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || "리뷰 작성 실패");
            }

            alert("리뷰가 성공적으로 작성되었습니다.");
            document.getElementById('review-content').value = "";

            // 리뷰 다시 불러오기
            fetchReviewsAndRender(0);

        } catch (err) {
            console.error("리뷰 작성 오류:", err);
            alert(`리뷰 작성 실패: ${err.message}`);
        }
    });


async function getCurrentMemberId() {
    const token = localStorage.getItem("accessToken");

    if (!token) return null;

    const res = await fetch("/auth/me", {
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    const data = await res.json();
    return data.data; // memberId 가 들어있음
}