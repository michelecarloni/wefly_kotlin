package com.example.exampleinstrumentedtest

import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wefly.R
import com.example.wefly.activity.MainActivity
import com.example.wefly.activity.SignUpActivity
import org.hamcrest.CoreMatchers.allOf

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class SignUpActivityTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<SignUpActivity> = ActivityScenarioRule(SignUpActivity::class.java)

    @Test
    fun testSignUpActivity() {
        // Type text into the EditText fields
        onView(withId(R.id.editTextNome)).perform(typeText("michele"), closeSoftKeyboard())
        onView(withId(R.id.editTextCognome)).perform(typeText("carloni"), closeSoftKeyboard())
        onView(withId(R.id.editTextTelefono)).perform(typeText("123456789"), closeSoftKeyboard())
        onView(withId(R.id.editTextEmail)).perform(typeText("uuoo@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextPassword)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.edit_conferma_password)).perform(typeText("password"), pressImeActionButton())

        // Click on the 'Continua' button
        onView(withId(R.id.continua_btn)).perform(click())

        Thread.sleep(5000)

        // Interact with views in CompletaProfiloActivity
        // For example, clicking on an ImageView and checking checkboxes
        onView(allOf(withId(R.id.profile_picture), isAssignableFrom(ImageView::class.java))).perform(click())
            .check(matches(isDisplayed()))
        onView(withId(R.id.scelta1)).perform(click())
        onView(withId(R.id.scelta3)).perform(click())
        onView(withId(R.id.scelta5)).perform(click())
        onView(withId(R.id.scelta7)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.registrati_btn)).perform(click())

        Thread.sleep(5000)

    }

}
