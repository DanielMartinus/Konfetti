package nl.dionsegijn.konfettidemo.configurations

import android.content.Context
import android.os.Build
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.bottomsheet_config_controls.view.*
import nl.dionsegijn.konfettidemo.R
import nl.dionsegijn.konfettidemo.configurations.selection_views.ColorSelectionView
import nl.dionsegijn.konfettidemo.configurations.selection_views.ShapeSelectionView
import nl.dionsegijn.konfettidemo.configurations.selection_views.SpeedSelectionView

/**
 * Created by dionsegijn on 5/21/17.
 */
class ConfigurationControlsWidget : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.bottomsheet_config_controls, this)
        orientation = VERTICAL

        viewPager.adapter = ConfigPagerAdapter(getTabs())
        tabLayout.setupWithViewPager(viewPager)
        for (i in 0..tabLayout.tabCount - 1) {
            tabLayout.getTabAt(i)?.setIcon(getTabs()[i].icon)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            elevation = 100f
        }

    }

    fun setOnTabSelectedListener(onTabSelectedListener: TabLayout.OnTabSelectedListener) {
        tabLayout.addOnTabSelectedListener(onTabSelectedListener)
    }

    fun getTabs(): Array<TabConfig> {
        return arrayOf(
                TabConfig(R.drawable.ic_paint, ColorSelectionView(context)),
                TabConfig(R.drawable.ic_shapes, ShapeSelectionView(context)),
                TabConfig(R.drawable.ic_speed, SpeedSelectionView(context)),
                TabConfig(R.drawable.ic_time_to_live, SpeedSelectionView(context)))
    }

    class TabConfig(val icon: Int, val widgetView: View)

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

}
