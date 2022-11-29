package com.example.najigithub.presentation.detail_screen

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.najigithub.R
import com.example.najigithub.data.remote.dto.UserDetailResponse
import com.example.najigithub.databinding.ActivityDetailBinding
import com.example.najigithub.domain.adapter.TabFollowAdapter
import com.example.najigithub.domain.utils.*
import com.example.najigithub.domain.utils.Extensions.CATEGORY
import com.example.najigithub.domain.utils.Extensions.FAVORITE_CATEGORY
import com.example.najigithub.domain.utils.Extensions.FAVORITE_DATA_IMAGE
import com.example.najigithub.domain.utils.Extensions.FAVORITE_DATA_USERNAME
import com.example.najigithub.domain.utils.Extensions.SEARCH_CATEGORY
import com.example.najigithub.domain.utils.Extensions.USERNAME
import com.example.najigithub.presentation.detail_screen.fragment.follower.FollowerFragment
import com.example.najigithub.presentation.detail_screen.fragment.following.FollowingFragment
import com.example.najigithub.presentation.favorite_screen.FavoriteActivity
import com.example.najigithub.presentation.favorite_screen.FavoriteViewModel
import com.example.najigithub.presentation.search_screen.SearchActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null

    private val binding get() = _binding as ActivityDetailBinding

    private var tabFollowAdapter: TabFollowAdapter? = null

    private var fragmentList: List<Fragment?>? = null

    private var followerFragment: FollowerFragment? = null

    private var followingFragment: FollowingFragment? = null

    private lateinit var sessionManager: SessionManager

    val viewModel: DetailViewModel by viewModel()

    private val favoriteViewModel: FavoriteViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        iniData()

        initInstance()

        initAdapter()

        initLaunch()

        initModeData()

    }

    private fun initModeData() {
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

    private fun initLaunch() {
        lifecycleScope.launchWhenStarted {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiEvent.collectLatest { event ->
                        when (event) {
                            is UiEvent.ShowSnackbar -> {
                                snackbar(
                                    binding.root,
                                    event.type,
                                    event.uiText.asString(this@DetailActivity)
                                )
                            }
                            else -> {
                                return@collectLatest
                            }
                        }
                    }
                }

                launch {
                    favoriteViewModel.uiEvent.collectLatest { event ->
                        when(event){
                            is UiEvent.ShowSnackbarWithCallback -> {
                                snackbarWithCallback(
                                    binding.root,
                                    event.type,
                                    event.uiText.asString(this@DetailActivity))
                            }
                            else -> {
                                return@collectLatest
                            }
                        }
                    }
                }

                launch {
                    with(binding) {
                        favoriteViewModel.stateVisibility.collectLatest { stateStatus ->
                            if (stateStatus) {
                                btnFavorite.setImageResource(R.drawable.ic_red_favorite)
                            } else {
                                btnFavorite.setImageResource(R.drawable.ic_black_favorite)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun iniData() {
        with(binding) {
            intent.getStringExtra(CATEGORY)?.let { category ->
                when(category) {
                   SEARCH_CATEGORY -> {
                       intent.getStringExtra(USERNAME)?.fromJson<UserDetailResponse>()?.let { dataResponse ->
                           viewModel.getDetailUser(dataResponse.login.toString()) { resultItem ->
                               ivAvatar.loadImage(resultItem.avatarUrl)
                               tvName.text = resultItem.name
                               tvUsername.text = resultItem.login
                               tvFollower.text = resultItem.followers.toString()
                               tvFollowing.text = resultItem.following.toString()
                           }

                           favoriteViewModel.checkFavorite(dataResponse.login.toString())

                           btnFavorite.setOnClickListener {
                               if (favoriteViewModel.stateVisibility.value) {
                                   startActivity(Intent(this@DetailActivity, FavoriteActivity::class.java))
                                   finish()
                               } else {
                                   favoriteViewModel.setInsertFavorite(dataResponse.login.toString(), dataResponse.avatarUrl.toString())
                               }
                           }
                       }
                   }
                   FAVORITE_CATEGORY -> {
                       val username = intent.getStringExtra(FAVORITE_DATA_USERNAME).toString()
                       val imagesData = intent.getStringExtra(FAVORITE_DATA_IMAGE).toString()
                       viewModel.getDetailUser(username) { resultItem ->
                           ivAvatar.loadImage(resultItem.avatarUrl)
                           tvName.text = resultItem.name
                           tvUsername.text = resultItem.login
                           tvFollower.text = resultItem.followers.toString()
                           tvFollowing.text = resultItem.following.toString()
                       }

                       favoriteViewModel.checkFavorite(username)

                       btnFavorite.setOnClickListener {
                           if (favoriteViewModel.stateVisibility.value) {
                               startActivity(Intent(this@DetailActivity, FavoriteActivity::class.java))
                               finish()
                           } else {
                               favoriteViewModel.setInsertFavorite(username, imagesData)
                           }
                       }
                   }
                   else -> {
                       false
                   }
               }
            }
        }
    }

    private fun initInstance() {
        followerFragment = FollowerFragment.instance()
        followingFragment = FollowingFragment.instance()
    }

    private fun initAdapter() {
        fragmentList = listOf(
            followerFragment,
            followingFragment
        )

        tabFollowAdapter = TabFollowAdapter(
            fragmentList as List<Fragment>,
            supportFragmentManager,
            lifecycle
        )

        with(binding) {
            vpFollow.apply {
                adapter = tabFollowAdapter
                (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }

            TabLayoutMediator(
                tabs,
                vpFollow,
            ) { tabs, position ->
                tabs.text = resources.getString(TAB_TITLE[position])
            }.attach()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, SearchActivity::class.java))
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        tabFollowAdapter = null
        fragmentList = null
        followerFragment = null
        followingFragment = null
    }

    companion object {
        @StringRes
        private val TAB_TITLE = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}