package com.example.currencyexchangeapp.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.currencyexchangeapp.R
import com.example.currencyexchangeapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BaseCurrencyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNavigationBottomBar()
    }

    private fun setupNavigationBottomBar() {
        val navigationController = findNavController(R.id.fragment_container)
        binding.bottomNavigation.setupWithNavController(navigationController)
    }
}