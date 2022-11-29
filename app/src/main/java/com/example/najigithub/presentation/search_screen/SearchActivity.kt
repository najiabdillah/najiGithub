package com.example.najigithub.presentation.search_screen

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.najigithub.R
import com.example.najigithub.databinding.ActivitySearchBinding
import com.example.najigithub.domain.adapter.SearchAdapter
import com.example.najigithub.domain.utils.*
import com.example.najigithub.domain.utils.Extensions.CATEGORY
import com.example.najigithub.domain.utils.Extensions.ON_QUERY_TEXT_LISTENER
import com.example.najigithub.domain.utils.Extensions.SEARCH_CATEGORY
import com.example.najigithub.domain.utils.Extensions.USERNAME
import com.example.najigithub.presentation.detail_screen.DetailActivity
import com.example.najigithub.presentation.setting_state.SettingViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private var _binding: ActivitySearchBinding? = null

    private val binding get() = _binding as ActivitySearchBinding

    private val viewModel: SearchViewModel by viewModel()

    private val settingViewModel: SettingViewModel by viewModel()

    private var searchAdapter: SearchAdapter? = null

    private var resultData: String? = null

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        searchAdapter = SearchAdapter.instance()
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        initRecyclerView()

        initStateLaunch()

        settingViewModel.getModeSetting()

        if (savedInstanceState != null) {
            val username = savedInstanceState.getString(USERNAME)
            if (username == null) {
                sessionManager.getUsername.asLiveData().observe(this) { userData ->
                    viewModel.getSearch(userData) { itemState ->
                        searchAdapter?.differ?.submitList(itemState.items)
                    }
                }
            } else {
                viewModel.getSearch(username) { itemState ->
                    searchAdapter?.differ?.submitList(itemState.items)
                }
            }
        } else {
            sessionManager.getUsername.asLiveData().observe(this) { usernameSession ->
                with(binding) {
                    if (usernameSession.isNullOrBlank() || usernameSession.isNullOrEmpty()) {
                        lyAnimationEmpty.showView()
                        lottieAnimationView.playAnimation()
                    } else {
                        lyAnimationEmpty.removeView()
                        lottieAnimationView.pauseAnimation()
                        viewModel.getSearch(usernameSession) { itemState ->
                            searchAdapter?.differ?.submitList(itemState.items)
                        }
                    }
                }
            }
        }
    }

    private fun initStateLaunch() {
        lifecycleScope.launchWhenStarted {
            launch {
                with(binding) {
                    viewModel.loadingState.collectLatest { state ->
                        if (state) {
                            pbLoading.showView()
                        } else {
                            pbLoading.removeView()
                        }
                    }
                }
            }

            launch {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is UiEvent.ShowSnackbar -> {
                            Snackbar.make(
                                binding.root,
                                event.uiText.asString(this@SearchActivity),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            return@collectLatest
                        }
                    }
                }
            }

            launch {
                settingViewModel.state.collectLatest { state ->
                    if (state) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        searchAdapter?.let { adapter ->

            binding.rvUser.apply {

                this.adapter = adapter

                layoutManager = LinearLayoutManager(this@SearchActivity)

                addItemDecoration(MarginItemDecorationVertical(16))

                ViewCompat.setNestedScrollingEnabled(this, true)
            }

            adapter.setOnItemClickListener { data ->
                val bundle = Bundle().apply {
                    putString(CATEGORY, SEARCH_CATEGORY)
                    putString(USERNAME, data.toJson())
                }
                startActivity(Intent(this, DetailActivity::class.java).putExtras(bundle))
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.option_menu, menu)

        initSearchMenu(menu)

        initSwitchMenu(menu)

        return true
    }

    private fun initSwitchMenu(menu: Menu) {

        val switchCompat = menu.findItem(R.id.settings).actionView as SwitchCompat

        val switchImage = menu.findItem(R.id.iv_Setting).actionView as ImageView

        lifecycleScope.launchWhenStarted {
            settingViewModel.state.collectLatest { state ->
                switchCompat.isChecked = state
                if (state) {
                    switchImage.setImageResource(R.drawable.ic_dark_mode)
                } else {
                    switchImage.setImageResource(R.drawable.ic_light_mode)
                }
            }
        }

        switchCompat.apply {
            setOnCheckedChangeListener { _, isChecks ->
                lifecycleScope.launch {
                    settingViewModel.saveModeSetting(isChecks)
                }
            }
        }
    }

    private fun initSearchMenu(menu: Menu) {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = resources.getString(R.string.search)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(username: String?): Boolean {
                    with(binding) {
                        if (lyAnimationEmpty.visibility == View.VISIBLE) {
                            lyAnimationEmpty.removeView()
                            lottieAnimationView.pauseAnimation()
                        } else {
                            viewModel.getSearch(username.toString()) { itemState ->
                                searchAdapter?.differ?.submitList(itemState.items)
                            }
                            resultData = username
                            val bundle = Bundle().apply {
                                putString(ON_QUERY_TEXT_LISTENER, resultData)
                            }
                            lifecycleScope.launch {
                                sessionManager.setUsername(resultData.toString())
                            }
                            clearFocus()
                            onSaveInstanceState(bundle)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(username: String?): Boolean {
                    return false
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(USERNAME, resultData)
    }

    override fun onBackPressed() {
        lifecycleScope.launch {
            viewModel.cancelRunningJob()
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        _binding = null
        searchAdapter = null
        super.onDestroy()
    }
}