const loginForm = document.getElementById('loginForm');
const loginError = document.getElementById('loginError');

if (loginForm) {
  loginForm.addEventListener('submit', async function (e) {
    e.preventDefault();

    if (loginError) {
      loginError.style.display = 'none';
      loginError.textContent = '';
    }

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!email || !password) {
      showLoginError('Completează emailul și parola.');
      return;
    }

    try {
      let userData = null;

      if (window.MockDB) {
        const result = window.MockDB.login(email, password);

        if (!result.ok) {
          showLoginError(result.message || 'Email sau parolă incorectă.');
          return;
        }

        userData = result.data;
      } else {
        const response = await fetch('/api/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
          showLoginError('Email sau parolă incorectă.');
          return;
        }

        userData = await response.json();
      }

      sessionStorage.setItem('cr_session', JSON.stringify(userData));

      const role = String(userData.user_role || '').trim().toLowerCase();

      switch (role) {
        case 'dispatcher':
          window.location.href = 'dispDash.html';
          break;

        case 'citizen':
        case 'persoana_fizica':
        case 'persoanafizica':
          window.location.href = 'feed.html';
          break;

        case 'globaladmin':
          window.location.href = 'adminDash.html';
          break;

        case 'institution':
          window.location.href = 'institutionDash.html';
          break;

        default:
          showLoginError('Rol de utilizator necunoscut.');
      }
    } catch (error) {
      console.error('Login error:', error);
      showLoginError('A apărut o eroare la autentificare.');
    }
  });
}

function showLoginError(message) {
  if (!loginError) return;
  loginError.textContent = message;
  loginError.style.display = 'block';
}