package com.example.currencyexchangeapp.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.currencyexchangeapp.databinding.FragmentCalculateBinding
import com.example.currencyexchangeapp.ui.activity.BaseRateActivity
import com.example.currencyexchangeapp.utils.Constants
import com.example.currencyexchangeapp.viewmodel.RateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalculateFragment : Fragment() {

    private lateinit var binding: FragmentCalculateBinding
    private var menuHost: MenuHost? = null
    private val menuProvider = object : MenuProvider {

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return true
        }
    }
    private val rateViewModel : RateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        showBackToolbarIcon()
        setToolbarOnBackPressed()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding()
        bind()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initMenu()
    }

    private fun initMenu() {
        menuHost = requireActivity()
        initMenuProvider()
    }

    override fun onStop() {
        super.onStop()
        destroyMenuProvider()
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

    private fun bind(){
        binding.userRateTv.text = rateViewModel.getSharedPref(Constants.USER_RATE_NAME)
        binding.rateTv.text = rateViewModel.getSharedPref(Constants.RATE_NAME)
        val rateValue = rateViewModel.getFloatSharedPref(Constants.RATE)

        binding.userRateEt.doAfterTextChanged{
            if(binding.userRateEt.text.toString() != "")
                binding.rateEt.setText(rateValue multiply binding.userRateEt.text.toString().toFloat())
        }
    }

    private infix fun Float.multiply(number: Float) : String{
        return (this*number).toString()
    }

    private fun baseCurrencyActivity() = (activity as BaseRateActivity)

    private fun initMenuProvider() {
        menuHost?.addMenuProvider(menuProvider)
    }

    private fun destroyMenuProvider() {
        menuHost?.removeMenuProvider(menuProvider)
    }
}