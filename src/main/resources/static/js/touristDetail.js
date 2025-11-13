// /js/touristDetail.js (최종 완성본 - 수정 페이지 이동 반영)

// map.js의 전역 변수와 함수 (map, geocoder, moveMap, addMarker, clearMarkers)를 사용한다고 가정합니다.
const container = document.getElementById('touristDetailContainer');
const touristId = container?.dataset.touristId;

// 💡 컨테이너 변수 선언
const reviewListContainer = document.getElementById('review-list');
const paginationContainer = document.getElementById('pagination-container');

// 💡 기본 프로필 이미지 URL 설정
const DEFAULT_PROFILE_IMG = '/img/default-avatar.png';


console.log("tt", touristId);
document.addEventListener("DOMContentLoaded", function() {
    // Geocoder 초기화
    if (typeof kakao !== 'undefined' && typeof kakao.maps.services !== 'undefined' && typeof geocoder === 'undefined') {
        geocoder = new kakao.maps.services.Geocoder();
    }

    // touristId 변수가 Thymeleaf에 의해 주입되었는지 확인 후 사용
    if (typeof touristId !== 'undefined' && touristId !== null && touristId !== 0) {
        fetchDetails(touristId);
        fetchReviewsAndRender(0); // 리뷰 목록 로딩 시작 (Spring Pageable 0-Index)
    }
    setupButtonListeners(); // 모든 버튼 리스너 연결
});


// 💡 리뷰 목록을 서버로부터 가져와 화면에 렌더링하는 메인 함수
async function fetchReviewsAndRender(page = 0) {
    if (!touristId || !reviewListContainer || !paginationContainer) return;

    // 로딩 상태 표시
    reviewListContainer.innerHTML = `<div style="text-align: center; padding: 20px;">리뷰를 불러오는 중입니다...</div>`;
    paginationContainer.innerHTML = '';

    try {
        const url = `/api/v1/reviews/${touristId}?page=${page}&size=5`;
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status} (리뷰 호출 실패)`);
        }

        const data = await response.json();
        const reviews = data.data.content || [];
        const pagination = {
            currentPage: data.data.number + 1,
            totalPages: data.data.totalPages
        };

        renderReviews(reviews, reviewListContainer);
        renderPagination(pagination, paginationContainer);

    } catch (error) {
        console.error('Error fetching reviews:', error);
        reviewListContainer.innerHTML = `<div style="text-align: center; padding: 20px; color: red;">리뷰를 불러오는 데 실패했습니다.</div>`;
    }
}

// 💡 리뷰 목록 HTML을 생성하여 렌더링하는 함수 (수정 폼 포함)
function renderReviews(reviews, container) {
    container.innerHTML = '';

    if (reviews.length === 0) {
        container.innerHTML = `<div style="text-align: center; padding: 20px; color: #777;">아직 작성된 리뷰가 없습니다.</div>`;
        return;
    }

    const DEFAULT_PROFILE_IMG = '/img/default-avatar.png';

    reviews.forEach(review => {
        let displayDate = '날짜 미상';
        let modifiedBadge = ''; // 수정됨 표시

        // 💡 [수정됨] isModified 조건: updatedAt이 존재하고 && createdAt과 값이 다를 때만 '수정됨'으로 간주
        const isModified = !!review.updatedAt && (review.updatedAt !== review.createdAt);

        // 💡 표시할 날짜 선택: 수정된 경우 updatedAt, 아니면 createdAt 사용
        const dateSource = isModified ? review.updatedAt : review.createdAt;


        if (dateSource) {
            const d = new Date(dateSource);

            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const day = String(d.getDate()).padStart(2, '0');

            const hour = String(d.getHours()).padStart(2, '0');
            const minute = String(d.getMinutes()).padStart(2, '0');

            // 날짜 형식: YYYY-MM-DD HH:mm
            displayDate = `${year}-${month}-${day} ${hour}:${minute}`;

            // 💡 [수정됨] isModified가 true일 때만 뱃지 추가
            if (isModified) {
                modifiedBadge = ' <span class="review-modified-badge">(수정됨)</span>';
            }
        }

        // 💡 프로필 이미지 URL 결정
        const profileImg = review.profileImgUrl || DEFAULT_PROFILE_IMG;

        const reviewHtml = `
            <div class="review-item" data-review-id="${review.id}">
                <div class="review-meta-row">
                    <div class="review-meta"> 
                        <img class="review-profile-img" src="${profileImg}" alt="${review.nickNm} 프로필"/>
                        <div class="review-text-meta">
                            <p class="review-profile">${review.nickNm || '익명 사용자'}</p>
                            <p class="review-date">${displayDate}${modifiedBadge}</p>
                        </div>
                    </div>
                    <div class="review-actions">
                        <button class="btn-review-edit" onclick="handleReviewEdit(${review.id})">수정</button>
                        <button class="btn-review-delete" onclick="handleReviewDelete(${review.id})">삭제</button>
                    </div>
                </div>
                <p class="review-content-display">${review.content || '내용 없음'}</p> 

                <div class="review-edit-form" style="display: none;">
                    <textarea class="review-edit-textarea">${review.content || ''}</textarea>
                    <div class="edit-actions">
                        <button class="btn-review-save submit-btn" onclick="handleReviewSave(${review.id})">저장</button>
                        <button class="btn-review-cancel submit-btn" onclick="handleReviewCancel(${review.id})">취소</button>
                    </div>
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', reviewHtml);
    });
}

// 💡 페이지네이션 관련 함수들 (기존 로직 유지)

function renderPagination(pagination, container) {
    container.innerHTML = '';
    const { currentPage, totalPages } = pagination;

    const prevHtml = `<span class="page-link" data-action="prev" data-current-page="${currentPage}" data-total-pages="${totalPages}" ${currentPage <= 1 ? 'style="opacity: 0.5; pointer-events: none;"' : ''}>< 이전</span>`;
    container.insertAdjacentHTML('beforeend', prevHtml);

    for (let i = 1; i <= totalPages; i++) {
        const activeClass = i === currentPage ? ' active' : '';
        const pageLinkHtml = `<span class="page-link${activeClass}" data-page="${i}">${i}</span>`;
        container.insertAdjacentHTML('beforeend', pageLinkHtml);
    }

    const nextHtml = `<span class="page-link" data-action="next" data-current-page="${currentPage}" data-total-pages="${totalPages}" ${currentPage >= totalPages ? 'style="opacity: 0.5; pointer-events: none;"' : ''}>다음 ></span>`;
    container.insertAdjacentHTML('beforeend', nextHtml);

    setupPaginationListeners();
}

function handlePageClick(event) {
    const pageLink = event.target;
    const pageNumber = pageLink.dataset.page;
    const action = pageLink.dataset.action;

    if (pageNumber) {
        fetchReviewsAndRender(parseInt(pageNumber) - 1);
    } else if (action === 'prev') {
        const currentPage = parseInt(pageLink.dataset.currentPage);
        const prevPage = currentPage - 1;
        if (prevPage >= 1) {
            fetchReviewsAndRender(prevPage - 1);
        }
    } else if (action === 'next') {
        const currentPage = parseInt(pageLink.dataset.currentPage);
        const totalPages = parseInt(pageLink.dataset.totalPages);
        const nextPage = currentPage + 1;
        if (nextPage <= totalPages) {
            fetchReviewsAndRender(nextPage - 1);
        }
    }
}

function setupPaginationListeners() {
    document.querySelectorAll('#pagination-container .page-link').forEach(link => {
        link.removeEventListener('click', handlePageClick);
        link.addEventListener('click', handlePageClick);
    });
}


// ==============================================================================
// 💡 관광지 수정/삭제 구현 (수정: 페이지 이동, 삭제: API 호출)
// ==============================================================================

// 💡 [수정됨] 관광지 수정 처리 함수: 수정 페이지로 이동
function handleSpotEdit(spotId) {
    if (!confirm(`[관광지 ID: ${spotId}] 이 관광지 정보를 수정하시겠습니까? 수정 페이지로 이동합니다.`)) {
        return;
    }

    // 💡 별도의 수정 페이지 URL로 이동 (백엔드에서 해당 경로 처리 필요)
    window.location.href = `/tourist-spots/${spotId}/edit`;
}

// 💡 관광지 삭제 처리 함수 (API 호출 유지)
async function handleSpotDelete(spotId) {
    if (!confirm(`[관광지 ID: ${spotId}] 관광지를 정말 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.`)) {
        return;
    }

    try {
        const url = `/api/v1/tourist-spots/${spotId}`;
        const response = await fetch(url, {
            method: 'DELETE',
            headers: {
                // 인증 토큰 없음
            }
        });

        if (response.ok) {
            alert('관광지가 성공적으로 삭제되었습니다. 목록 페이지로 이동합니다.');
            window.location.href = '/tourist-spots'; // 목록 페이지로 리다이렉션 (경로 확인 필요)
        } else {
            const data = await response.json();
            alert(`관광지 삭제 실패: ${data.message || '서버 오류'} (권한 오류 확인 필요)`);
        }

    } catch (error) {
        console.error('Error deleting spot:', error);
        alert('관광지 삭제 중 네트워크 오류가 발생했습니다.');
    }
}


// ==============================================================================
// 💡 리뷰 수정/삭제 구현 (토큰 제거 상태 유지)
// ==============================================================================

function handleReviewEdit(reviewId) {
    const reviewItem = document.querySelector(`.review-item[data-review-id="${reviewId}"]`);
    if (!reviewItem) return;

    reviewItem.querySelector('.review-content-display').style.display = 'none';
    reviewItem.querySelector('.review-actions').style.display = 'none';
    reviewItem.querySelector('.review-edit-form').style.display = 'flex';
}

function handleReviewCancel(reviewId) {
    const reviewItem = document.querySelector(`.review-item[data-review-id="${reviewId}"]`);
    if (!reviewItem) return;

    reviewItem.querySelector('.review-edit-form').style.display = 'none';
    reviewItem.querySelector('.review-content-display').style.display = 'block';
    reviewItem.querySelector('.review-actions').style.display = 'flex';

    const originalContent = reviewItem.querySelector('.review-content-display').textContent;
    reviewItem.querySelector('.review-edit-textarea').value = originalContent;
}

// 💡 저장 버튼 클릭 시 서버로 수정 요청
async function handleReviewSave(reviewId) {
    const reviewItem = document.querySelector(`.review-item[data-review-id="${reviewId}"]`);
    if (!reviewItem) return;

    const newContent = reviewItem.querySelector('.review-edit-textarea').value;

    if (!newContent.trim()) {
        alert('수정할 리뷰 내용을 입력해주세요.');
        return;
    }

    try {
        const url = `/api/v1/reviews/${reviewId}`;
        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                content: newContent
            })
        });

        if (response.ok) {
            alert('리뷰가 성공적으로 수정되었습니다.');
            fetchReviewsAndRender(0);
        } else {
            const data = await response.json();
            alert(`리뷰 수정 실패: ${data.message || '서버 오류'} (권한 오류 확인 필요)`);
            handleReviewCancel(reviewId);
        }

    } catch (error) {
        console.error('Error saving review:', error);
        alert('리뷰 수정 중 네트워크 오류가 발생했습니다.');
        handleReviewCancel(reviewId);
    }
}

// 💡 삭제 버튼 클릭 시 서버로 삭제 요청
async function handleReviewDelete(reviewId) {
    if (!confirm(`[리뷰 ID: ${reviewId}] 이 리뷰를 정말 삭제하시겠습니까?`)) {
        return;
    }

    try {
        const url = `/api/v1/reviews/${reviewId}`;
        const response = await fetch(url, {
            method: 'DELETE',
            headers: {}
        });

        if (response.ok) {
            alert('리뷰가 성공적으로 삭제되었습니다.');
            fetchReviewsAndRender(0);
        } else {
            const data = await response.json();
            alert(`리뷰 삭제 실패: ${data.message || '서버 오류'} (권한 오류 확인 필요)`);
        }

    } catch (error) {
        console.error('Error deleting review:', error);
        alert('리뷰 삭제 중 네트워크 오류가 발생했습니다.');
    }
}


// ==============================================================================
// 💡 관광지 상세 정보 및 이미지 로드 함수 (기존과 동일)
// ==============================================================================
async function fetchDetails(id) {
    try {
        const response = await fetch(`/api/v1/tourist-spots/${id}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status} (API 호출 실패)`);
        }

        const data = await response.json();
        const spot = data.data;

        if (spot) {
            // 1. 정보 표시
            document.getElementById('spot-title').textContent = spot.title || '정보 없음';
            document.getElementById('spot-address').textContent = spot.address || '정보 없음';
            document.getElementById('spot-phone').textContent = spot.phone || '정보 없음';
            document.getElementById('spot-description').textContent = spot.description || '정보 없음';

            // 2. 이미지 가져오기 및 메인 이미지 설정
            let searchKeyword = spot.title;
            if (searchKeyword.includes("해양광장")) {
                searchKeyword = searchKeyword.replace("해양광장", "").trim();
            } else if (searchKeyword.includes("광장")) {
                searchKeyword = searchKeyword.replace("광장", "").trim();
            }

            const firstImageUrl = await fetchImages(searchKeyword);

            if (firstImageUrl) {
                const sliderContainer = document.querySelector('.image-slider-container');
                sliderContainer.innerHTML = `<img src="${firstImageUrl}" alt="${spot.title} Main Image" style="width: 100%; height: 100%; object-fit: cover; border-radius: 8px;">`;
            } else {
                document.getElementById('main-image-placeholder').textContent = "이미지 없음";
            }

            // 3. 카카오맵 위치 설정 (기존 로직 유지)
            if (spot.address && typeof geocoder !== 'undefined' && typeof moveMap === 'function') {
                geocoder.addressSearch(spot.address, function(result, status) {
                    if (status === kakao.maps.services.Status.OK) {
                        const lat = result[0].y;
                        const lng = result[0].x;
                        if (typeof clearMarkers === 'function') clearMarkers();
                        if (typeof moveMap === 'function') moveMap(lat, lng, 3);
                        if (typeof addMarker === 'function') addMarker(lat, lng, spot.title);
                    }
                });
            }
        } else {
            document.getElementById('spot-title').textContent = "관광지 정보를 찾을 수 없습니다. (데이터 필드 없음)";
            document.getElementById('image-loading-status').textContent = "관광지 정보를 찾을 수 없습니다.";
        }
    } catch (error) {
        console.error('Error fetching details:', error);
        document.getElementById('spot-title').textContent = `오류가 발생했습니다: ${error.message}`;
        document.getElementById('image-loading-status').textContent = "오류가 발생했습니다.";
    }
}


async function fetchImages(keyword) {
    const galleryContainer = document.querySelector('.gallery-images');
    const statusText = document.getElementById('image-loading-status');
    galleryContainer.innerHTML = '';
    statusText.textContent = '...이미지 로딩 중';

    const url = `/api/v1/tourist-spots/images?keyword=${encodeURIComponent(keyword)}`;

    try {
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`이미지 API 호출 실패: HTTP Status ${response.status}`);
        }

        const data = await response.json();
        let items = data.response?.body?.items?.item;

        if (items && !Array.isArray(items)) {
            items = [items];
        }

        if (items && items.length > 0) {
            statusText.style.display = 'none';
            items.slice(0, 5).forEach(item => {
                const imageUrl = item.galWebImageUrl;
                if (imageUrl) {
                    const img = document.createElement('img');
                    img.src = imageUrl;
                    img.alt = item.galTitle;
                    img.referrerPolicy = "no-referrer";
                    galleryContainer.appendChild(img);
                }
            });
            return items[0].galWebImageUrl;
        } else {
            statusText.textContent = `관련 이미지를 찾을 수 없습니다. (검색어: ${keyword})`;
            return null;
        }
    } catch (error) {
        console.error('Error fetching images details:', error);
        statusText.textContent = `이미지 로딩 중 오류가 발생했습니다. 자세한 오류: ${error.message}`;
        return null;
    }
}

function setupButtonListeners() {
    // 💡 관광지 수정/삭제 버튼 리스너 연결 (touristId를 인자로 넘김)
    document.querySelector('.btn-spot-edit')?.addEventListener('click', () => handleSpotEdit(touristId));
    document.querySelector('.btn-spot-delete')?.addEventListener('click', () => handleSpotDelete(touristId));

    // 💡 리뷰 작성 로직 구현
    document.getElementById('submit-review-btn')?.addEventListener('click', async function() {
        const content = document.getElementById('review-content').value;

        if (content.trim()) {
            try {
                const memberNo = 1;
                const url = `/api/v1/reviews/${touristId}?memberNo=${memberNo}`;

                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        content: content
                    })
                });

                const data = await response.json();

                if (response.ok && (data.status === 'OK' || data.status === 'CREATED')) {
                    alert('리뷰가 성공적으로 작성되었습니다.');
                    document.getElementById('review-content').value = '';
                    fetchReviewsAndRender(0);
                } else {
                    alert(`리뷰 작성 실패: ${data.message || '서버 오류'}`);
                }

            } catch (error) {
                console.error('Error submitting review:', error);
                alert('리뷰 작성 중 네트워크 오류가 발생했습니다.');
            }

        } else {
            alert('리뷰 내용을 입력해주세요.');
        }
    });
}