new Vue({
    el: '#home-app',
    data: {
        nickname: '',
        token: ''
    },
    created() {
        if (window.location.search !== "") {
            const urlParams= new URLSearchParams(location.search);
            this.token = urlParams.get('token');
            axios.get('/users/oauth', {params: {token: this.token}})
                .then(response => {
                    localStorage.setItem('token', response.data.token);
                    localStorage.setItem('nickname', response.data.nickname);
                    location.href='/main';
                }).catch(error => {
                const message=error.message;
                alert(message);
                console.log('oauth-login error='+message);
            })
        }
    }
});