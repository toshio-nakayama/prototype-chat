package com.portfolio.prototype_chat.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivitySplashBinding
import com.portfolio.prototype_chat.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding
    private lateinit var animation: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animation = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })
    }

    override fun onStart() {
        super.onStart()
        binding.textViewAppName.startAnimation(animation)
    }

}