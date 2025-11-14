// ==================== 마이페이지 JavaScript ====================
// ==================== Left Container Loader ====================

// 토큰
let token = localStorage.getItem("accessToken");

// 공통 API 호출 (자동 토큰 갱신)
async function apiFetch(url, options = {}) {

  let accessToken = localStorage.getItem("accessToken");
  let refreshToken = localStorage.getItem("refreshToken");

  options.headers = options.headers || {};

  // FormData가 아닐 때만 Content-Type 지정
  if (!(options.body instanceof FormData)) {
    options.headers["Content-Type"] = "application/json";
  }

  // AccessToken 자동 포함
  options.headers["Authorization"] = `Bearer ${accessToken}`;

  // ---- 1차 요청 ----
  let response = await fetch(url, options);

  // AccessToken 만료 시
  if (response.status === 401) {
    console.warn("⚠ Access Token 만료 → Refresh 시도");

    const refreshResponse = await fetch("/auth/refresh", {
      method: "POST",
      headers: {"Authorization": `Bearer ${refreshToken}`}
    });
    if (refreshResponse.status === 200) {
      const data = await refreshResponse.json();
      const newAccess = data.data.accessToken;
      const newRefresh = data.data.refreshToken;

      // 로컬 저장소 갱신
      localStorage.setItem("accessToken", newAccess);
      localStorage.setItem("refreshToken", newRefresh);

      // 기존 요청 재시도
      options.headers["Authorization"] = `Bearer ${newAccess}`;
      return await fetch(url, options);
    }

    alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
    window.location.href = "/loginFrom";
    return;
  }

  return response;
}

// ================================================================
// 화면 로드
let allTrips = [];
let currentMemberNo = null;

document.addEventListener("DOMContentLoaded", async () => {

  currentMemberNo = localStorage.getItem("memberNo"); // 백엔드에서 토큰 기반이면 없어도 됨

  await loadLeftView("view");
  loadTripCard();
  loadTrips();
});

// ==================== Left 화면 로드 ====================
async function loadLeftView(type) {
  const url = type === "edit" ? "/profile-edit" : "/profile-view";

  const res = await apiFetch(url);
  const html = await res.text();

  const leftContainer = document.getElementById("leftContainer");
  leftContainer.innerHTML = html;

  // 화면이 삽입된 이후 실행
  setTimeout(() => {
    if (type === "view") {
      loadProfileView();
    } else {
      loadProfileEdit();
    }
  }, 0);
}

function goEdit() {
  loadLeftView("edit");
}

// ==================== 프로필 조회 ====================
async function loadProfileView() {
  const res = await apiFetch(`/api/v1/myPage`);
  const data = await res.json();

  if (data.status !== 200) {
    return;
  }

  const p = data.data;

  const img = p.profileImage || "/images/default-profile.png";
  document.getElementById("profileImageView").src = img;
  document.getElementById("nickNameView").textContent = p.nickName;
  document.getElementById("ageView").textContent = `${p.age}세`;
  document.getElementById("genderView").textContent = p.gender === "M" ? "남성"
      : "여성";
  document.getElementById("emailView").textContent = p.email;
  document.getElementById("introView").textContent = p.introduction
      ?? "자기소개가 없습니다.";
}

// ==================== 프로필 수정 화면 ====================
async function loadProfileEdit() {
  const res = await apiFetch(`/api/v1/myPage`);
  const data = await res.json();

  const p = data.data;

  currentImageUrl = p.profileImage || "/images/default-profile.png";
  document.getElementById("profileImagePreview").src = currentImageUrl;

  document.getElementById("nickNameInput").value = p.nickName;
  document.getElementById("emailInput").value = p.email;
  document.getElementById("introInput").value = p.introduction || "";
}

// 이미지 미리보기
function previewImage(e) {
  const file = e.target.files[0];
  if (!file) {
    return;
  }

  selectedFile = file;
  const reader = new FileReader();

  reader.onload = function (ev) {
    document.getElementById("profileImagePreview").src = ev.target.result;
  };

  reader.readAsDataURL(file);
}

// 프로필 이미지 업로드
async function uploadProfileImage() {
  if (!selectedFile) {
    return currentImageUrl;
  }

  const fd = new FormData();
  fd.append("file", selectedFile);

  const res = await apiFetch(`/api/v1/myPage/profile/image`, {
    method: "POST",
    body: fd
  });

  const data = await res.json();
  return data.data;
}

// 프로필 수정 요청
async function submitProfileEdit() {

  const finalImageUrl = await uploadProfileImage();

  const dto = {
    nickName: document.getElementById("nickNameInput").value,
    email: document.getElementById("emailInput").value,
    introduction: document.getElementById("introInput").value,
    profileImage: finalImageUrl
  };

  const res = await apiFetch(`/api/v1/myPage`, {
    method: "PUT",
    body: JSON.stringify(dto)
  });

  const data = await res.json();

  if (data.status === 200) {
    alert("프로필 수정 완료!");
    loadLeftView("view");
  } else {
    alert("수정 실패: " + data.message);
  }
}

window.goEdit = () => loadLeftView("edit");
window.cancelEdit = () => loadLeftView("view");

// ==================== Trip Card ====================
async function loadTripCard() {
  const res = await apiFetch(`/api/v1/myPage/card`);
  const data = await res.json();

  if (data.status === 200) {
    displayTripCard(data.data);
  } else {
    displayEmptyCard();
  }
}

function displayTripCard(card) {
  document.getElementById("dstcCnt").textContent = card.dstcCnt;
  document.getElementById("ttlCnt").textContent = card.ttlCnt;
  document.getElementById("dayCnt").textContent = card.dayCnt;
}

function displayEmptyCard() {
  document.getElementById("dstcCnt").textContent = 0;
  document.getElementById("ttlCnt").textContent = 0;
  document.getElementById("dayCnt").textContent = 0;
}

// ==================== Trip List ====================
async function loadTrips() {
  const res = await apiFetch(`/api/v1/myPage/trips`);
  const data = await res.json();

  if (data.status === 200) {
    allTrips = data.data;
    displayTrips(allTrips);
  } else {
    displayEmptyTrips();
  }
}

function displayTrips(trips) {
  const list = document.getElementById("tripList");

  list.innerHTML = trips
  .map(t => {
    const dDay = calculateDday(t.startDate);
    return `
                <div class="trip-card">
                    <div class="trip-dday ${getDdayClass(dDay)}">${dDay}</div>
                    <div class="trip-destination">${t.destination}</div>
                    <div class="trip-title">${t.title}</div>
                    <div class="trip-dates">${t.startDate} ~ ${t.endDate}</div>
                    <div class="trip-info-row">
                        <div class="trip-duration">${t.duration}일</div>
                        <div class="trip-budget">${formatBudget(t.budget)}</div>
                    </div>
                    <div class="trip-actions">
                        <button class="btn-edit" onclick="editTrip(${t.id})">수정</button>
                        <button class="btn-delete" onclick="deleteTrip(${t.id})">삭제</button>
                    </div>
                </div>
            `;
  })
  .join("");
}

function displayEmptyTrips() {
  document.getElementById("tripList").innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">✈️</div>
            <div class="empty-text">아직 등록된 여행 일정이 없습니다.</div>
        </div>
    `;
}

function formatBudget(b) {
  return b ? b.toLocaleString() + "원" : "예산 미정";
}

function getDdayClass(dday) {
  if (dday === "D-Day") {
    return "today";
  }
  if (dday.startsWith("D-")) {
    return "upcoming";
  }
  return "past";
}

function calculateDday(startDate) {
  const today = new Date();
  const start = new Date(startDate);

  const diff = start - today;
  const days = Math.ceil(diff / (1000 * 60 * 60 * 24));

  if (days > 0) {
    return `D-${days}`;
  }
  if (days === 0) {
    return "D-Day";
  }
  return `D+${Math.abs(days)}`;
}

// ==================== 여행 삭제 ====================
async function deleteTrip(id) {
  if (!confirm("삭제하시겠습니까?")) {
    return;
  }

  const res = await apiFetch(`/api/v1/myPage/trips/${id}`, {
    method: "DELETE"
  });

  const data = await res.json();

  if (data.status === 200) {
    alert("삭제되었습니다");
    loadTrips();
    loadTripCard();
  } else {
    alert("삭제 실패: " + data.message);
  }
}

// ==================== 여행 수정 ====================
async function editTrip(tripId) {
  const trip = allTrips.find(t => t.id === tripId);
  if (!trip) {
    return;
  }

  window.currentEditingTrip = trip;

  await loadRightView(`/myPage/trip/edit/${tripId}`);

  setTimeout(fillTripEditForm, 0);
}

async function loadRightView(path) {
  const res = await apiFetch(path);
  const html = await res.text();

  document.getElementById("rightContainer").innerHTML = html;
}

function fillTripEditForm() {
  const t = window.currentEditingTrip;
  if (!t) {
    return;
  }

  document.getElementById("destination").value = t.destination;
  document.getElementById("title").value = t.title;
  document.getElementById("startDate").value = t.startDate;
  document.getElementById("endDate").value = t.endDate;
  document.getElementById("budget").value = t.budget || "";
  document.getElementById("memo").value = t.memo || "";
}

async function submitTrip(event) {
  event.preventDefault();

  const tripId = window.currentEditingTrip.id;

  const dto = {
    destination: document.getElementById("destination").value,
    title: document.getElementById("title").value,
    startDate: document.getElementById("startDate").value,
    endDate: document.getElementById("endDate").value,
    budget: parseInt(document.getElementById("budget").value) || 0,
    memo: document.getElementById("memo").value
  };

  const res = await apiFetch(`/api/v1/myPage/trips/${tripId}`, {
    method: "PUT",
    body: JSON.stringify(dto)
  });

  const data = await res.json();

  if (data.status === 200) {
    alert("수정 완료!");
    window.location.href = "/myPage";
  } else {
    alert("수정 실패: " + data.message);
  }
}

// ==================== 여행 등록 ====================
function submitTripFromButton() {
  const form = document.getElementById("tripCreateForm");
  if (form.checkValidity()) {
    submitTripCreate(new Event("submit"));
  } else {
    form.reportValidity();
  }
}

async function submitTripCreate(event) {
  event.preventDefault();

  const dto = {
    destination: document.getElementById('destination').value,
    title: document.getElementById('title').value,
    startDate: document.getElementById('startDate').value,
    endDate: document.getElementById('endDate').value,
    budget: parseInt(document.getElementById('budget').value) || 0,
    memo: document.getElementById('memo').value
  };

  const res = await apiFetch(`/api/v1/myPage/trips`, {
    method: "POST",
    body: JSON.stringify(dto)
  });

  const data = await res.json();

  if (data.status === 200 || data.status === 201) {
    alert("등록 완료!");
    window.location.href = "/myPage";
  } else {
    alert("등록 실패: " + data.message);
  }
}

function cancelCreate() {
  window.location.href = "/myPage";
}

function createTrip() {
  loadRightView("/myPage/trip/create");
}