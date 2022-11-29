package com.example.najigithub.presentation.detail_screen.fragment.following

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.najigithub.R
import com.example.najigithub.databinding.FragmentFollowingBinding
import com.example.najigithub.domain.adapter.FollowAdapter
import com.example.najigithub.domain.utils.MarginItemDecorationVertical
import com.example.najigithub.domain.utils.removeView
import com.example.najigithub.domain.utils.showView
import com.example.najigithub.presentation.detail_screen.DetailActivity
import com.example.najigithub.presentation.detail_screen.DetailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FollowingFragment : Fragment(R.layout.fragment_following) {

    private var _binding: FragmentFollowingBinding? = null

    private val binding get() = _binding as FragmentFollowingBinding

    private var followAdapter: FollowAdapter? = null

    private lateinit var viewModel: DetailViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentFollowingBinding.bind(view)
        viewModel = (requireActivity() as DetailActivity).viewModel
        followAdapter = FollowAdapter.instance()
        super.onViewCreated(binding.root, savedInstanceState)

        initData()

        initAdapter()

        initLaunch()
    }

    private fun initData() {
        viewModel.getUserFollowing { followItems ->
            followAdapter?.differ?.submitList(followItems)
        }
    }

    private fun initAdapter() {
        with(binding) {
            followAdapter?.let { adapter ->
                rvFollowing.apply {

                    this.adapter = adapter

                    layoutManager = LinearLayoutManager(requireContext())

                    addItemDecoration(MarginItemDecorationVertical(16))

                    ViewCompat.setNestedScrollingEnabled(this, true)
                }
            }
        }
    }

    private fun initLaunch() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        followAdapter = null
        super.onDestroyView()
    }

    companion object {
        fun instance() = FollowingFragment()
    }
}