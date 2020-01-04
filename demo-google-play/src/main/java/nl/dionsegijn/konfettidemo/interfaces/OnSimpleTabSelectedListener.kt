package nl.dionsegijn.konfettidemo.interfaces

import com.google.android.material.tabs.TabLayout

/**
 * Created by dionsegijn on 5/21/17.
 */
open class OnSimpleTabSelectedListener : TabLayout.OnTabSelectedListener {
    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabSelected(tab: TabLayout.Tab?) {}
}
