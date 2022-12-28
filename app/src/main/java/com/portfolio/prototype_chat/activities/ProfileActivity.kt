package com.portfolio.prototype_chat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityProfileBinding
import com.portfolio.prototype_chat.fragments.ProfileHomeFragment
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), ProfileHomeFragment.LogoutDetectionListener {
    
    private lateinit var binding: ActivityProfileBinding
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        toolbar.setNavigationIcon(R.drawable.ic_close)
        setSupportActionBar(binding.toolbar)
        
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(),
            fallbackOnNavigateUpListener = {
                finish()
                true
            }
        )
        navController.addOnDestinationChangedListener{_, _, _ ->
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        }
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
    
    override fun onLogout() {
        val auth = Firebase.auth
        auth.signOut()
        startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
        finishAffinity()
    }
    
//    fun setActionBar(@DrawableRes resId:Int){
//        val destinationId = navController.currentDestination?.id ?: return
//        val isTopLevelDestination = (destinationId in topLevelDestinationIds)
//        supportActionBar?.setDisplayHomeAsUpEnabled(isTopLevelDestination.not())
//
//    }
}