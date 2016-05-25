package secureapps.com.fitsec;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import secureapps.com.fitsec.data.App;
import timber.log.Timber;

/**
 * @author Alexander Braunreuther
 */
public class FitsecApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(App.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("f401GO8JgC7mKlqpeGp6KroOz9jb5F5A8J6gKAd5")
                .clientKey("En7j22yNKCpu5wLOrh1hvPve6Y9JJoeyqHVuibxk")
                .build());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
