package com.portfolio.prototype_chat.models.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Talk(
    val userId: String? = "",
    val userName: String? = "",
    val photo: String? = null,
    val unreadCount: Int = 0,
    val lastMessage: String? = "",
    val time: String? = "",
)
