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
            accountIdErrorMessage: '', // 아이디 중복 확인 표시 데이터
            emailErrorMessage: '', // 이메일 오류 메시지를 표시할 데이터
            emailAuthCodeErrorMessage: '', // 이메일 확인 오류 메시지를 표시할 데이터
            nicknameErrorMessage: '', // 닉네임 오류 메시지를 표시할 데이터
            passwordErrorMessage: '', // 패스워드 오류 메시지를 표시할 데이터
            passwordCheckErrorMessage: '', // 패스워드 체크 오류 메시지를 표시할 데이터
            isFormValid: false, // 회원가입 버튼
        },
        watch: {
            accountId: 'checkFormValidity',
            email: 'checkFormValidity',
            emailAuthCode: 'checkFormValidity',
            nickname: 'checkFormValidity',
            password: 'checkFormValidity',
            passwordCheck: 'checkFormValidity',
            accountIdErrorMessage: 'checkFormValidity',
            emailErrorMessage: 'checkFormValidity',
            emailAuthCodeErrorMessage: 'checkFormValidity',
            nicknameErrorMessage: 'checkFormValidity',
            passwordErrorMessage: 'checkFormValidity',
            passwordCheckErrorMessage: 'checkFormValidity'
        },
        methods: {
            // 아이디 폼 확인 메서드
            async checkExistingAccount() {
                const accountId = this.accountId;
                const response = await axios.get('/users/check/accountId', {
                    params: {accountId},
                })

                console.log(response.data);

                if(response.data) {
                    this.accountIdErrorMessage = '이미 가입한 회원의 아이디입니다.'
                } else {
                    this.accountIdErrorMessage = '';
                }
            },
            // 이메일 폼 확인 메서드
            async checkEmailFormat() {
                // 이메일 형식 검사
                const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
                if(!emailRegex.test(this.email)) {
                    this.emailErrorMessage = '올바른 이메일 형식을 입력하세요.';
                } else {
                    this.emailErrorMessage = '';
                }
            },
            // 이메일 중복 확인 메서드
            async checkExistingEmail() {
                const email = this.email;
                const response = await axios.get('/users/check/email', {
                    params: {email},
                })

                console.log(response.data);

                if(response.data) {
                    this.emailErrorMessage = '이미 등록된 회원의 이메일입니다.';
                }
            },
            // 이메일 확인 폼 확인 메서드
            async checkEmailAuthCode() {
                const code = this.emailAuthCode;

                if(code.length != 6) {
                    this.emailAuthCodeErrorMessage = '인증번호 코드는 6글자입니다.';
                } else {
                    this.emailAuthCodeErrorMessage = '';
                }
            },
            async sendEmailAuthCode() {
                // 이메일 인증번호 보내기 요청
                await axios.post('/users/email-auth', {email: this.email})
                    .then(response => {
                        Swal.fire({
                            icon: 'success',
                            title: '이메일로 인증번호가 전송되었습니다.'
                        });
                    })
                    .catch(error => {
                        console.error('이메일 인증번호 요청 에러: ', error);
                    });
            },
            // 닉네임 폼 확인 메서드
            async checkExistingNickname() {
                const nickname = this.nickname;
                const response = await axios.get('/users/check/nickname', {
                    params: {nickname},
                })

                if(response.data) {
                    this.nicknameErrorMessage = '이미 등록된 닉네임입니다.';
                } else {
                    this.nicknameErrorMessage = '';
                }
            },
            // 패스워드 입력 폼 확인 메서드
            async checkPassword() {
                const password = this.password;

                if(password.length < 8) {
                    this.passwordErrorMessage = '비밀번호는 최소 8글자 입니다.'
                } else {
                    this.passwordErrorMessage = '';
                }

            },
            // 패스워드 체크 입력 폼 확인 메서드
            async checkPasswordCheck() {
                const passwordCheck = this.passwordCheck;

                if(this.password != passwordCheck) {
                    this.passwordCheckErrorMessage = '비밀번호와 비밀번호 확인이 다릅니다.'
                } else {
                    this.passwordCheckErrorMessage = '';
                }

            },
            // 모든 입력 필드가 유효한지 확인하는 메서드
            checkFormValidity() {
                this.isFormValid =
                    this.accountId.length > 0 &&
                    this.email.length > 0 &&
                    this.emailAuthCode.length > 0 &&
                    this.nickname.length > 0 &&
                    this.password.length > 0 &&
                    this.passwordCheck.length > 0 &&
                    this.accountIdErrorMessage === '' &&
                    this.emailErrorMessage === '' &&
                    this.emailAuthCodeErrorMessage === '' &&
                    this.nicknameErrorMessage === '' &&
                    this.passwordErrorMessage === '' &&
                    this.passwordCheckErrorMessage === '';
            },
            async signup() {
                // 회원가입 요청
                try {
                    await axios.post('/users/signup', {
                        accountId: this.accountId,
                        email: this.email,
                        emailAuthCode: this.emailAuthCode,
                        nickname: this.nickname,
                        password: this.password,
                        passwordCheck: this.passwordCheck
                    })

                    Swal.fire({
                        icon: 'success',
                        title: '회원가입이 완료되었습니다.\n' + '구해듀오의 회원이 되신 것을 환영합니다.',
                        showConfirmButton: true,
                    }).then((result) => {
                        if (result.isConfirmed) {
                            location.href = "/login";
                        }
                    });
                } catch (error) {
                    Swal.fire({
                        icon: 'error',
                        title: '회원가입 실패',
                        text: error.response.data.message
                    });
                }
            }
        }
    });
});
