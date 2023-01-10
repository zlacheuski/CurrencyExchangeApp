package com.example.currencyexchangeapp.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangeapp.R
import com.example.currencyexchangeapp.databinding.FragmentRateBinding
import com.example.currencyexchangeapp.db.entity.Rates
import com.example.currencyexchangeapp.domain.model.states.Resource
import com.example.currencyexchangeapp.extension.hide
import com.example.currencyexchangeapp.extension.onSearchTextChanged
import com.example.currencyexchangeapp.extension.show
import com.example.currencyexchangeapp.utils.Constants
import com.example.currencyexchangeapp.utils.IRateAdapter
import com.example.currencyexchangeapp.utils.permission.Permission
import com.example.currencyexchangeapp.utils.permission.PermissionManager
import com.example.currencyexchangeapp.ui.activity.BaseRateActivity
import com.example.currencyexchangeapp.ui.adapter.RateAdapter
import com.example.currencyexchangeapp.viewmodel.RateViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class RateFragment : Fragment() {

    private val viewModel: RateViewModel by viewModels()
    private lateinit var binding: FragmentRateBinding
    private var currencyAdapter: RateAdapter? = null
    private var menuHost: MenuHost? = null
    private var menuItem: MenuItem? = null
    private val ratesList: MutableList<Rates> = mutableListOf()
    private val permissionManager = PermissionManager.from(this)
    private val fusedLocationClient: FusedLocationProviderClient
        get() = LocationServices.getFusedLocationProviderClient(activity as BaseRateActivity)

    private val locationResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Timber.d("***active")
                getLastKnownLocation()
            } else {
                Timber.d("***denied")
                askForPermission()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        collectCurrencies()
        collectCurrenciesFromDB()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initMenu()
        initMenuProvider()
        initRecyclerView()
        locationResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        showBottomBar()
        hideBackToolbarIcon()
    }

    private fun showBottomBar() {
        (activity as BaseRateActivity).showBottomBar()
    }

    private fun initBinding() {
        binding = FragmentRateBinding.inflate(layoutInflater)
    }

    private fun initMenu() {
        menuHost = requireActivity()
    }


    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Timber.d("*******")
                    val currentLocation = location.getRateCode()
                    val rateCode = getRateCode(currentLocation)
                    viewModel.addSharedPref(Constants.USER_RATE_NAME, rateCode)
                    getRatesFromDB(rateCode)
                }
            }
    }

    private fun getRateCode(currentLocation: String?) =
        Currency.getInstance(
            Locale.getAvailableLocales()
                .filter { it.country == currentLocation }[0]
        ).currencyCode ?: "USD"

    private fun Location.getRateCode(): String? {
        Geocoder(activity).getFromLocation(this.latitude, this.longitude, 1).let {
            if (it.size != 0) {
                return it[0].countryCode
            } else {
                return "US"
            }
        }
    }

    private fun createAdapterDelegateImp() = object : IRateAdapter {

        override fun updateRateState(rateName: String, isLiked: Boolean) {
            viewModel.updateRateState(rateName, isLiked)
            menuItem?.collapseActionView()
        }

        override fun addToSharedPreferences(rateName: String, rateValue: Double) {
            viewModel.addSharedPref(Constants.RATE_NAME, rateName)
            viewModel.addFloatSharedPref(Constants.RATE, rateValue.toFloat())
        }

        override fun getFromSharedPreferences(value: String): String? {
            return viewModel.getSharedPref(value)
        }

        override fun getFloatFromSharedPreferences(value: String): Float {
            return viewModel.getFloatSharedPref(value)
        }
    }

    private fun navigateToCalculateFragment() {
        findNavController().navigate(R.id.calculateFragment)
    }

    private fun searchCurrency(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getRatesByName(query).let { allRates ->
                withContext(Dispatchers.Main) {
                    currencyAdapter?.addDataToAdapter(allRates)
                }
            }
        }
    }

    private fun initMenuProvider() {
        menuHost?.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.searchItem -> {
                        this@RateFragment.menuItem = menuItem
                        (menuItem.actionView as SearchView).onSearchTextChanged {
                            searchCurrency(query = it)
                        }
                        true
                    }
                    else -> true
                }
            }
        })
    }

    private fun initRecyclerView() {
        currencyAdapter = RateAdapter(createAdapterDelegateImp(), ::navigateToCalculateFragment)
        val llm = LinearLayoutManager(activity?.baseContext)
        llm.orientation = RecyclerView.VERTICAL
        binding.recyclerView.adapter = currencyAdapter
        binding.recyclerView.layoutManager = llm
    }

    private fun getRates(rateName: String) = viewModel.getRates(rateName)

    private fun getRatesFromDB(rateName: String = "USD") {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            viewModel.getRatesDB().let { rates ->
                ratesList.clear()
                ratesList.addAll(rates)
                getRates(rateName)
            }
        }
    }

    private fun collectCurrenciesFromDB() {
        lifecycleScope.launch {
            viewModel.dbRates.collect {
                    currencyAdapter?.addDataToAdapter(it)
                }
        }
    }

    private fun collectCurrencies() {
        lifecycleScope.launchWhenCreated {
            viewModel.response.collect { state ->
                when (state) {
                    is Resource.Success -> {
                        binding.progressBar.hide()
                        Timber.d("Success: ${state.data}")
                            if (ratesList.isEmpty()) {
                                viewModel.insertRates(state.data.ratesList)
                            } else {
                                mutableListOf<Rates>().let { list->
                                    list.addAll(state.data.ratesList)
                                    list.attachLikesToList(ratesList)
                                    viewModel.insertRates(list)
                            }
                        }
                    }
                    is Resource.Progress -> {
                        binding.progressBar.show()
                        Timber.d("Progress")
                    }
                    is Resource.Error -> {
                        Timber.d("Error: ${state.errorData}")
                    }
                }
            }
        }
    }

    private fun List<Rates>.attachLikesToList(historyList: List<Rates>) {
        this.forEach { it2 ->
            if (it2.rateName in historyList.filter { it.isLiked }.map { it.rateName }) {
                it2.isLiked = true
            }
        }
    }
    private fun hideBackToolbarIcon() {
        (activity as BaseRateActivity).hideBackToolbarIcon()
    }

    private fun askForPermission() {
        permissionManager
            .request(Permission.Location)
            .rationale("Please, give a location permission in settings")
            .checkPermission { granted ->
                if (granted) {
                    success("Location service is active")
                } else {
                    error("Location permission is not given")
                    openSettings()
                }
            }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context?.packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun success(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun error(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}