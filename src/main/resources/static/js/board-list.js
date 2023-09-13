let token = localStorage.getItem('token');

new Vue({
    el: '#board-list-app',
    data: {
        content: [], // 게시판 목록을 저장할 데이터 속성
        sortBy: 'id', // 최초 로딩 시 최신순으로 초기화
        searchQuery: '', // 검색어를 저장할 데이터 속성
        searchBy: 'title', // 검색 기준 초기값 설정
        currentPage: 1, // 현재 페이지 번호 초기화
        totalPages: 1 // 전체 페이지 수 초기화
    },
    methods: {
        async fetchContent() {
            await axios.get(`/${this.sortBy === 'best' ? 'board/best' : this.sortBy === 'search' ? 'board/search' : 'board'}`, {
                params: {
                    page: this.currentPage - 1, // 현재 페이지 번호를 서버에 전달 (0부터 시작)
                    size: 20,
                    query: this.searchQuery, // 검색어 추가
                    searchBy: this.searchBy // 검색 기준 추가
                }
            })
                .then(res => {
                    this.content = res.data.content;
                    this.totalPages = res.data.totalPages; // 전체 페이지 수 업데이트
                    for (let i = 0; i < this.content.length; i++) {
                        console.log(this.content[i].modifiedAt);
                        let temp = processDate(this.content[i].modifiedAt);
                        this.content[i].modifiedAt = temp;
                    }
                })
                .catch(error => console.error(error));
        },
        async sortByPopular() {
            this.sortBy = 'best'; // '인기순' 버튼 클릭 시
            this.fetchContent();
        },
        async sortByLatest() {
            this.sortBy = 'id'; // '최신순' 버튼 클릭 시
            this.fetchContent();
        },
        async search() {
            this.sortBy = 'search'
            this.currentPage = 1; // 검색 버튼 클릭 시 현재 페이지를 1로 초기화
            this.fetchContent(); // 검색 수행
        },
        async redirectToWritePage() {
            console.log('token 확인 : ' + token);
            if (!token) {
                Swal.fire({
                    icon: 'error',
                    title: '로그인 후 이용해 주세요',
                    confirmButtonText: '확인'
                }).then((result) => {
                    if (result.isConfirmed) {
                        location.href = '/login';
                    }
                });
            } else {
                location.href = "/board/form/write"; // 게시판 작성페이지로 이동
            }
        },
        // 페이지 이동 함수 추가
        goToPage(page) {
            if (page >= 1 && page <= this.totalPages) {
                this.currentPage = page;
                this.fetchContent(); // 페이지 변경 시 데이터 가져오기
            }
        },
    },
    mounted() {
        this.fetchContent();
    }
});

function processDate (data) {
    const splitDate = data.split('T');
    const date = splitDate[0].split('-');
    const time = splitDate[1].split('.');

    return date[0]+'년 '+date[1]+'월 '+date[2]+'일 '+time[0];
}
