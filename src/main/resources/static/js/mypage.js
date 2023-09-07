const token = localStorage.getItem('token');

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
    },
    // 페이지 방문시 조회 기능
    async created() {
        if (!token) {
            // 토큰이 없는 경우 로그인 페이지로 이동
            alert('로그인 후 이용해주세요.')
            location.href = '/login';
            return;
        }

        // 마이페이지 조회 요청
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
                this.nickname = response.data.nickname;
                this.createdAt = response.data.createdAt;
                this.lolNickname = response.data.lolNickname;
                this.profileImage = response.data.profileImage;
            })
            .catch(error => {
                console.error('마이페이지 조회 에러: ', error);
                // 조회 에러시 메인 페이지로 이동
                location.href = '/main';
            });
    },
    methods: {
        // 회원 정보 수정
        async updateData() {
            // 수정된 데이터를 서버로 수정 요청
            const updateData = {
                accountId: this.accountId,
                email: this.email,
                nickname: this.nickname,
                password: this.password,
                passwordCheck: this.passwordCheck,
            };

            // 마이페이지 정보 수정 요청
            await axios.put('/users/mypage', updateData, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
                .then(response => {
                    alert('수정되었습니다.');
                    console.log(response.data);
                })
                .catch(error => {
                    alert('수정이 실패하였습니다.');
                    console.error('마이페이지 수정 에러: ', error);

                })
        },
        // 롤 계정 연동 기능
        async linkLolAccount() {
            // 계정 연동 요청
            await axios.post('/lol/save', null, {
                params: {
                    lolNickname: this.lolNickname.replaceAll(" ", "")
                },
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
                .then(response => {
                    alert('계정 연동이 완료되었습니다.');
                })
                .catch(error => {
                    alert('계정 연동이 실패하였습니다.');
                    console.error('계정 연동 실패: ', error);
                })
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
            await axios.put('/users/mypage/profile-image', formData, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    // 파일 업로드시 필요한 헤더
                    'Content-Type': 'multipart/form-data'
                },
            })
                .then(response => {
                    alert('프로필 이미지가 업로드되었습니다.')
                    localStorage.setItem('profileImage', response.data.profileImage);
                    // 프로필 이미지 등록시 마이페이지 재로드해서 변경사항 확인
                    location.href = '/mypage';
                })
                .catch(error => {
                    alert('프로필 이미지 업로드 실패 ' + error.message);
                    console.error('프로필 이미지 업로드 실패 ', error);
                })
        },
        // 회원 탈퇴 기능
        async deleteAccount() {
            if (confirm('정말로 회원을 탈퇴하시겠습니까?')) {
                // 탈퇴 요청
                await axios.delete('/users/mypage', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                })
                    .then(() => {
                        alert('회원 탈퇴가 완료되었습니다.');
                        localStorage.clear();
                        location.href = '/main';
                    })
                    .catch(error => {
                        alert('회원 탈퇴 실패: ' + error);
                    })
            }
        }
    }
});