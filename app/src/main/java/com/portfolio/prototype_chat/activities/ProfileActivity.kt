package com.portfolio.prototype_chat.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.portfolio.prototype_chat.R
import com.portfolio.prototype_chat.databinding.ActivityProfileBinding
import com.portfolio.prototype_chat.fragments.ProfileHomeFragment

class ProfileActivity : AppCompatActivity(), ProfileHomeFragment.LogoutDetectionListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentcontainer) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph)
            .setFallbackOnNavigateUpListener {
                finish()
                true
            }.build()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return findNavController(R.id.fragmentcontainer).navigateUp(appBarConfiguration)
//    }

    override fun onLogout() {
        val auth = Firebase.auth
        auth.signOut()
        startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
        finishAffinity()
    }
}