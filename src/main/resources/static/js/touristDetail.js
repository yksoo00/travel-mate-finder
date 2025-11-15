
let touristId = null;
let reviewListContainer = null;
let paginationContainer = null;

const DEFAULT_PROFILE_IMG = '/img/default-avatar.png';

document.addEventListener("DOMContentLoaded", () => {

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
    const userRole = localStorage.getItem('memberId');
    console.log("현재 사용자 역할(memberId):", userRole);

    if (userRole && userRole.includes('admin')) {

        const adminButtonsContainer = document.getElementById('spot-admin-buttons');

        if (adminButtonsContainer) {
            adminButtonsContainer.style.display = 'flex';
            console.log("관리자 버튼을 표시합니다.");
        }
    }

    fetchDetails(touristId);
    fetchReviewsAndRender(0);


    setupButtonListeners();
});


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


function renderReviews(reviews) {
    reviewListContainer.innerHTML = '';

    if (reviews.length === 0) {
        reviewListContainer.innerHTML =
            `<div style="text-align:center;padding:20px;color:#777;">아직 리뷰가 없습니다.</div>`;
        return;
    }

    reviews.forEach(review => {

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

        const profileImg = review.profileImgUrl || DEFAULT_PROFILE_IMG;

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
          <div class="review-actions">
            <button class="btn-review-edit" onclick="handleReviewEdit(${review.id})">수정</button>
            <button class="btn-review-delete" onclick="handleReviewDelete(${review.id})">삭제</button>
          </div>
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


function renderPagination(totalPages, current) {
    paginationContainer.innerHTML = '';
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

    paginationContainer.appendChild(createBtn('«', 0, current === 0));
    paginationContainer.appendChild(
        createBtn('‹', Math.max(0, current - 1), current === 0));

    const maxButtons = 7;
    let start = Math.max(0, current - Math.floor(maxButtons / 2));
    let end = Math.min(totalPages - 1, start + maxButtons - 1);

    if (end - start < maxButtons - 1) {
        start = Math.max(0, end - (maxButtons - 1));
    }

    for (let i = start; i <= end; i++) {
        paginationContainer.appendChild(createBtn(i + 1, i, false, i === current));
    }

    paginationContainer.appendChild(
        createBtn('›', Math.min(totalPages - 1, current + 1),
            current === totalPages - 1));
    paginationContainer.appendChild(
        createBtn('»', totalPages - 1, current === totalPages - 1));
}


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
        const mainImageContainer = document.querySelector('.image-slider-container');
        const mainImage = await fetchImages(keyword);

        if (mainImage) {
            mainImageContainer.innerHTML =
                `<img src="${mainImage}" style="width:100%;height:100%;object-fit:cover;border-radius:8px;">`;
            mainImageContainer.style.display = 'block';
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


document.getElementById('submit-review-btn')?.addEventListener('click',
    async function () {
        const content = document.getElementById('review-content').value.trim();

        if (!content) {
            alert("리뷰 내용을 입력해주세요.");
            return;
        }

        try {
            const memberNo = 1;
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