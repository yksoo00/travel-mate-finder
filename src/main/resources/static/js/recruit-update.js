document.addEventListener('DOMContentLoaded', async () => {
  const token = localStorage.getItem('accessToken');
  const recruitId = window.location.pathname.split('/').pop(); // ✅ 변경

  // async function loadTouristSpots(selectedId) {
  //   const select = document.getElementById('touristSpotId');
  //   try {
  //     const res = await fetch('/api/v1/touristSpots', {
  //       headers: {Authorization: `Bearer ${token}`},
  //     });
  //     const result = await res.json();
  //     result.data?.forEach(spot => {
  //       const option = document.createElement('option');
  //       option.value = spot.id;
  //       option.textContent = spot.title;
  //       if (spot.id === selectedId) {
  //         option.selected = true;
  //       }
  //       select.appendChild(option);
  //     });
  //   } catch (err) {
  //     console.error('관광지 목록 로딩 실패:', err);
  //   }
  // }

  async function loadRecruitData() {
    try {
      const res = await fetch(`/api/v1/recruit/${recruitId}`, {
        headers: {Authorization: `Bearer ${token}`},
      });
      const result = await res.json();
      const data = result.data;

      document.getElementById('recruitTitle').value = data.recruitTitle;
      document.getElementById('recruitContent').value = data.recruitContent;
      document.getElementById('startDate').value = data.startDate;
      document.getElementById('endDate').value = data.endDate;
      document.getElementById('recruitCategory').value = data.category;

      // await loadTouristSpots(data.touristSpotId);

      data.conditions?.forEach(c => {
        const el = document.querySelector(`input[value="${c.code}"]`);
        if (el) {
          el.checked = true;
        }
      });
    } catch (err) {
      console.error('모집글 불러오기 실패:', err);
    }
  }

  document.getElementById('btn-update').addEventListener('click', async () => {
    const recruitTitle = document.getElementById('recruitTitle').value.trim();
    const recruitContent = document.getElementById(
        'recruitContent').value.trim();
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const recruitCategory = document.getElementById('recruitCategory').value;
    const touristSpotId = 1;
    const genderCodes = [...document.querySelectorAll(
        'input[name="genderCodes"]:checked')].map(e => e.value);
    const ageCodes = [...document.querySelectorAll(
        'input[name="ageCodes"]:checked')].map(e => e.value);
    const touristSpotName = document.getElementById(
        'touristSpotName').value.trim();

    try {
      const res = await fetch(`/api/v1/recruit/${recruitId}`, {
        method: 'PUT',
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
        alert('수정 완료');
        window.location.href = `/recruit/${recruitId}`;
      } else {
        alert(result.message || '수정 실패');
      }
    } catch (err) {
      console.error('수정 실패:', err);
    }
  });

  await loadRecruitData();
});