console.log("map-marker-select.js 로드됨");

function displayMapMarkers(spots) {
  console.log("🔥 displaySelectMarkers: spot 개수 =", spots.length);

  spots.forEach(spot => {

    if (!spot.address) {
      console.warn("주소 없음 → 스킵:", spot.title);
      return;
    }

    window.geocoder.addressSearch(spot.address, (result, status) => {

      if (status !== kakao.maps.services.Status.OK) {
        console.warn("주소 변환 실패:", spot.address);
        return;
      }

      const coords = new kakao.maps.LatLng(result[0].y, result[0].x);

      const marker = new kakao.maps.Marker({
        map: window.map,
        position: coords
      });
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

      kakao.maps.event.addListener(marker, "click", () => {

        document.getElementById("touristSpotId").value = spot.id;
        document.getElementById("touristSpotName").value = spot.title;
        document.getElementById("map-address-display").value = spot.address;

        window.map.setCenter(coords);
      });

    });

  });
}