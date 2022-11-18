package com.portfolio.prototype_chat.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.portfolio.prototype_chat.R

class ProfileHomeFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileHomeFragment()
    }

    private lateinit var viewModel: ProfileHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileHomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}