package nl.dionsegijn.konfettidemo.configurations.viewpager

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * Created by dionsegijn on 5/24/17.
 */
class ConfigPagerAdapter(val tabs: Array<TabConfig>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val view = tabs[position].widgetView
        container?.addView(view)
        return view
    }

    override fun isViewFromObject(view: View?, oView: Any?): Boolean {
        return view == oView as View
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return ""
    }
}
