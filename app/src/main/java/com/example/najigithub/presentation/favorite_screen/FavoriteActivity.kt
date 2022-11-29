package com.example.najigithub.presentation.favorite_screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.najigithub.databinding.ActivityFavoriteBinding
import com.example.najigithub.domain.adapter.FavoriteAdapter
import com.example.najigithub.domain.utils.*
import com.example.najigithub.domain.utils.Extensions.FAVORITE_DATA_IMAGE
import com.example.najigithub.domain.utils.Extensions.FAVORITE_DATA_USERNAME
import com.example.najigithub.presentation.detail_screen.DetailActivity
import com.example.najigithub.presentation.search_screen.SearchActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteActivity : AppCompatActivity() {

    private var _binding: ActivityFavoriteBinding? = null

    private val binding get() = _binding as ActivityFavoriteBinding

    private val viewModel: FavoriteViewModel by viewModel()

    private var favoriteAdapter: FavoriteAdapter? = null

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFavoriteBinding.inflate(layoutInflater)
        favoriteAdapter = FavoriteAdapter.instance()
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        iniAdapter()

        iniLaunch()

        initMode()
    }

    private fun initMode() {
        lifecycleScope.launchWhenStarted {
            sessionManager.getMode.collectLatest { stateMode ->
                if (stateMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

    private fun iniLaunch() {
        lifecycleScope.launchWhenStarted {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getAllFavorite.collectLatest { favoriteItem ->
                        with(binding) {
                            if (favoriteItem.isEmpty()){
                                lottieAnimationView.playAnimation()
                                lyAnimationEmpty.showView()
                            } else {
                                lottieAnimationView.pauseAnimation()
                                lyAnimationEmpty.removeView()
                            }
                            favoriteAdapter?.differ?.submitList(favoriteItem)
                        }
                    }
                }

                launch {
                    viewModel.uiEvent.collectLatest { event ->
                        when(event) {
                            is UiEvent.ShowSnackbar -> {
                                snackbar(
                                    binding.root,
                                    event.type,
                                    event.uiText.asString(this@FavoriteActivity))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun iniAdapter() {
        favoriteAdapter?.let { adapter ->
            binding.rvFavorite.apply {

                this.adapter = adapter

                layoutManager = LinearLayoutManager(this@FavoriteActivity)

                addItemDecoration(MarginItemDecorationVertical(16))

                ViewCompat.setNestedScrollingEnabled(this, true)
            }

            adapter.setOnItemClickListener { stateItem ->
                val bundle = Bundle().apply {
                    putString(Extensions.CATEGORY, Extensions.FAVORITE_CATEGORY)
                    putString(FAVORITE_DATA_USERNAME, stateItem.NAME)
                    putString(FAVORITE_DATA_IMAGE, stateItem.IMAGE)
                }
                startActivity(Intent(this, DetailActivity::class.java).putExtras(bundle))
                finish()
            }

            adapter.setOnDeleteItemListener { dataId ->
                viewModel.deleteFavorite(dataId)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        returnView()
    }

    private fun returnView() {
        startActivity(Intent(this, SearchActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        favoriteAdapter = null
    }
}