document.addEventListener("DOMContentLoaded", function() {
    const vm = new Vue({
        el: '#login-app',
        data: {
            accountId: '',
            password: ''
        }
    });
    const loginButton = document.getElementById("login-button");

    if(loginButton) {
        loginButton.addEventListener("click", async function() {
            try {
                // await isValidateToken(localStorage.getItem('token'));
                // 로그인 요청 보내기
                const response = await axios.post('/users/login', {
                    accountId: vm.accountId,
                    password: vm.password
                });

                const token = response.data.token;
                // 토큰이 로컬스토리지에 있는 경우를 로그인 했다고 판단
                // 후에 access token 부분 구현 후 추가 구현해야 함
                if(token != undefined) {
                    localStorage.setItem("token", token);
                    alert("로그인에 성공했습니다.")
                    console.log(localStorage.getItem('token'));
                } else {
                    alert(response.data)
                    // 로그인에 실패하면 login 페이지로 다시 이동
                    location.href = "/login";
                }

                // 프로필 정보를 스토리지에 담기 위해 조회 요청
                await axios.get('/users/mypage', {
                    // 헤더 설정
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                })
                    .then(response => {
                        console.log(response.data);
                        localStorage.setItem('profileImage', response.data.profileImage);
                        localStorage.setItem('accountId', response.data.accountId);
                        // 저장하는 것 까지 성공하면 홈페이지로 이동
                        location.href = '/main'
                    })
                    .catch(error => {
                        console.error('조회 에러', error);
                        // 조회 에러시 로그인 페이지로 이동
                        localStorage.removeItem(token);
                        location.href = '/login';
                    })
            } catch (error) {
                console.error('Login error: ', error);
            }
        });
    }
});