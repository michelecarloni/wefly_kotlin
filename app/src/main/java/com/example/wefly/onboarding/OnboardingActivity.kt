package com.example.wefly.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wefly.R
import androidx.viewpager2.widget.ViewPager2

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val fragmentList = arrayListOf(
            FirstSlideFragment(),
            SecondSlideFragment()
        )
        val adapter = ViewPagerAdapter(
            fragmentList,
            supportFragmentManager,
            lifecycle
        )
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter
    }
}