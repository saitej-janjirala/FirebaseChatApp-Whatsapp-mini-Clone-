package com.saitejajanjirala.mychatapp.models

class Chats {
    private var id:String?=null
    constructor()
    constructor(id: String?) {
        this.id = id
    }
    fun getid():String?{
        return id
    }
    fun setid(id:String){
        this.id=id
    }
}