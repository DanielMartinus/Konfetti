package nl.dionsegijn.konfettidemo;

import android.app.Application;

import com.codemonkeylabs.fpslibrary.TinyDancer;

/**
 * Created by dionsegijn on 4/3/17.
 */
public class DemoApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();

        TinyDancer.create()
                .redFlagPercentage(.1f) // set red indicator for 10%....different from default
                .startingXPosition(200)
                .startingYPosition(600)
                .show(getApplicationContext());
    }
}
