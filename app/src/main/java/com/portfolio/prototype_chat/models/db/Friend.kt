package com.portfolio.prototype_chat.models.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Friend(
    val id:String,
    val name:String,
    val statusMessage:String,
    val photoName:String,
)
