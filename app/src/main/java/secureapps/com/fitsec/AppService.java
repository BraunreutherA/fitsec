package secureapps.com.fitsec;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import secureapps.com.fitsec.data.RealmApp;

/**
 * Created by Alex on 27.06.16.
 */
public class AppService {
    private final PackageManager packageManager;

    public AppService(Context context) {
        packageManager = context.getPackageManager();
    }

    public OrderedRealmCollection<RealmApp> getInstalledApps() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RealmApp> query = realm.where(RealmApp.class);

        return query.findAllAsync();
    }

    public void updateInternalAppList() {
        List<ApplicationInfo> packages = getPackageInformation();
        Realm realm = Realm.getDefaultInstance();

        final List<RealmApp> apps = new ArrayList<>();
        for (ApplicationInfo applicationInfo: packages) {
            RealmApp realmApp = new RealmApp();
            realmApp.setPackageName(applicationInfo.packageName);

            realmApp.setName((String) packageManager.getApplicationLabel(applicationInfo));
            realmApp.setAppIcon(Utility.convertDrawable(packageManager.getApplicationIcon(applicationInfo)));

            apps.add(realmApp);
        }

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(apps);
            }
        });
    }

    private List<ApplicationInfo> getPackageInformation() {
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

//        for(ApplicationInfo app : packages) {
//            //checks for flags; if flagged, check if updated system app
//            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
//                installedApps.add(app);
//                //it's a system app, not interested
//            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
//                //Discard this one
//                //in this case, it should be a user-installed app
//            } else {
//                installedApps.add(app);
//            }
//        }

        return packages;
    }
}
