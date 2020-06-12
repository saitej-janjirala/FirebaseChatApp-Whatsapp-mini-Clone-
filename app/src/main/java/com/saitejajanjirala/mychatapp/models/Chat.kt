package com.saitejajanjirala.mychatapp.models

class Chat {
    private var sender:String?=null
    private var message:String?=null
    private var reciever:String?=null
    private var isseen:Boolean?=null
    private var url:String?=null
    private var messageid:String?=null
    constructor()
    constructor(
        sender: String?,
        message: String?,
        reciever: String?,
        isseen: Boolean?,
        url: String?,
        messageid: String?
    ) {
        this.sender = sender
        this.message = message
        this.reciever = reciever
        this.isseen = isseen
        this.url = url
        this.messageid = messageid
    }
    fun getsender(): String? {
        return sender
    }
    fun setsender(sender:String?){
        this.sender=sender
    }
    fun getmessage(): String? {
        return message
    }
    fun setmessage(message:String?){
        this.message=message
    }
    fun getreciever():String?{
        return reciever
    }
    fun setreciever(reciever: String?){
        this.reciever=reciever
    }
    fun getisseen():Boolean?{
        return isseen
    }
    fun setisseen(isseen:Boolean?){
        this.isseen=isseen
    }
    fun geturl():String?{
        return url
    }
    fun seturl(url:String?){
        this.url=url
    }
    fun getmessageid():String?{
        return messageid
    }
    fun setmessageid(messageid: String?){
        this.messageid=messageid
    }


}