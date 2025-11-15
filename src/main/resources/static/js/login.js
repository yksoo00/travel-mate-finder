document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById('login-in');
  const registerForm = document.getElementById('login-up');

  // ------------------------
  // 로그인 처리
  // ------------------------
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const memberId = loginForm.username.value.trim();
    const memberPassword = loginForm.password.value.trim();

    if (!memberId || !memberPassword) {
      return alert('아이디와 비밀번호를 입력하세요.');
    }

    try {
      const res = await fetch('/auth/login', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({memberId, memberPassword})
      });

      const data = await res.json();

      if (res.ok) {
        localStorage.setItem('accessToken', data.data.accessToken);
        localStorage.setItem('refreshToken', data.data.refreshToken);
        localStorage.setItem('memberId', memberId);

        console.log("member = ", memberId);

        window.location.href = '/layout'; // 로그인 후 이동 경로
      } else {
        alert(data.message || '로그인 실패');
      }
    } catch (err) {
      console.error(err);
      alert('로그인 중 오류가 발생했습니다.');
    }
  });

  // ------------------------
  // 회원가입 처리
  // ------------------------
  registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // ------------------------
    // 폼 데이터 가져오기
    // ------------------------
    const data = {
      memberId: registerForm.username.value.trim(),
      memberPassword: registerForm.password.value.trim(),
      confirmPassword: registerForm.confirmPassword.value.trim(),
      memberName: registerForm.nickname.value.trim(),
      memberEmail: registerForm.email.value.trim(),
      memberAge: registerForm.age.value.trim(),
      memberGender: (() => {
        const genderNodeList = registerForm.gender;
        for (const radio of genderNodeList) {
          if (radio.checked) {
            return radio.value === 'male' ? 'M' : 'F';
          }
        }
        return null;
      })(),
      memberIntro: registerForm.introduce.value.trim()
    };

    // ------------------------
    // 유효성 체크
    // ------------------------
    if (!data.memberId || !data.memberPassword || !data.confirmPassword
        || !data.memberName || !data.memberEmail) {
      return alert('필수 항목을 모두 입력하세요.');
    }

    if (data.memberPassword !== data.confirmPassword) {
      return alert('비밀번호가 일치하지 않습니다.');
    }

    if (!data.memberGender) {
      return alert('성별을 선택해주세요.');
    }

    // ------------------------
    // 서버 전송
    // ------------------------
    try {
      const res = await fetch('/auth/signup', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
          memberId: data.memberId,
          memberPassword: data.memberPassword,
          memberName: data.memberName,
          memberEmail: data.memberEmail,
          memberAge: data.memberAge,
          memberGender: data.memberGender,
          memberIntro: data.memberIntro
        })
      });

      const result = await res.json();

      if (res.ok) {
        alert(result.message || '회원가입 성공! 로그인 페이지로 이동합니다.');
        toggleForm(false); // 로그인 폼으로 전환
      } else {
        alert(result.message || '회원가입 실패');
      }
    } catch (err) {
      console.error(err);
      alert('회원가입 중 오류가 발생했습니다.');
    }
  });
});

// ------------------------
// 폼 토글 함수
// ------------------------
function toggleForm(isRegister) {
  const loginForm = document.getElementById('login-in');
  const registerForm = document.getElementById('login-up');

  if (isRegister) {
    loginForm.classList.add('none');
    registerForm.classList.remove('none');
  } else {
    loginForm.classList.remove('none');
    registerForm.classList.add('none');
  }
}