package com.saitejajanjirala.mychatapp.models

class Users{
    private var uid:String?=null
    private var username:String?=null
    private var profileurl:String?=null
    private var coverurl:String?=null
    private var status:String?=null
    private var search:String?=null
    private var facebook:String?=null
    private var instagram:String?=null
    private var website:String?=null
    constructor()
    constructor(
        uid: String,
        username: String,
        profileurl: String,
        coverurl: String,
        status: String,
        search: String,
        facebook: String,
        instagram: String,
        website: String
    ) {
        this.uid = uid
        this.username = username
        this.profileurl = profileurl
        this.coverurl = coverurl
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }
    fun getuid():String?{
        return uid
    }
    fun setuid(uid:String){
        this.uid=uid
    }
    fun getusername():String?{
        return username
    }
    fun setusername(username:String){
        this.username=username
    }
    fun getprofileurl():String?{
        return profileurl
    }
    fun setprofileurl(profileurl: String){
        this.profileurl=profileurl
    }
    fun getcoverurl():String?{
        return coverurl
    }
    fun setcoverurl(coverurl: String){
        this.coverurl=coverurl
    }
    fun getstatus():String?{
        return status
    }
    fun setstatus(status: String){
        this.status==status
    }
    fun getsearch():String?{
        return search
    }
    fun setsearch(search:String){
        this.search=search
    }
    fun getfacebook():String?{
        return facebook
    }
    fun setfacebook(facebook: String){
        this.facebook=facebook
    }
    fun getinstagram():String?{
        return instagram
    }
    fun setinstagram(instagram: String){
        this.instagram=instagram
    }
    fun getwebsite():String?{
        return website
    }
    fun setwebsite(website: String){
        this.website=website
    }






}