import {isValidateToken} from "./keep-access-token.js";

let token = localStorage.getItem('token');
const dataTransfer = new DataTransfer()

new Vue({
    el: "#div-update-data",
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

        const url = window.location.href.split("/");
        this.boardId = url[url.length - 1];

        await axios.get('/board/' + this.boardId)
            .then(response => {
                this.board = response.data;
                this.nickname = response.data.nickname;
                this.images = response.data.images;
            })
            .catch((error) => {
                console.error(error)
            });

        document.title = this.board.title
    },
    methods: {
        async updateBoard() {
            const formData = new FormData();

            const dto = {
                "title": this.board.title,
                "content": this.board.content
            };


            for(let i = 0; i < dataTransfer.files.length; i++){
                formData.append("file", dataTransfer.files[i]);
            }

            formData.append("dto", new Blob([JSON.stringify(dto)], {type: "application/json"}))

            const result = await Swal.fire({
                title: '게시글 수정',
                text: '게시글을 수정하시겠습니까?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: '예',
                cancelButtonText: '아니요'
            });

            if (result.isConfirmed) {
                try {
                    token = await isValidateToken();
                    const response = await axios.put('/board/' + this.boardId, formData, {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'multipart/form-data'
                        }
                    });

                    // SweetAlert2를 사용하여 수정 성공 메시지 표시
                    await Swal.fire({
                        icon: 'success',
                        title: '게시글 수정 완료',
                    });

                    location.href = '/board/view/' + this.boardId;
                } catch (error) {
                    // SweetAlert2를 사용하여 오류 메시지 표시
                    await Swal.fire({
                        icon: 'error',
                        title: '게시글 수정에 실패했습니다.',
                    });
                    console.error(error.message);
                }
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