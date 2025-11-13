// ==================== 마이페이지 JavaScript ====================

// ✅ 전역 토큰 (모든 요청에 자동으로 붙음)
const token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTc2MzAxMjA3OH0.9ZyMi_h2zZRbrpCryRBElhvXCRuaqI00S-ML7uYIqwU0JGmlPzjv9ksnyaH2O33sWXnLGlQeqAh8AAmPFvGoNg";

// 전역 fetch 오버라이드 (모든 요청에 자동으로 Authorization 추가)
const _originalFetch = window.fetch;
window.fetch = async (url, options = {}) => {
    const newOptions = {
        ...options,
        headers: {
            ...(options.headers || {}),
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        },
    };
    return _originalFetch(url, newOptions);
};

// ================================================================

let currentMemberNo = null;
let allTrips = [];

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    currentMemberNo = urlParams.get('memberNo') || 1;

    initMyPage();
});

// 마이페이지 초기화
function initMyPage() {
    loadProfile();
    loadTripCard();
    loadTrips();
}

// 프로필 정보
async function loadProfile() {
    try {
        // /api/v1/myPage
        const response = await fetch(`/api/v1/myPage?memberNo=${currentMemberNo}`);
        const data = await response.json();

        if (data.status === 200) {
            displayProfile(data.data);
        } else {
            console.error('프로필 로드 실패:', data.message);
            showError('프로필을 불러올 수 없습니다.');
        }
    } catch (error) {
        console.error('프로필 로드 오류:', error);
        showError('서버 오류가 발생했습니다.');
    }
}

// 프로필 표시
function displayProfile(profile) {
    let imageUrl = profile.profileImage;
    if (!imageUrl || imageUrl === 'null' || imageUrl === '') {
        // UI Avatars 서비스 사용 (이니셜로 이미지 생성)
        const name = profile.nickName || 'User';
        imageUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=00BFFF&color=fff&size=200&bold=true`;
    }
    document.getElementById('profileImage').src = profile.profileImage || '/images/default-profile.png';
    document.getElementById('nickName').textContent = profile.nickName || '닉네임 없음';
    document.getElementById('age').textContent = profile.age ? `${profile.age}세` : '나이 미공개';
    const genderText = profile.gender === 'M' ? '남성' : profile.gender === 'F' ? '여성' : '성별 미공개';
    document.getElementById('gender').textContent = genderText;
    document.getElementById('email').textContent = profile.email || '이메일 없음';
    document.getElementById('introduction').textContent = profile.introduction || '자기소개가 없습니다.';
}

// 여행 카드 통계 로드
async function loadTripCard() {
    try {
        const response = await fetch(`/api/v1/myPage/card?memberNo=${currentMemberNo}`);
        const data = await response.json();

        if (data.status === 200) {
            displayTripCard(data.data);
        } else if (data.status === 404) {
            displayEmptyCard();
        }
    } catch (error) {
        console.error('카드 로드 오류:', error);
        displayEmptyCard();
    }
}

// 여행 카드 표시
function displayTripCard(cardData) {
    document.getElementById('dstcCnt').textContent = cardData.dstcCnt || 0;
    document.getElementById('ttlCnt').textContent = cardData.ttlCnt || 0;
    document.getElementById('dayCnt').textContent = cardData.dayCnt || 0;
}

// 빈 카드 표시
function displayEmptyCard() {
    document.getElementById('dstcCnt').textContent = 0;
    document.getElementById('ttlCnt').textContent = 0;
    document.getElementById('dayCnt').textContent = 0;
}

// 여행 일정 목록 로드
async function loadTrips() {
    try {
        const response = await fetch(`/api/v1/myPage/trips?memberNo=${currentMemberNo}`, { method: 'GET' });
        const data = await response.json();

        if (data.status === 200) {
            allTrips = data.data;
            displayTrips(allTrips);
        } else {
            displayEmptyTrips();
        }
    } catch (error) {
        console.error('여행 일정 로드 오류:', error);
        displayEmptyTrips();
    }
}

// 여행 일정 표시
function displayTrips(trips) {
    const tripList = document.getElementById('tripList');

    if (!trips || trips.length === 0) {
        displayEmptyTrips();
        return;
    }

    tripList.innerHTML = trips.map(trip => {
        // D-day 계산
        const dDay = calculateDday(trip.startDate);

        return `
            <div class="trip-card" onclick="event.stopPropagation()">
                <div class="trip-dday ${getDdayClass(dDay)}">${dDay}</div>
                <div class="trip-destination">${trip.destination}</div>
                <div class="trip-title">${trip.title}</div>
                <div class="trip-dates">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="2">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                        <line x1="16" y1="2" x2="16" y2="6"></line>
                        <line x1="8" y1="2" x2="8" y2="6"></line>
                        <line x1="3" y1="10" x2="21" y2="10"></line>
                    </svg>
                    ${trip.startDate} ~ ${trip.endDate}
                </div>
                <div class="trip-info-row">
                    <div class="trip-duration">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#666" stroke-width="2">
                            <circle cx="12" cy="12" r="10"></circle>
                            <polyline points="12 6 12 12 16 14"></polyline>
                        </svg>
                        ${trip.duration}일
                    </div>
                    <div class="trip-budget">${formatBudget(trip.budget)}</div>
                </div>
                <div class="trip-actions">
                    <button class="btn-edit" onclick="editTrip(${trip.id})">수정</button>
                    <button class="btn-delete" onclick="deleteTrip(${trip.id})">삭제</button>
                </div>
            </div>
        `;
    }).join('');
}

// 빈 여행 일정 표시
function displayEmptyTrips() {
    const tripList = document.getElementById('tripList');
    tripList.innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">✈️</div>
            <div class="empty-text">아직 등록된 여행 일정이 없습니다.</div>
            <div class="empty-text" style="font-size: 14px; color: #aaa; margin-top: 8px;">
                위의 "일정 추가" 버튼을 눌러 첫 여행을 등록해보세요!
            </div>
        </div>
    `;
}

// D-day 클래스 반환
function getDdayClass(dday) {
    if (!dday || typeof dday !== 'string') {
        return 'past'; // 기본값 (혹은 'unknown', 'none' 등으로도 가능)
    }

    if (dday.startsWith('D-') && !dday.includes('+')) {
        return 'upcoming';
    } else if (dday === 'D-Day') {
        return 'today';
    } else {
        return 'past';
    }
}


// 예산 포맷
function formatBudget(budget) {
    if (!budget) return '예산 미정';
    return budget.toLocaleString() + '원';
}

// 프로필 수정 페이지로 이동
function editProfile() {
    window.location.href = `/myPage/profileEdit?memberNo=${currentMemberNo}`;
}

// 여행 등록 페이지로 이동
function createTrip() {
    window.location.href = `/myPage/tripCreate?memberNo=${currentMemberNo}`;
}

// 여행 수정 페이지로 이동
function editTrip(tripId) {
    const trip = allTrips.find(t => t.id === tripId);
    if (trip) {
        localStorage.setItem('editTrip', JSON.stringify(trip));
        window.location.href = `/myPage/tripEdit?tripId=${tripId}&memberNo=${currentMemberNo}`;
    }
}

// 여행 삭제
async function deleteTrip(tripId) {
    if (!confirm('정말 이 여행 일정을 삭제하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`/api/v1/myPage/trips/${tripId}`, { method: 'DELETE' });
        const data = await response.json();

        if (data.status === 200) {
            alert('여행 일정이 삭제되었습니다.');
            loadTrips();
            loadTripCard();
        } else {
            alert(`삭제 실패: ${data.message}`);
        }
    } catch (error) {
        console.error('삭제 오류:', error);
        alert('서버 오류가 발생했습니다.');
    }
}

// 에러 표시
function showError(message) {
    alert(message);
}

function calculateDday(startDate) {
    if (!startDate) return 'D-?';

    const today = new Date();
    const start = new Date(startDate);
    const diffTime = start - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays > 0) return `D-${diffDays}`;
    if (diffDays === 0) return 'D-Day';
    return `D+${Math.abs(diffDays)}`;
}

