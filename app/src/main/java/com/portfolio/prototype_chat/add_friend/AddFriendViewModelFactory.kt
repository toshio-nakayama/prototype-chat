package com.portfolio.prototype_chat.add_friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddFriendViewModelFactory(val userId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddFriendViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}