import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import nl.dionsegijn.xml.kotlin.MainActivity
import nl.dionsegijn.xml.kotlin.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val testButtons = listOf(
        TestButton(R.id.btnFestive, "Festive"),
        TestButton(R.id.btnExplode, "Explode"),
        TestButton(R.id.btnParade, "Parade"),
        TestButton(R.id.btnRain, "Rain"),
    )

    @Test
    fun mainActivityTest() {
        testButtons.forEachIndexed { pos, button ->
            runTestForButtonWithText(button.viewId, button.buttonText, pos + 1)
        }
    }

    private fun runTestForButtonWithText(viewId: Int, buttonText: String, position: Int) {
        val materialButton = onView(
            allOf(
                withId(viewId), withText(buttonText),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    position
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                    && view == parent.getChildAt(position)
            }
        }
    }

    data class TestButton(val viewId: Int, val buttonText: String)
}
