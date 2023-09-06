const token = localStorage.getItem('token');
const dataTransfer = new DataTransfer()

new Vue({
    el: "#div-update-data",
    data: {
        board: {},
        boardId: '',
        images: []
    },
    async created() {
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
        updateBoard() {
            const formData = new FormData();

            const dto = {
                "title": this.board.title,
                "content": this.board.content
            };


            for(let i = 0; i < dataTransfer.files.length; i++){
                formData.append("file", dataTransfer.files[i]);
            }

            formData.append("dto", new Blob([JSON.stringify(dto)], {type: "application/json"}))

            if (confirm('게시글을 수정하시겠습니까?'))
                axios.put('/board/' + this.boardId, formData, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'multipart/form-data'
                    }
                })
                    .then(response => {
                        // 수정 성공 시 처리
                        alert('게시글 수정 완료')
                        location.href = '/board/view/' + this.boardId;
                    })
                    .catch(error => {
                        alert('게시글 수정에 실패했습니다.');
                        console.log(error.message)
                    })
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