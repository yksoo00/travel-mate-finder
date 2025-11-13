document.addEventListener('DOMContentLoaded', async () => {
  const token = localStorage.getItem('accessToken');

  async function loadTouristSpots() {
    const select = document.getElementById('touristSpotId');
    try {
      const res = await fetch('/api/v1/touristSpots', {
        headers: {Authorization: `Bearer ${token}`},
      });
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

  document.getElementById('btn-create').addEventListener('click', async () => {
    const recruitTitle = document.getElementById('recruitTitle').value.trim();
    const recruitContent = document.getElementById(
        'recruitContent').value.trim();
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const recruitCategory = document.getElementById('recruitCategory').value;
    const touristSpotId = 1;
    const touristSpotName = document.getElementById(
        'touristSpotName').value.trim();

    const genderCodes = [...document.querySelectorAll(
        'input[name="genderCodes"]:checked')].map(e => e.value);
    const ageCodes = [...document.querySelectorAll(
        'input[name="ageCodes"]:checked')].map(e => e.value);

    if (!recruitTitle || !recruitContent || !startDate || !endDate
        || !recruitCategory || !touristSpotId) {
      alert('모든 필수 항목을 입력해주세요.');
      return;
    }

    try {
      const res = await fetch('/api/v1/recruit', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
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

  await loadTouristSpots();
});