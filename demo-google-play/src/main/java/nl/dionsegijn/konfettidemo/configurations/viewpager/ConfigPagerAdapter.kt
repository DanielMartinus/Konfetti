package nl.dionsegijn.konfettidemo.configurations.viewpager

import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * Created by dionsegijn on 5/24/17.
 */
class ConfigPagerAdapter(
        private val tabs: Array<TabConfig>
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = tabs[position].widgetView
        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, oView: Any): Boolean = view == oView as View

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun getCount(): Int = tabs.size

    override fun getPageTitle(position: Int): CharSequence = ""
}
