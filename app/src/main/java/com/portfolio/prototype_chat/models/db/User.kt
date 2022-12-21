package com.portfolio.prototype_chat.models.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val name: String = "",
    val email: String = "",
    val statusMessage: String = "",
    val photoUriPath: String = "",
    val backgroundPhotoUriPath: String = ""
)
