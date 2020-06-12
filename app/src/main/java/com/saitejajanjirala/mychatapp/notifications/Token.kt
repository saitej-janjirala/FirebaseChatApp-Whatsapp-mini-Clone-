package com.saitejajanjirala.mychatapp.notifications

class Token {
    private var token:String=""
    constructor(){}
    constructor(token: String) {
        this.token = token
    }
    fun gettoken():String?{
        return token
    }
    fun settokn(token:String){
        this.token=token
    }

}