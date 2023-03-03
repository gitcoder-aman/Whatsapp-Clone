package com.tech.whatsappclone.Model

class MessageModel {
    private var uid:String?=null
    private var message:String?=null
    private var timestamp:Long?=null

    constructor(uid:String?,message:String?,timestamp:Long?){
        this.uid = uid
        this.timestamp = timestamp
        this.message = message
    }

    constructor(uid: String?, message: String?) {
        this.uid = uid
        this.message = message
    }

    constructor()
    fun setUid(uid: String?){
        this.uid = uid
    }
    fun getUid(): String? {
        return uid
    }
    fun setTimestamp(timestamp: Long?){
        this.timestamp = timestamp
    }
    fun getTimestamp():Long?{
        return timestamp
    }
    fun setMessage(message: String?){
        this.message = message
    }
    fun getMessage():String?{
        return message
    }




}