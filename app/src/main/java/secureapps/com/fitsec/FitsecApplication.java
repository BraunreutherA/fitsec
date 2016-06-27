package secureapps.com.fitsec;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.parse.Parse;
import com.parse.ParseObject;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import secureapps.com.fitsec.data.App;
import timber.log.Timber;

/**
 * @author Alexander Braunreuther
 */
public class FitsecApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);

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
