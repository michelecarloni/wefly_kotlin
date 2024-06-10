package com.example.wefly.fragment

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.example.wefly.R
import com.example.wefly.fragment.ProfiloFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource

@RunWith(AndroidJUnit4::class)
class ProfiloFragmentTest {

    // Grant the necessary permissions before running the test
    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE"
    )

    @Test
    fun testLogout() {
        // Register an idling resource to wait for the fragment to be ready
        val idlingResource = CountingIdlingResource("Fragment")
        IdlingRegistry.getInstance().register(idlingResource)

        // Launch the fragment in a container with a specific theme
        val scenario: FragmentScenario<ProfiloFragment> = launchFragmentInContainer(themeResId = R.style.Theme_WeFly)

        idlingResource.increment()
        scenario.onFragment {
            idlingResource.decrement()
        }


        onView(withId(R.id.logout_btn)).perform(click())
        Thread.sleep(2000)

        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}
