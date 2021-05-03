package com.saitejajanjirala.mychatapp.model

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Chat(
    var senderid:String?=null,
    var receiverid:String?=null,
    var imageurl:String?=null,
    var message:String?=null,
    var seen:Boolean=false,
    var timestamp:Long?=null,
    var deleted:Boolean=false,
    var chatid:String?=null
) {
}