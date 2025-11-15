/**
 * map-marker-select.js
 * * 이 스크립트는 'map.js'가 생성한 전역 'map' 변수를 감지하여
 * 지도 클릭 이벤트를 자동으로 추가합니다.
 * * 기능:
 * 1. 지도 클릭 시 해당 위치에 마커 표시
 * 2. 좌표를 주소로 변환
 * 3. id="map-address-display"와 id="address" 필드에 주소 값 채우기
 * 4. 지도 커서를 'pointer'(손가락) 모양으로 변경
 */
document.addEventListener('DOMContentLoaded', function () {
  setupMapClickEventWhenReady();
});

function setupMapClickEventWhenReady() {

  if (window.map) {

    const geocoder = new kakao.maps.services.Geocoder();
    let clickMarker = null;

    map.setCursor('pointer');

    kakao.maps.event.addListener(map, 'click', function (mouseEvent) {

      if (clickMarker) {
        clickMarker.setMap(null);
      }

      var latlng = mouseEvent.latLng;
      clickMarker = new kakao.maps.Marker({
        position: latlng
      });
      clickMarker.setMap(map);

      searchAddrFromCoords(latlng, function (result, status) {
        if (status === kakao.maps.services.Status.OK) {
          var addr = result[0].road_address
              ? result[0].road_address.address_name
              : result[0].address.address_name;

          // 6. ID를 기준으로 폼에 주소 값 채우기
          const addressDisplayField = document.getElementById(
              'map-address-display');
          const hiddenAddressField = document.getElementById('address');

          if (addressDisplayField) {
            addressDisplayField.value = addr;
          }
          if (hiddenAddressField) {
            hiddenAddressField.value = addr;
          }

        } else {
          alert('주소를 가져오는 데 실패했습니다.');
        }
      });
    });

    function searchAddrFromCoords(coords, callback) {
      geocoder.coord2Address(coords.getLng(), coords.getLat(), callback);
    }

  } else {
    setTimeout(setupMapClickEventWhenReady, 50);
  }
}