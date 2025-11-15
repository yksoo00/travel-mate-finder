/**
 * markerManager.js
 * * - 전역 'map'과 'geocoder' 변수를 사용합니다.
 * - 'markers' 배열을 직접 관리합니다.
 * - 주소(spot.address)를 좌표로 변환(Geocoding)합니다.
 * - 변환된 좌표에 마커(pin)를 표시합니다.
 */

let markers = [];

function displayMapMarkers(spots) {

  markers.forEach(marker => marker.setMap(null));
  markers = [];

  spots.forEach(spot => {
    if (!spot.address) {
      console.warn(`[${spot.title}] 주소(address)값이 없어 마커를 표시할 수 없습니다.`);
      return;
    }

    geocoder.addressSearch(spot.address, function (result, status) {
      if (status === kakao.maps.services.Status.OK) {
        const coords = new kakao.maps.LatLng(result[0].y, result[0].x);

        const marker = new kakao.maps.Marker({
          map: map,
          position: coords
        });

        const infowindow = new kakao.maps.InfoWindow({
          content: `
    <div style="
      padding:10px 16px;
      background:white;
      border-radius:12px;
      border:2px solid #00BFFF;
      font-weight:700;
      color:#333;
      font-size:16px;
      box-shadow:0px 4px 12px rgba(0,0,0,0.15);
      text-align:center;
      white-space:nowrap;
    ">
      ${spot.title}
    </div>
  `,
          removable: false   // ❗ X 버튼 제거!
        });

        kakao.maps.event.addListener(marker, 'click', function () {
          window.location.href = `/tourist/touristDetail/${spot.id}`;
        });

        kakao.maps.event.addListener(marker, 'mouseover', function () {
          infowindow.open(map, marker);
        });
        kakao.maps.event.addListener(marker, 'mouseout', function () {
          infowindow.close();
        });

        markers.push(marker);
      }
    });
  });
}