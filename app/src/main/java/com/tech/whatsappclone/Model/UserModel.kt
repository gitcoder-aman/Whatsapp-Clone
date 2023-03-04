package com.tech.whatsappclone.Model

open class UserModel() {
    private var profilePic : String ?=null
    private var username : String ?=null
    private var email : String ?=null
    private var password : String ?= null
    private var userId : String ?= null
    private var lastMessage : String ?= null
    private var about : String ?= null


    fun setAbout(about:String?){
        this.about = about
    }
    fun getAbout():String?{
        return about
    }
    fun getProfilePic(): String? {
        return profilePic
    }

    fun setProfilePic(profilePic: String?) {
        this.profilePic = profilePic
    }
    fun getUserName():String?{
        return username
    }
    fun setUserName(username:String?){
        this.username = username
    }
    fun getEmail():String?{
        return email
    }
    fun setEmail(email:String?){
        this.email = email
    }
    fun getPassword():String?{
        return password
    }
    fun setPassword(password:String?){
        this.password = password
    }
    fun getUserId():String?{
        return userId
    }
    fun setUserId(userId:String?){
        this.userId = userId
    }
    fun getLastMessage():String?{
        return lastMessage
    }
    fun setLastMessage(lastMessage:String?){
        this.lastMessage = lastMessage
    }
    data class UserSignup(
        var userName : String ?=null,
        var email : String ?=null,
        var password : String ?= null, )
}

