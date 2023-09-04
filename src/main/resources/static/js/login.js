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

                    // 로그인에 성공하면 main 페이지로 이동
                    location.href = "/main";
                } else {
                    alert(response.data)

                    // 로그인에 실패하면 login 페이지로 다시 이동
                    location.href = "/login";
                }
            } catch (error) {
                console.error('Login error: ', error);
            }
        });
    }
});