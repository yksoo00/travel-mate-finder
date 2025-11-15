function displayMapMarkers(spots) {
  markers = window.markers
  markers.forEach(m => m.setMap(null));
  markers = [];

  spots.forEach(spot => {
    if (!spot.address) {
      return;
    }

    geocoder.addressSearch(spot.address, function (result, status) {
      if (status !== kakao.maps.services.Status.OK) {
        return;
      }

      const coords = new kakao.maps.LatLng(result[0].y, result[0].x);

      const marker = new kakao.maps.Marker({
        map: map,
        position: coords
      });

      // HTML 요소 형태로 만들어야 class 토글 가능
      const overlayDiv = document.createElement("div");
      overlayDiv.className = "ouigo-overlay";
      overlayDiv.innerText = spot.title;

      const overlay = new kakao.maps.CustomOverlay({
        position: coords,
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

      // 🔵 클릭 시 모집글 리스트 불러오기
      kakao.maps.event.addListener(marker, "click", () => {
        loadRecruitListBySpot(spot.id);
      });

      markers.push(marker);
    });
  });

  window.markers = markers;
}