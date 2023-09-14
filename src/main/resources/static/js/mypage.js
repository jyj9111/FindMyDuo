import {isValidateToken,jwtExpireTime,reissueJwt} from "./keep-access-token.js";

let token = localStorage.getItem('token');

new Vue({
    el: '#app',
    data: {
        accountId: '',
        email: '',
        nickname: '',
        createdAt: '',
        lolNickname: '',
        profileImage: '',
        password: '',
        passwordCheck: '',
        linkingLolAccount: false, // 롤 계정 연동 중인지 여부를 나타내는 변수
        nicknameErrorMessage: '', // 닉네임 오류 메시지를 표시할 데이터
        emailErrorMessage: '', // 이메일 오류 메시지를 표시할 데이터
        isFormValid: false, // 수정 버튼
        emptyLolNickname: null
    },
    watch: {
        nickname: 'checkFormValidity',
        lolNickname: 'isEmptyLolNickname',
        nicknameErrorMessage: 'checkFormValidity',
        email: 'checkFormValidity',
        emailErrorMessage: 'checkFormValidity',
    },
    // 페이지 방문시 조회 기능
    async created() {
        if (!token) {
            // 토큰이 없는 경우 로그인 페이지로 이동
            location.href = '/login';
            return;
        }

        // 마이페이지 조회 요청
        token = await isValidateToken()
        await axios.get('/users/mypage', {
            // 헤더 설정
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            // 각 데이터를 현재 화면 데이터에 저장
            .then(response => {
                console.log(response.data);
                this.accountId = response.data.accountId;
                this.email = response.data.email;
                this.nickname = localStorage.getItem('nickname');
                this.createdAt = processDate(response.data.createdAt);
                this.lolNickname = response.data.lolNickname;
                this.profileImage = localStorage.getItem('profileImage');
                this.emptyLolNickname = this.lolNickname == '' || this.lolNickname == null ? true : false
            })
            .catch(error => {
                console.error('마이페이지 조회 에러: ', error);
                // 조회 에러시 메인 페이지로 이동
                location.href = '/main';
            });
    },
    methods: {
        // 닉네임 중복 확인 메서드
        async checkExistingNickname() {
            const nickname = this.nickname;
            const response = await axios.get('/users/check/nickname', {
                params: {nickname},
            })

            if(!response.data || localStorage.getItem('nickname') == this.nickname){
                this.nicknameErrorMessage = '';
            } else {
                this.nicknameErrorMessage = '이미 등록된 회원의 닉네임입니다.';
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
        // 회원 정보 수정
        async updateData() {
            // 수정된 데이터를 서버로 수정 요청
            const updateData = {
                accountId: this.accountId,
                email: this.email,
                nickname: this.nickname,
            };

            // 마이페이지 정보 수정 요청
            token = await isValidateToken()
            await axios.put('/users/mypage', updateData, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
                .then(response => {
                    Swal.fire({
                        icon: 'success',
                        title: '수정되었습니다',
                    });
                    console.log(response.data);
                    localStorage.setItem('nickname', this.nickname);
                })
                .catch(error => {
                    Swal.fire({
                        icon: 'error',
                        title: '수정에 실패하였습니다.',
                    });
                    console.error('마이페이지 수정 에러: ', error);
                })
        },
        // 비밀번호 변경
        async changePassword() {
            const changePasswordData = {
                password: this.password,
                passwordCheck: this.passwordCheck
            };

            // 비밀번호 변경 요청
            token = await isValidateToken()
            await axios.put('/users/mypage/change-password', changePasswordData, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })

                .then(response => {
                    Swal.fire({
                        icon: 'success',
                        title: '비밀번호가 변경되었습니다.',
                    });
                    console.log(response.data);
                })
                .catch(error => {
                    Swal.fire({
                        icon: 'error',
                        title: '수정에 실패하였습니다.',
                    });
                    console.error('비밀번호 변경 에러: ', error);

                })
        },
        // 롤 계정 연동 기능
        async linkLolAccount() {
            // 롤 계정 연동 시작 시 로딩 상태 활성화
            this.linkingLolAccount = true;

            try {
                // 계정 연동 요청
                token = await isValidateToken();

                const loadingModal = Swal.fire({
                    title: '로딩 중',
                    text: '계정 연동 중입니다...',
                    allowOutsideClick: false,
                    showConfirmButton: false,
                    onBeforeOpen: () => {
                        Swal.showLoading();
                    },
                });

                await axios.post('/lol/save', null, {
                    params: {
                        lolNickname: this.lolNickname.replaceAll(" ", "")
                    },
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }).then(response => {
                    loadingModal.close();
                    Swal.fire({
                        icon: 'success',
                        title: '계정 연동 완료',
                        text: '계정이 성공적으로 연동되었습니다.',
                    });
                })
                    .catch(error => {
                    loadingModal.close();
                    Swal.fire({
                        icon: 'error',
                        title: '계정 연동 실패',
                        text: error.response.data.message,
                    });
                });
            }
            finally {
                // 롤 계정 연동 종료 시 로딩 상태 비활성화
                this.linkingLolAccount = false;
            }
        },
        async selectImage(event) {
            this.profileImage = event.target.files[0];
        },
        // 프로필 이미지 등록 기능
        async uploadImage() {
            // FormData 객체를 생성하여 이미지 파일을 담는다.
            const formData = new FormData();
            formData.append('image', this.profileImage);

            // 프로필 이미지 업로드 요청
            token = await isValidateToken()
            await axios.put('/users/mypage/profile-image', formData, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    // 파일 업로드시 필요한 헤더
                    'Content-Type': 'multipart/form-data'
                },
            })
                .then(response => {
                    Swal.fire({
                        icon: 'success',
                        title: '프로필 이미지가 업로드되었습니다.'
                    });
                    localStorage.setItem('profileImage', response.data);

                    setTimeout(() => {
                        location.href = '/mypage';
                    }, 2000);
                })
                .catch(error => {
                    // alert('프로필 이미지 업로드 실패 ' + error.message);
                    Swal.fire({
                        icon: 'error',
                        title: '프로필 이미지 업로드 실패',
                        text: error.message
                    });
                    console.error('프로필 이미지 업로드 실패 ', error);
                })
        },
        // 회원 탈퇴 기능
        async deleteAccount() {
            const result = await Swal.fire({
                title: '회원 탈퇴',
                text: '정말로 회원을 탈퇴하시겠습니까?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: '네, 탈퇴하겠습니다.',
                cancelButtonText: '아니요',
            });

            if (result.isConfirmed) {
                // 탈퇴 요청
                token = await isValidateToken();

                try {
                    await axios.delete('/users/mypage', {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });

                    Swal.fire({
                        icon: 'success',
                        title: '회원 탈퇴 완료',
                        text: '회원 탈퇴가 성공적으로 완료되었습니다.',
                    });
                    localStorage.clear();
                    setTimeout(() => {
                        location.href = '/main';
                    }, 2000);

                } catch (error) {
                    Swal.fire({
                        icon: 'error',
                        title: '회원 탈퇴 실패',
                        text: '회원 탈퇴 중 오류가 발생했습니다: ' + error,
                    });
                }
            }
        },
        // 모든 입력 필드가 유효한지 확인하는 메서드
        checkFormValidity() {
            this.isFormValid =
                this.email.length > 0 &&
                this.nickname.length > 0 &&
                this.emailErrorMessage === '' &&
                this.nicknameErrorMessage === '';
        },
        // 롤 계정 연동부분에서 롤 닉네임이 입력되었는지 확인하는 메서드
        isEmptyLolNickname(){
            if(this.lolNickname == '' || this.lolNickname == null){
                this.emptyLolNickname = true
            }
            else this.emptyLolNickname = false
        }
    }
});

function processDate (data) {
    const splitDate = data.split('T');
    const date = splitDate[0].split('-');
    const time = splitDate[1].split('.');

    return date[0]+'년 '+date[1]+'월 '+date[2]+'일 '+time[0];
}