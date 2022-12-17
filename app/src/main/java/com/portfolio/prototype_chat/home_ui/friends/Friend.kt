package com.portfolio.prototype_chat.home_ui.friends

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Friend(
    val id:String,
    val name:String,
    val statusMessage:String,
    val photoName:String,
)
