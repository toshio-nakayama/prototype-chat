package com.portfolio.prototype_chat.signup

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val name: String? = null,
    val email: String? = null,
    val statusMessage: String? = null,
    val photoUriPath: String? = null,
    val backgroundPhotoUriPath: String? = null
)
