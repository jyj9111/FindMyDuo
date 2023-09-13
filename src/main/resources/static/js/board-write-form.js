import {isValidateToken} from "./keep-access-token.js";

let token = localStorage.getItem('token');
const dataTransfer = new DataTransfer()

new Vue({
    el: "#div-write-data",
    data: {
        board: {},
        boardId: '',
        images: []
    },
    async created() {
        if (!token) {
            alert('로그인 후 이용해주세요.')
            location.href = '/login';
            return;
        }
    },
    methods: {
        async createBoard() {
            const formData = new FormData();

            const dto = {
                "title": this.board.title,
                "content": this.board.content
            };


            for(let i = 0; i < dataTransfer.files.length; i++){
                formData.append("file", dataTransfer.files[i]);
            }

            formData.append("dto", new Blob([JSON.stringify(dto)], {type: "application/json"}))

            try {
                token = await isValidateToken();

                const response = await axios.post('/board', formData, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'multipart/form-data'
                    }
                });
                location.href = `/board/view`;
            } catch (error) {
                // SweetAlert2를 사용하여 오류 메시지 표시
                await Swal.fire({
                    icon: 'error',
                    title: '게시글 작성 실패',
                });
                console.error(error.message);
            }
        },
        handleFileUpload(event){
            let files = event.target.files
            if(files != null && files.length > 0){

                for(var i=0; i<files.length; i++){
                    dataTransfer.items.add(files[i])
                }
                document.getElementById("input-images").files = dataTransfer.files;
            }
        }
    }
});