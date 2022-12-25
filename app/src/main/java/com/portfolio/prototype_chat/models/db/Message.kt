package com.portfolio.prototype_chat.models.db

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ServerValue

@IgnoreExtraProperties
data class Message(
    val messageId: String? = "",
    val message: String? = "",
    val messageFrom: String? = "",
    val messageTime: Long = 0
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "messageId" to messageId,
            "message" to message,
            "messageFrom" to messageFrom,
            "messageTime" to ServerValue.TIMESTAMP
        )
    }

}