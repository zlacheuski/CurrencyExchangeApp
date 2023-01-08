package com.example.currencyexchangeapp.view.fragment

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.example.currencyexchangeapp.databinding.FragmentCalculateBinding
import com.example.currencyexchangeapp.view.activity.BaseRateActivity

class CalculateFragment : Fragment() {

    private lateinit var binding: FragmentCalculateBinding
    private var menuHost: MenuHost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        showBackToolbarIcon()
        setToolbarOnBackPressed()
        initMenu()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding()
        return binding.root
    }

    private fun initMenu() {
        menuHost = requireActivity()
        initMenuProvider()
    }

    private fun initBinding() {
        binding = FragmentCalculateBinding.inflate(layoutInflater)
    }

    private fun hideNavigationBar() {
        baseCurrencyActivity().hideBottomBar()
    }

    private fun showBackToolbarIcon() {
        baseCurrencyActivity().showBackToolbarIcon()
    }

    private fun setToolbarOnBackPressed(){
        baseCurrencyActivity().getToolbar().setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun baseCurrencyActivity() = (activity as BaseRateActivity)

    private fun initMenuProvider() {
        menuHost?.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        })
    }
}