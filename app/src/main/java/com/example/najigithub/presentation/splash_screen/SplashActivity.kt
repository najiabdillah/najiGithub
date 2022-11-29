package com.example.najigithub.presentation.splash_screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.najigithub.databinding.ActivitySplashBinding
import com.example.najigithub.domain.utils.SessionManager
import com.example.najigithub.presentation.search_screen.SearchActivity
import com.example.najigithub.presentation.welcome_screen.WelcomeActivity
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var _binding: ActivitySplashBinding? = null

    private val binding get() = _binding as ActivitySplashBinding

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        initLaunch()
    }

    private fun initLaunch() {
        object : Thread() {
            override fun run() {
                try {
                    sleep(3000L)
                    lifecycleScope.launchWhenStarted {
                        sessionManager.getFirstIntall.collectLatest { state ->
                            if (state) {
                                homeView()
                            } else {
                                firstInstallView()
                                sessionManager.setFirstInstall(false)
                            }
                        }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun homeView() {
        startActivity(Intent(this, SearchActivity::class.java))
        finish()
    }

    private fun firstInstallView() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}