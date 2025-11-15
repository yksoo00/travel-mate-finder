document.addEventListener('DOMContentLoaded', async () => {
  const recruitId = window.location.pathname.split('/').pop(); // URL 마지막 segment

  // ==============================
  // 모집글 데이터 로드
  // ==============================
  async function loadRecruitData() {
    try {
      const res = await apiFetch(`/api/v1/recruit/${recruitId}`);
      const result = await res.json();
      const data = result.data;

      document.getElementById('recruitTitle').value = data.recruitTitle;
      document.getElementById('recruitContent').value = data.recruitContent;
      document.getElementById('startDate').value = data.startDate;
      document.getElementById('endDate').value = data.endDate;
      document.getElementById('recruitCategory').value = data.category;

      // 조건 체크박스 채우기
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

  // ==============================
  // 수정 요청
  // ==============================
  document.getElementById('btn-update').addEventListener('click', async () => {

    const recruitTitle = document.getElementById('recruitTitle').value.trim();
    const recruitContent = document.getElementById(
        'recruitContent').value.trim();
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const recruitCategory = document.getElementById('recruitCategory').value;
    const touristSpotId = document.getElementById('touristSpotId').value;
    const touristSpotName = document.getElementById(
        'touristSpotName').value.trim();

    const genderCodes = [...document.querySelectorAll(
        'input[name="genderCodes"]:checked')]
    .map(e => e.value);
    const ageCodes = [...document.querySelectorAll(
        'input[name="ageCodes"]:checked')]
    .map(e => e.value);

    try {
      const res = await apiFetch(`/api/v1/recruit/${recruitId}`, {
        method: 'PUT',
        body: JSON.stringify({
          recruitTitle,
          recruitContent,
          startDate,
          endDate,
          recruitCategory,
          touristSpotId,
          genderCodes,
          ageCodes
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