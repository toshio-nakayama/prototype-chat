package com.portfolio.prototype_chat.utils

import android.content.Context
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UpdateUI(private val handler: Handler) {
    
    fun setTextAsync(textView: TextView, text: String?) {
        text ?: return
        handler.post {
            textView.text = text
        }
    }
    
    fun setImageAsync(
        context: Context, fullUrl: String?, @DrawableRes id: Int, view: ImageView,
    ) {
        fullUrl ?: return
        Firebase.storage.getReferenceFromUrl(fullUrl)
            .downloadUrl.addOnSuccessListener {
                handler.post {
                    Glide.with(context)
                        .load(it)
                        .placeholder(id)
                        .error(id)
                        .into(view)
                }
            }
        
    }
}