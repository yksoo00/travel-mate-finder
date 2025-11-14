let map;
let geocoder;

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