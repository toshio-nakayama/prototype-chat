package com.portfolio.prototype_chat.home_ui.talk

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Talk(
    val userId:String = "",
    val userName: String = "",
    val photoName:String = "",
    val unreadCount:Int = 0,
    val lastMessage:String = "",
    val time:String = ""
)
