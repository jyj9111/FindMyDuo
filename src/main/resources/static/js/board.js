import {
    jwtToAccountId
} from "./jwt-to-accountid.js";
import {
    isValidateToken
} from "./keep-access-token.js";

let token = localStorage.getItem('token');

new Vue({
    el: "#board-app",
    data: {
        boardId: '',
        comments: [],
        images: [],
        accountId: '',
        title: '',
        content: '',
        isAuthorizedUser: false, // 작성자인지 체크
        isUnAuthorizedUser: false, // 작성자 아닌지 체크(신고)
        isLike: '',
        isBookmark: '',
        nickName: '',
        modifiedAt: '',
        isReported: false,
        reported: '',
        isLoginAuthorizedUser: false  //로그인 한 유저인지 체크
    },
    async created() {
        const url = window.location.href.split("/");
        this.boardId = url[url.length - 1];

        await axios.get('/board/' + this.boardId)
            .then(response => {
                this.title = response.data.title;
                document.title = `${response.data.title}`;
                this.content = response.data.content;
                this.nickName = response.data.nickName;
                this.comments = response.data.comments;
                this.images = response.data.images;
                this.accountId = response.data.accountId;
                this.modifiedAt = processDate(response.data.modifiedAt);
                this.reported = response.data.reported;

                if (this.reported > 1) {
                    location.href = '/board/view';
                }

                console.log('게시판 수정시간 : ' + this.modifiedAt);

                // 댓글시간 포맷팅
                for (let i = 0; i < this.comments.length; i++) {
                    console.log(this.comments[i].modifiedAt);
                    let temp = processDate(this.comments[i].modifiedAt);
                    this.comments[i].modifiedAt = temp;
                }

                if (this.accountId === jwtToAccountId() && jwtToAccountId !== null) {
                    this.isAuthorizedUser = true;
                }

                if (this.accountId !== jwtToAccountId() && jwtToAccountId() !== null) {
                    this.isUnAuthorizedUser = true;
                }

                if (jwtToAccountId() !== null) {
                    this.isLoginAuthorizedUser = true;
                    console.log('로그인 유저 여부: ' + this.isLoginAuthorizedUser);
                }
            })
            .catch((error) => {
                alert(error.response.data.message);
                location.href = '/board/view';
            });
    },
    mounted() {
        document.title = this.title;
    },
    methods: {
        goBack() {
            history.back();
        },
        // 게시판 수정버튼 클릭시 수정페이지로
        async redirectToEditPage() {
            location.href = '/board/form/' + this.boardId;
        },
        // 게시판 삭제
        async deleteBoard() {
            const result = await Swal.fire({
                title: '게시글 삭제',
                text: '게시글을 삭제하시겠습니까?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: '예',
                cancelButtonText: '아니요'
            });
            if (result.isConfirmed) {
                try {
                    token = await isValidateToken();
                    await axios.delete('/board/' + this.boardId, {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        },
                    });

                    // SweetAlert2를 사용하여 삭제 성공 메시지 표시
                    await Swal.fire({
                        icon: 'success',
                        title: '게시글 삭제 완료',
                    });
                    location.href = '/board/view';

                } catch (error) {
                    // SweetAlert2를 사용하여 삭제 실패 메시지 표시
                    await Swal.fire({
                        icon: 'error',
                        title: '게시글 삭제 실패',
                        text: '게시글 삭제에 실패했습니다.',
                    });
                    console.error(error.message);
                }
            }
        },
        // 좋아요
        async likeBoard() {

            if (!token) {
                location.href = '/login';
                return;
            }

            const url = '/board/' + this.boardId + '/like';
            token = await isValidateToken()
            await axios.post(url,{}, {
                headers: {
                    'Authorization': `Bearer ${token}`
                },
            })
                .then(response => {
                    this.isLike = response.data.message;

                    if (this.isLike == '좋아요 처리 완료') {
                        Swal.fire({
                            icon: 'success',
                            title: '좋아요 처리 완료',
                            timer: 1000
                        });
                    }

                    if (this.isLike == '좋아요 취소 완료') {
                        Swal.fire({
                            icon: 'success',
                            title: '좋아요 취소 완료',
                            timer: 1000
                        });
                    }
                })
        },
        // 즐겨찾기
        async bookmarkBoard() {

            if (!token) {
                location.href = '/login';
                return;
            }

            const url = '/board/' + this.boardId + '/bookmark';
            token = await isValidateToken()
            await axios.post(url, {}, {
                headers: {
                    'Authorization': `Bearer ${token}`
                },
            })
                .then(response => {
                    this.isBookmark = response.data.message;

                    if (this.isBookmark == '즐겨찾기 처리완료') {
                        Swal.fire({
                            icon: 'success',
                            title: '즐겨찾기 처리 완료',
                            timer: 1000
                        });
                    }

                    if (this.isBookmark == '즐겨찾기 취소완료') {
                        Swal.fire({
                            icon: 'success',
                            title: '즐겨찾기 취소 완료',
                            timer: 1000
                        });
                    }
                })
        },
        // 신고
        async reportBoard() {
            if (this.isReported) {
                await this.showReportCancelDialog();
            } else {
                await this.showReportDialog();
            }
        },

        async showReportDialog() {
            const { value: message } = await Swal.fire({
                title: '게시글 신고',
                input: 'text',
                inputLabel: '신고 내용',
                inputPlaceholder: '신고 내용을 입력하세요...',
                inputValidator: (value) => {
                    if (!value) {
                        return '신고 내용을 입력해 주세요';
                    }
                },
                showCancelButton: true,
                confirmButtonText: '신고',
                cancelButtonText: '취소',
            });

            if (message) {
                await this.sendReportRequest(message);
            }
        },

        async showReportCancelDialog() {
            const result = await Swal.fire({
                icon: 'info',
                title: '게시글 신고 취소',
                text: '게시글 신고를 취소하시겠습니까?',
                showCancelButton: true,
                confirmButtonText: '예',
                cancelButtonText: '아니요',
            });

            if (result.isConfirmed) {
                await this.cancelReportRequest();
            }
        },

        async sendReportRequest(message) {
            const url = '/board/' + this.boardId + '/report';
            token = await isValidateToken();

            try {
                await axios.post(url, { 'content': message }, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                })
                    .then(response => {
                        this.reported = response.data.reported;
                        console.log(this.boardId);
                        console.log(url);
                        Swal.fire({
                            icon: 'success',
                            title: '신고 완료',
                            text: '게시글이 성공적으로 신고되었습니다.',
                        }).then((result) => {
                            this.isReported = true;
                            if (this.reported > 1) {
                                location.href = '/board/view';
                            }
                        })
                    })

            } catch (error) {
                console.error(error.message);
            }
        },

        async cancelReportRequest() {
            const url = '/board/' + this.boardId + '/report';
            token = await isValidateToken();

            try {
                await axios.post(url, {}, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                });

                await Swal.fire({
                    icon: 'success',
                    title: '게시글 신고 취소 완료',
                    text: '게시글 신고가 취소되었습니다.',
                });

                this.isReported = false;
            } catch (error) {
                console.error(error.message);
            }
        },
        // 댓글 작성
        async inputComment() {
            const url = '/board/' + this.boardId + '/comment';
            const content = document.getElementById("content").value
            token = await isValidateToken()
            await axios.post(url, {'content': content}, {
                headers: {
                    'Authorization': `Bearer ${token}`
                },
            })
                .then(response => {
                    this.nickname = response.data.nickname;
                    location.href = '/board/view/' + this.boardId;
                })
        },
        // 댓글 삭제
        async deleteComment(commentId, nickname) {
            const url = '/board/' + this.boardId + '/comment/' + commentId;

            const result = await Swal.fire({
                title: '댓글 삭제',
                text: '댓글을 삭제하시겠습니까?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: '삭제',
                cancelButtonText: '취소',
            });

            if (result.isConfirmed) {
                try {
                    token = await isValidateToken();
                    await axios.delete(url, {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        },
                    });

                    if (this.nickname === nickname && jwtToAccountId !== null) {
                        console.log('댓글 삭제 완료');
                        Swal.fire({
                            icon: 'success',
                            title: '댓글 삭제완료'
                        }).then((result) => {
                            if (result.isConfirmed) {
                                location.href = '/board/view/' + this.boardId;
                            }
                        });
                    }
                } catch (error) {
                    console.error(error.message);
                }
            }
        },
            isCommentAuthor(commentNickname) {
                const userNickname = localStorage.getItem('nickname');
                console.log(userNickname);
                return userNickname === commentNickname;
            }
        },
    }
);

        function processDate(data) {
    const splitDate = data.split('T');
    const date = splitDate[0].split('-');
    const time = splitDate[1].split('.');

    return date[0] + '년 ' + date[1] + '월 ' + date[2] + '일 ' + time[0];
}