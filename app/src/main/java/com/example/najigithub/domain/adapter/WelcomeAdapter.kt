package com.example.najigithub.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.najigithub.R
import com.example.najigithub.databinding.WelcomeItemLayoutBinding

class WelcomeAdapter: PagerAdapter() {

    private val imageList = listOf(
        R.drawable.welcome_illustration_one,
        R.drawable.welcome_illustration_two,
        R.drawable.welcome_illustration_three
    )

    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return WelcomeItemLayoutBinding.inflate(LayoutInflater.from(container.context), container, false).also { binding ->
            binding.ivWelcome.setImageResource(imageList[position])
            container.addView(binding.root)
        }.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).apply {
            removeView(`object` as View)
        }
    }

    companion object {
        fun instance() = WelcomeAdapter()
    }
}