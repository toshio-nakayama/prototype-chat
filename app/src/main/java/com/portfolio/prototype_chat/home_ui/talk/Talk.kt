package com.portfolio.prototype_chat.home_ui.talk

data class Talk(
    val userId:String = "",
    val userName: String = "",
    val photoName:String = "",
    val unreadCount:String = "0",
    val lastMessage:String = "",
    val time:String = ""
)
