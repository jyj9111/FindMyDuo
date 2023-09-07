new Vue({
    el: '#board-list-app',
    data: {
        content: [], // 게시판 목록을 저장할 데이터 속성
        sortBy: 'id', // 최초 로딩 시 최신순으로 초기화
        searchQuery: '', // 검색어를 저장할 데이터 속성
        searchBy: 'title' // 검색 기준 초기값 설정
    },
    methods: {
        async fetchContent() {
            await axios.get(`/${this.sortBy === 'best' ? 'board/best' : this.sortBy === 'search' ? 'board/search' : 'board'}`, {
                params: {
                    page: 0,
                    size: 20,
                    query: this.searchQuery, // 검색어 추가
                    searchBy: this.searchBy // 검색 기준 추가
                }
            })
                .then(res => {
                    this.content = res.data.content;
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
            this.fetchContent(); // 검색 버튼 클릭 시 검색 수행
        },
        async redirectToWritePage() {
            location.href="/board/form/write" // 게시판 작성페이지로 이동
        }
    },
    mounted() {
        this.fetchContent();
    }
});