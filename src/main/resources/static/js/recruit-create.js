document.addEventListener('DOMContentLoaded', async () => {

  // ========================= 관광지 목록 로드 =========================
  async function loadTouristSpots() {
    const select = document.getElementById('touristSpotId');
    try {
      const res = await apiFetch('/api/v1/touristSpots');
      const result = await res.json();

      result.data?.forEach(spot => {
        const option = document.createElement('option');
        option.value = spot.id;
        option.textContent = spot.title;
        select.appendChild(option);
      });
    } catch (err) {
      console.error('관광지 목록 로딩 실패:', err);
    }
  }

  // ========================= 모집글 등록 =========================
  document.getElementById('btn-create').addEventListener('click', async () => {
    const recruitTitle = document.getElementById('recruitTitle').value.trim();
    const recruitContent = document.getElementById(
        'recruitContent').value.trim();
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const recruitCategory = document.getElementById('recruitCategory').value;
    const touristSpotId = 1; // ❗ 지금은 하드코딩 유지 (원래 로직 그대로)
    const touristSpotName = document.getElementById(
        'touristSpotName').value.trim();

    const genderCodes = [...document.querySelectorAll(
        'input[name="genderCodes"]:checked')]
    .map(e => e.value);
    const ageCodes = [...document.querySelectorAll(
        'input[name="ageCodes"]:checked')]
    .map(e => e.value);

    // 검증
    if (!recruitTitle || !recruitContent || !startDate || !endDate
        || !recruitCategory || !touristSpotId) {
      alert('모든 필수 항목을 입력해주세요.');
      return;
    }

    try {
      const res = await apiFetch('/api/v1/recruit', {
        method: 'POST',
        body: JSON.stringify({
          recruitTitle,
          recruitContent,
          startDate,
          endDate,
          recruitCategory,
          touristSpotId,
          genderCodes,
          ageCodes,
          touristSpotName
        }),
      });

      const result = await res.json();

      if (res.ok) {
        alert(result.message || '모집글이 등록되었습니다.');
        window.location.href = `/recruit/${result.data}`;
      } else {
        alert(result.message || '등록 실패');
      }

    } catch (err) {
      console.error('등록 실패:', err);
    }
  });

  // 첫 로딩
  await loadTouristSpots();
});