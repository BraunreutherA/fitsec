package secureapps.com.fitsec;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmQuery;
import secureapps.com.fitsec.data.RealmApp;

/**
 * Created by Alex on 27.06.16.
 */
public class AppService implements LoaderManager.LoaderCallbacks<List<ApplicationInfo>> {
    private static final String SYSTEM_PACKAGE_NAME = "android";

    private final PackageManager packageManager;
    private final Context context;

    public AppService(Context context) {
        this.context = context;
        packageManager = context.getPackageManager();
    }

    public OrderedRealmCollection<RealmApp> getInstalledApps() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RealmApp> query = realm.where(RealmApp.class);

        return query.findAllAsync();
    }

    public boolean isAppSecured(String packageName) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RealmApp> query = realm.where(RealmApp.class).equalTo("packageName", packageName);

        RealmApp realmApp = query.findFirst();

        return realmApp != null && realmApp.isSecured();
    }

    public void updateInternalAppList(List<ApplicationInfo> packages) {
        final List<RealmApp> apps = new ArrayList<>();
        for (ApplicationInfo applicationInfo: packages) {
            RealmApp realmApp = new RealmApp();
            realmApp.setPackageName(applicationInfo.packageName);

            realmApp.setName((String) packageManager.getApplicationLabel(applicationInfo));
            realmApp.setAppIcon(Utility.convertDrawable(packageManager.getApplicationIcon(applicationInfo)));

            apps.add(realmApp);
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(apps);
            }
        });
    }

//    private List<ApplicationInfo> getPackageInformation() {
//        List<ApplicationInfo> packages = new ArrayList<>();
//
////        for (ApplicationInfo app: packageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {
////            if (!isSystemApp(app.packageName)) {
////                packages.add(app);
////            }
////        }
//
//        return packages;
//    }

    private boolean isSystemApp(String packageName) {
        // TODO doesn't work

        try {
            PackageInfo targetPkgInfo = packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            PackageInfo sys = packageManager.getPackageInfo(
                    SYSTEM_PACKAGE_NAME, PackageManager.GET_SIGNATURES);
            return (targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0]
                    .equals(targetPkgInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public Loader<List<ApplicationInfo>> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(context);
    }

    @Override
    public void onLoadFinished(Loader<List<ApplicationInfo>> loader, List<ApplicationInfo> data) {
        updateInternalAppList(data);
    }

    @Override
    public void onLoaderReset(Loader<List<ApplicationInfo>> loader) {}
}

/**
 * A custom Loader that loads all of the installed applications.
 */
class AppListLoader extends AsyncTaskLoader<List<ApplicationInfo>> {
    private final PackageManager packageManager;

    public AppListLoader(Context context) {
        super(context);
        packageManager = getContext().getPackageManager();
    }

    @Override public List<ApplicationInfo> loadInBackground() {
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
    }
}
