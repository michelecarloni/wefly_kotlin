package com.example.wefly.onboarding

import android.content.Intent
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import com.example.wefly.R
import com.example.wefly.activity.RegisterActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash)

            Handler(Looper.getMainLooper()).postDelayed({
                if (onBoardingFinished()) {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, OnboardingActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }, 3000)
    }
    private fun onBoardingFinished(): Boolean {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}