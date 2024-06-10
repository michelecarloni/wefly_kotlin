package com.example.exampleinstrumentedtest

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wefly.R
import com.example.wefly.activity.SignInActivity

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class SignInActivityTest{

    @get:Rule
    var activityRule: ActivityScenarioRule<SignInActivity> = ActivityScenarioRule(SignInActivity::class.java)

    @Test
    fun testSignInActivity() {
        // Type text into the EditText fields
        Espresso.onView(ViewMatchers.withId(R.id.email_address))
            .perform(ViewActions.typeText("uuoo@gmail.com"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.password))
            .perform(ViewActions.typeText("password"), ViewActions.closeSoftKeyboard())


        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.click())

        Thread.sleep(5000)

    }

}