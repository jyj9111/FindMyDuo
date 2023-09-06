function jwtToAccountId(){
    const token = localStorage.getItem('token');
    if(token !== null){
        const payloadBase64 = token.split('.')[1];
        const payload = JSON.parse(atob(payloadBase64));
        const subject = payload.sub;
        return subject
    }
    else return null
}

export {jwtToAccountId};