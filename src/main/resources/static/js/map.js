// Kakao Map Initialization
let map;
let markers = [];

document.addEventListener('DOMContentLoaded', function() {
    initMap();
});

function initMap() {
    const container = document.getElementById('map');
    const options = {
        center: new kakao.maps.LatLng(37.5665, 126.9780), // Seoul coordinates
        level: 5
    };
    
    map = new kakao.maps.Map(container, options);
    
    // Add zoom control
    const zoomControl = new kakao.maps.ZoomControl();
    map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
}

// Function to add marker on map
function addMarker(lat, lng, title) {
    const position = new kakao.maps.LatLng(lat, lng);
    
    const marker = new kakao.maps.Marker({
        position: position,
        map: map
    });
    
    // Add info window if title is provided
    if (title) {
        const infowindow = new kakao.maps.InfoWindow({
            content: `<div style="padding:5px;font-size:12px;">${title}</div>`
        });
        
        kakao.maps.event.addListener(marker, 'mouseover', function() {
            infowindow.open(map, marker);
        });
        
        kakao.maps.event.addListener(marker, 'mouseout', function() {
            infowindow.close();
        });
    }
    
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

// Function to search location
function searchLocation(keyword) {
    const ps = new kakao.maps.services.Places();
    
    ps.keywordSearch(keyword, function(data, status, pagination) {
        if (status === kakao.maps.services.Status.OK) {
            const bounds = new kakao.maps.LatLngBounds();
            
            clearMarkers();
            
            for (let i = 0; i < data.length; i++) {
                const marker = addMarker(
                    data[i].y, 
                    data[i].x, 
                    data[i].place_name
                );
                bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
            }
            
            map.setBounds(bounds);
        }
    });
}
