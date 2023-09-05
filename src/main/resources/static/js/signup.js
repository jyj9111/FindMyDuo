document.addEventListener("DOMContentLoaded", function () {
    new Vue({
        el: '#signup-app',
        data: {
            accountId: '',
            email: '',
            emailAuthCode: '',
            nickname: '',
            password: '',
            passwordCheck: '',
        },
        methods: {
            async sendEmailAuthCode() {
                // 이메일 인증번호 보내기 요청
                await axios.post('/users/email-auth', { email: this.email })
                    .then(response => {
                        alert('이메일로 인증번호가 전송되었습니다.');
                    })
                    .catch(error => {
                        console.error('이메일 인증번호 요청 에러: ', error);
                    });
            },
            async signup() {
                // 회원가입 요청
                await axios.post('/users/signup', {
                    accountId: this.accountId,
                    email: this.email,
                    emailAuthCode: this.emailAuthCode,
                    nickname: this.nickname,
                    password: this.password,
                    passwordCheck: this.passwordCheck
                })
                    .then(() => {
                        alert('회원가입이 완료되었습니다.\n'
                            + '구해듀오의 회원이 되신 것을 환영합니다.');
                        location.href = '/login' // 회원가입 완료 후 로그인 페이지로 이동
                    })
                    .catch(error => {
                        console.error('회원가입 에러: ', error);
                    });
            }
        }
    });
});
