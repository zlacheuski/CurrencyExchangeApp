package com.example.currencyexchangeapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.currencyexchangeapp.R
import com.example.currencyexchangeapp.databinding.ActivityMainBinding
import com.example.currencyexchangeapp.extension.hide
import com.example.currencyexchangeapp.extension.show
import com.example.currencyexchangeapp.utils.EncryptedSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BaseRateActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPref: EncryptedSharedPreferences
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNavigationBottomBar()
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    private fun setupNavigationBottomBar() {
        val navigationController = findNavController(R.id.fragment_container)
        binding.bottomNavigation.setupWithNavController(navigationController)
    }

    fun hideBottomBar() {
        binding.bottomNavigation.hide()
    }

    fun showBottomBar() {
        binding.bottomNavigation.show()
    }

    fun showBackToolbarIcon() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun hideBackToolbarIcon() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun getToolbar() = binding.toolbar
}