document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById('login-in');
  const registerForm = document.getElementById('login-up');

  // 로그인
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
        alert('로그인 성공!');
        window.location.href = '/layout';
      } else {
        alert(data.message || '로그인 실패');
      }
    } catch (err) {
      console.error(err);
      alert('로그인 중 오류가 발생했습니다.');
    }
  });

  // 회원가입
  registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const memberId = registerForm.username.value.trim();
    const memberPassword = registerForm.password.value.trim();
    const confirmPassword = registerForm.confirmPassword.value.trim();
    const memberName = registerForm.nickname.value.trim();
    const memberEmail = registerForm.email.value.trim();
    const memberAge = registerForm.age.value.trim();
    const memberIntro = registerForm.introduce.value.trim();
    const genderInput = document.querySelector('input[name="gender"]:checked');
    const memberGender = genderInput ? genderInput.value : '';

    if (!memberId || !memberPassword || !confirmPassword || !memberName
        || !memberEmail) {
      return alert('필수 항목을 모두 입력하세요.');
    }
    if (memberPassword !== confirmPassword) {
      return alert('비밀번호가 일치하지 않습니다.');
    }
    if (!memberGender) {
      return alert('성별을 선택해주세요.');
    }

    try {
      const res = await fetch('/auth/signup', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
          memberId,
          memberPassword,
          memberName,
          memberEmail,
          memberAge,
          memberGender,
          memberIntro
        })
      });

      const data = await res.json();
      if (res.ok) {
        alert(data.message || '회원가입 성공! 로그인 페이지로 이동합니다.');
        toggleForm(false); // 로그인 폼으로 전환
      } else {
        alert(data.message || '회원가입 실패');
      }
    } catch (err) {
      console.error(err);
      alert('회원가입 중 오류가 발생했습니다.');
    }
  });
});

// 폼 토글
function toggleForm(isRegister) {
  const loginForm = document.getElementById('login-in');
  const registerForm = document.getElementById('login-up');
  loginForm.classList.toggle('none', isRegister);
  registerForm.classList.toggle('none', !isRegister);
}