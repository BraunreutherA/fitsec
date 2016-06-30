package secureapps.com.fitsec;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import secureapps.com.fitsec.data.App;
import secureapps.com.fitsec.data.RealmApp;

/**
 * Created by Alex on 30.06.16.
 */
public class Synchronizer implements RealmChangeListener<RealmResults<RealmApp>> {
    @Override
    public void onChange(RealmResults<RealmApp> realmApps) {
        List<App> apps = new ArrayList<>(realmApps.size());

        for (RealmApp realmApp: realmApps) {
            boolean shouldUpdate = false;
            App app = new App();
            app.setPackageName(realmApp.getPackageName());

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            if (!realmApp.isInstallationReported()) {
                app.incrementInstallationCount();
                realmApp.setInstallationReported(true);
                shouldUpdate = true;
            }

            if (realmApp.getSecureReport() < 0) {
                app.decrementSecureCount();
                realmApp.setSecureReport(0);
                shouldUpdate = true;
            }

            if (realmApp.getSecureReport() > 0) {
                app.incrementSecureCount();
                realmApp.setSecureReport(0);
                shouldUpdate = true;
            }
            realm.commitTransaction();

            if (shouldUpdate) {
                apps.add(app);
            }
        }

        ParseObject.saveAllInBackground(apps);
    }
}
