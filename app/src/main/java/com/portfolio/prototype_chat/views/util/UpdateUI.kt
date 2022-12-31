package com.portfolio.prototype_chat.views.util

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

fun setImage(context: Context, url: String?, @DrawableRes id: Int, view: ImageView) {
    url?.let {
        Firebase.storage.getReferenceFromUrl(url).downloadUrl.addOnSuccessListener {
            Glide.with(context).load(it).placeholder(id).error(id).into(view)
        }
    }
}