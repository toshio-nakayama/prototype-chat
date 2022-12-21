package com.portfolio.prototype_chat.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes

class ToastGenerator(
    builder: Builder
) {
    companion object {
        const val CENTER = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
    }

    val context: Context = builder.context
    val text: CharSequence = builder.text
    private val length: Int = builder.duration
    private val gravity: Int? = builder.gravity

    fun show() {
        val toast = Toast.makeText(context, text, length)
        gravity?.let { toast.setGravity(gravity, 0, 0) }
        gravity ?: let { toast.setGravity(CENTER, 0, 0) }
        toast.show()
    }

    class Builder(val context: Context) {
        var text: CharSequence = ""
        var duration: Int = Toast.LENGTH_SHORT
        var gravity: Int? = null

        fun resId(@StringRes resId: Int): Builder {
            this.text = context.getText(resId)
            return this
        }

        fun build() {
            ToastGenerator(this).show()
        }
    }

}