let map;
let geocoder;
let currentKeyword = null;

document.addEventListener("DOMContentLoaded", function () {
    const mapContainer = document.getElementById('map');
    const mapOption = {
        center: new kakao.maps.LatLng(37.566826, 126.9786567),
        level: 13
    };

    map = new kakao.maps.Map(mapContainer, mapOption);
    geocoder = new kakao.maps.services.Geocoder();
    const userRole = localStorage.getItem('memberId');

    if (userRole && userRole.includes('admin')) {

        const registerButton = document.getElementById('register-spot-button');

        if (registerButton) {
            registerButton.style.display = 'inline-block';
        }
    }

    loadMapMarkers();
    loadPage(0);
});

window.addEventListener("globalSearch", async (e) => {
    const keyword = (e.detail.keyword || "").trim();

    // 전역 검색어 상태 업데이트
    currentKeyword = keyword.length > 0 ? keyword : null;
    //
    loadMapMarkers(currentKeyword);

    // 첫 페이지부터 다시 조회
    loadPage(0);
});

async function loadMapMarkers(keyword) {
    if (window.clearMarkers) {
        window.clearMarkers();
    }

    let url = `/api/v1/tourist-spots/markers`;
    if (keyword && keyword.trim() !== '') {
        url += `?keyword=${encodeURIComponent(keyword)}`;
    }

    try {
        const response = await apiFetch(url);
        const data = await response.json();

        const allSpots = data.data;
        displayMapMarkers(allSpots); // 3. 새 마커 표시

    } catch (error) {
        console.error('Error fetching markers with keyword:', error);
    }
}

async function loadPage(page) {
    let url = `/api/v1/tourist-spots?page=${page}&size=10`;

    if (currentKeyword && currentKeyword.trim() !== '') {
        url += `&keyword=${encodeURIComponent(currentKeyword)}`;
    }

    try {
        const response = await apiFetch(url);
        const data = await response.json();

        const pageData = data.data;
        displayList(pageData.content);
        displayPagination(pageData);

    } catch (error) {
        console.error('Error fetching tourist spots page:', error);
    }
}

function displayList(spots) {
    const listElement = document.getElementById('spot-list-container');
    listElement.innerHTML = '';

    if (!spots || spots.length === 0) {
        listElement.innerHTML =
            '<div class="empty-state"><p class="empty-state-text">검색된 관광지가 없습니다.</p></div>';
        return;
    }

    spots.forEach(spot => {
        const card = document.createElement('div');
        card.className = 'recruit-card';

        card.onclick = function () {
            window.location.href = `/tourist/touristDetail/${spot.id}`;
        };

        const spotAddress = spot.address || '주소 정보 없음';

        card.innerHTML = `
      <div class="card-content">
        <h3 class="card-title">[${spot.district}] ${spot.title}</h3>
        <p class="card-description">${spotAddress}</p>
        <div class="card-author" style="border-top: none; padding-top: 5px;">
          <div class="author-info"></div>
        </div>
      </div>
    `;
        listElement.appendChild(card);
    });
}

function displayPagination(pageData) {
    const paginationElement = document.getElementById('pagination-container');
    paginationElement.innerHTML = '';

    const totalPages = pageData.totalPages;
    const currentPage = pageData.number;

    for (let i = 0; i < totalPages; i++) {
        const pageLink = document.createElement('button');
        pageLink.className = 'page-btn';
        pageLink.textContent = i + 1;

        if (i === currentPage) {
            pageLink.classList.add('active');
        }

        pageLink.addEventListener('click', (function (pageIndex) {
            return function (e) {
                e.preventDefault();
                loadPage(pageIndex);
            };
        })(i));

        paginationElement.appendChild(pageLink);
    }
}