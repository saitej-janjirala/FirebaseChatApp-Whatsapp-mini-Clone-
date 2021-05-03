package com.saitejajanjirala.mychatapp.model

data class User(
    var username:String?=null,
    var phonenumber:String?=null,
    var email:String?=null,
    var instagram:String?=null,
    var facebook:String?=null,
    var uid:String?=null,
    var active:Boolean?=null,
    var picurl:String?=null,
    var coverurl:String?=null,
    var fcmtoken:String?=null
) {
}