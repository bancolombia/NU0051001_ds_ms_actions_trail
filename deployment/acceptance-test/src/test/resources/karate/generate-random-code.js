function fn(length) {
    var charactersAndNumbers = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    var charactersLength = charactersAndNumbers.length;
    var result = '';
    for ( var i = 0; i < length; i++ ) {
        result += charactersAndNumbers.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}