document.addEventListener('DOMContentLoaded', async () => {
  if (window.recruitDetailLoaded) {
    return;
  }
  window.recruitDetailLoaded = true;
  const container = document.getElementById('recruitDetailContainer');
  const recruitId = container?.dataset.recruitId;

  if (!recruitId) {
    console.error("recruitId가 없습니다.");
    return;
  }

  const token = localStorage.getItem('accessToken');
  const loggedInMemberId = localStorage.getItem('memberId'); // ✅ 로그인한 사용자 ID

  const conditionMap = {
    G100: "남자",
    G101: "여자",
    C100: "10대",
    C101: "20대",
    C102: "30대",
    C103: "40대",
    C104: "50대",
    C105: "60대"
  };

  try {
    const response = await fetch(`/api/v1/recruit/${recruitId}`, {
      headers: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error('상세 조회 실패');
    }

    const result = await response.json();
    const data = result.data;

    // ✅ 여행장 정보 표시
    const ownerInfo = document.getElementById('recruitOwnerInfo');
    if (ownerInfo && data.memberName) {
      ownerInfo.innerHTML = '';
      const div = document.createElement('div');
      const genderText = data.memberGender === 'M' ? '남' : '여';
      const genderClass = data.memberGender === 'M' ? 'male' : 'female';
      div.className = `tag ${genderClass}`;
      div.innerHTML = `👤 ${data.memberName} (${genderText}, ${data.memberAge}세)`;
      ownerInfo.appendChild(div);
    }

    // ✅ 기본 데이터 세팅
    document.getElementById(
        'recruitTitle-client').textContent = data.recruitTitle;
    document.getElementById(
        'recruitContent-client').textContent = data.recruitContent;
    document.getElementById(
        'recruitPeriod-client').textContent = `${data.startDate} ~ ${data.endDate}`;
    document.getElementById(
        'touristSpotAddress-client').textContent = data.touristSpotAddress;

    // ✅ 조건 렌더링
    const conditionsList = document.getElementById('conditionsList-client');
    conditionsList.innerHTML = '';
    data.conditions?.forEach(c => {
      const li = document.createElement('li');
      li.textContent = conditionMap[c.code?.toUpperCase()] || c.code;
      li.className = 'tag';
      conditionsList.appendChild(li);
    });

    // ✅ 동행 유형
    const categoryEl = document.getElementById('categoryText-client');
    categoryEl.textContent = data.category || '미정';
    categoryEl.className = 'tag type';

// ✅ 같이 가는 사람 (APPROVED만 표시)
    const approvalsList = document.getElementById('approvalsList-client');
    approvalsList.innerHTML = '';

    const approvedList = data.approvals?.filter(a => a.status === 'APPROVED')
        || [];

    if (approvedList.length === 0) {
      approvalsList.innerHTML = `<li class="tag">승인된 동행자가 없습니다</li>`;
    } else {
      approvedList.forEach(a => {
        const li = document.createElement('li');
        const genderClass = a.memberGender === 'M' ? 'male' : 'female';
        const gender = a.memberGender === 'M' ? '남' : '여';

        li.className = `tag ${genderClass}`;
        li.textContent = `${a.memberName} (${gender}, ${a.memberAge}세)`;

        approvalsList.appendChild(li);
      });
    }

    // ✅ 본인 글이면 수정/삭제 버튼 표시
    const actionWrap = document.querySelector('.action-wrap');
    if (loggedInMemberId && data.memberId && loggedInMemberId
        === data.memberId) {
      const editBtn = document.createElement('button');
      editBtn.id = 'btn-edit';
      editBtn.className = 'btn btn-secondary';
      editBtn.textContent = '수정하기';
      editBtn.addEventListener('click', () => {
        window.location.href = `/recruit/recruitUpdatePage/${recruitId}`;
      });

      const deleteBtn = document.createElement('button');
      deleteBtn.id = 'btn-delete';
      deleteBtn.className = 'btn btn-danger';
      deleteBtn.textContent = '삭제하기';
      deleteBtn.addEventListener('click', async () => {
        if (!confirm('정말 삭제하시겠습니까?')) {
          return;
        }
        try {
          const res = await fetch(`/api/v1/recruit/${recruitId}`, {
            method: 'DELETE',
            headers: {'Authorization': `Bearer ${token}`}
          });
          const result = await res.json();
          if (res.ok) {
            alert(result.message || '삭제 성공');
            window.location.href = '/layout';
          } else {
            alert(result.message || '삭제 실패');
          }
        } catch (err) {
          console.error('삭제 오류:', err);
          alert('서버 통신 중 오류가 발생했습니다.');
        }
      });

      const applyBtn = document.getElementById('btn-apply');
      actionWrap.insertBefore(deleteBtn, applyBtn);
      actionWrap.insertBefore(editBtn, applyBtn);
    }

    // ✅ 참여하기 버튼
    const applyBtn = document.getElementById('btn-apply');
    applyBtn.addEventListener('click', async () => {
      if (!token) {
        alert('로그인이 필요합니다.');
        window.location.href = '/loginForm';
        return;
      }

      if (!confirm('이 모집에 참여하시겠습니까?')) {
        return;
      }

      try {
        const res = await fetch(
            `${window.location.origin}/api/v1/recruit/participation/${recruitId}`,
            {
              method: 'POST',
              headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
              }
            });

        const result = await res.json();
        if (res.ok) {
          alert(result.message || '참여 신청 되었습니다.');
          window.location.href = '/layout';
        } else {
          const msg = result.message || '참여 실패';
          if (msg.includes('이미 신청')) {
            alert('이미 참여한 모집글입니다.');
          } else {
            alert(msg);
          }
        }
      } catch (err) {
        console.error(err);
        alert('참여 중 오류가 발생했습니다.');
      }
    });

  } catch (err) {
    console.error('Error:', err);
  }
});