let map;
let geocoder;

let markers = [];

document.addEventListener("DOMContentLoaded", function () {
  const mapContainer = document.getElementById('map');
  if (!mapContainer) {
    console.error("❌ #map 요소를 찾을 수 없습니다.");
    return;
  }

  const mapOption = {
    center: new kakao.maps.LatLng(37.566826, 126.9786567),
    level: 7
  };

  // 지도 생성
  map = new kakao.maps.Map(mapContainer, mapOption);

  // 지오코더 생성
  geocoder = new kakao.maps.services.Geocoder();

  // 전역 등록
  window.map = map;
  window.geocoder = geocoder;
  window.markers = markers;
  loadAllMarkers();
});

// ===============================================
// ⭐ 전체 관광지 마커 불러오기 (apiFetch 적용)
// ===============================================
async function loadAllMarkers() {
  try {
    const response = await apiFetch(`/api/v1/tourist-spots/markers`);

    if (!response.ok) {
      console.error("❌ markers API 오류");
      return;
    }

    const result = await response.json();
    const allSpots = result.data || [];

    console.log("📌 가져온 관광지 개수:", allSpots.length);
    if (allSpots.length > 0) {
      console.log("📍 첫 장소 주소:", allSpots[0].address);
    }

    displayMapMarkers(allSpots);

  } catch (error) {
    console.error("❌ 전체 마커 로드 실패:", error);
  }
}

function addMarker(lat, lng, title) {
  const position = new kakao.maps.LatLng(lat, lng);

  const marker = new kakao.maps.Marker({
    position: position,
    map: map
  });

  // Add info window if title is provided

  const overlayDiv = document.createElement("div");
  overlayDiv.className = "ouigo-overlay";
  overlayDiv.innerText = title;

  const overlay = new kakao.maps.CustomOverlay({
    position: position,
    content: overlayDiv,
    yAnchor: 1.7,
    xAnchor: 0.5
  });

  // 처음에는 숨김
  overlay.setMap(null);

  // 🔵 마우스 올리면 표시
  kakao.maps.event.addListener(marker, "mouseover", () => {
    overlay.setMap(map);
    overlayDiv.classList.add("show");
  });

  // 🔵 마우스 벗어나면 숨김
  kakao.maps.event.addListener(marker, "mouseout", () => {
    overlayDiv.classList.remove("show");

    setTimeout(() => {
      if (!overlayDiv.classList.contains("show")) {
        overlay.setMap(null);
      }
    }, 150);
  });
  window.markers = [];
  markers.push(marker);
  return marker;
}

// Function to clear all markers
function clearMarkers() {
  markers.forEach(marker => marker.setMap(null));
  markers = [];
}

// Function to move map to specific location
function moveMap(lat, lng, level = 5) {
  const moveLatLon = new kakao.maps.LatLng(lat, lng);
  map.setCenter(moveLatLon);
  map.setLevel(level);
}