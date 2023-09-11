async function jwtExpireTime(token) {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload).exp;
}

// ACCESS 토큰 재발급 해서 로컬 스토리지에 저장하는 함수
async function reissueJwt(token){
    let jwt = null
    try{
        await axios.get("/users/reissue-token",{
            headers:{
                "Authorization": "Bearer " + token
            }
        })
            .then(response =>{
                if(response.data.token != undefined){
                    jwt = response.data.token;
                    localStorage.setItem("token", jwt);
                }
                else throw new Error("에러!");
            })
    } catch (error){
        console.log(error.message)
        localStorage.clear()
    }

    return jwt
}

// ACCESS 토큰이 유효한 지 확인하고 유효하지 않으면 재발급 후 로컬 스토리지에 저장해주는 함수
async function isValidateToken(){
    const token = localStorage.getItem('token')
    if(token == null) return;
    else{
        const jwtExpire = await jwtExpireTime(token)
        const now = new Date().getTime() / 1000
        if(jwtExpire < now){
            const jwt = await reissueJwt(token)
            console.log(jwt)
            return jwt
        }
        return token
    }
}

export {jwtExpireTime, reissueJwt, isValidateToken};
