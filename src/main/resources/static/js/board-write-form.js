const token = localStorage.getItem('token');
const dataTransfer = new DataTransfer()

new Vue({
    el: "#div-write-data",
    data: {
        board: {},
        boardId: '',
        images: []
    },
    methods: {
        createBoard() {
            const formData = new FormData();

            const dto = {
                "title": this.board.title,
                "content": this.board.content
            };


            for(let i = 0; i < dataTransfer.files.length; i++){
                formData.append("file", dataTransfer.files[i]);
            }

            formData.append("dto", new Blob([JSON.stringify(dto)], {type: "application/json"}))

            if (confirm('게시글을 작성하시겠습니까?'))
                axios.post('/board', formData, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'multipart/form-data'
                    }
                })
                    .then(response => {
                        alert('게시글 작성 완료')
                        location.href = '/board/view';
                    })
                    .catch(error => {
                        alert('게시글 작성에 실패했습니다.');
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