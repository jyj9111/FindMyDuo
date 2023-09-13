import {isValidateToken} from "./keep-access-token.js";

let token = localStorage.getItem('token');
new Vue({
    el: '#board-bookmark-app',
    data: {
        content: [],
        currentPage: 1,
        totalPages: 1
    },
    created() {
      document.title = localStorage.getItem('nickname') + "의 북마크 - Find My Duo";
    },
    methods: {
        async fetchBoards() {
            token = await isValidateToken()
            await axios.get('/users/bookmark', {
                params:{
                    page: this.currentPage - 1,
                    size: 20
                },
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
                .then(response => {
                    this.content = response.data.content;
                    this.totalPages = response.data.totalPages; // 전체 페이지 수 업데이트
                })
                .catch(error => console.error(error));
        },
        goToPage(page) {
            if (page >= 1 && page <= this.totalPages) {
                this.currentPage = page;
                this.fetchBoards();
            }
        }
    },
    mounted() {
        this.fetchBoards();
    },

});