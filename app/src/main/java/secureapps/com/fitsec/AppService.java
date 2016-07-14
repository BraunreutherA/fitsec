package secureapps.com.fitsec;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;
import secureapps.com.fitsec.data.App;
import secureapps.com.fitsec.data.InstallationReport;
import secureapps.com.fitsec.data.RealmApp;
import timber.log.Timber;

/**
 * Created by Alex on 27.06.16.
 */
public class AppService implements LoaderManager.LoaderCallbacks<List<ApplicationInfo>> {
    private static final String SYSTEM_PACKAGE_NAME = "android";

    private Context context;
    private PackageManager packageManager;

    public AppService() {
    }

    public rx.Observable<List<RealmApp>> getInstalledApps(final Context context) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RealmApp> query = realm.where(RealmApp.class);

        return query.findAllAsync()
                .asObservable()
                .map(new Func1<RealmResults<RealmApp>, List<RealmApp>>() {
                    @Override
                    public List<RealmApp> call(RealmResults<RealmApp> realmApps) {
                        return Utility.transformRealResultsToList(realmApps);
                    }
                })
                .map(new Func1<List<RealmApp>, List<RealmApp>>() {
                    @Override
                    public List<RealmApp> call(List<RealmApp> realmApps) {
                        List<RealmApp> apps = new ArrayList<>();

                        for (RealmApp realmApp: realmApps) {
                            if (!realmApp.getPackageName().equals(context.getPackageName())) {
                                apps.add(realmApp);
                            }
                        }

                        return apps;
                    }
                });
    }

    public void setAppSecured(final String packageName, final boolean secured) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmApp realmApp = realm
                        .where(RealmApp.class)
                        .equalTo("packageName", packageName)
                        .findFirst();

                if (realmApp != null) {
                    realmApp.setSecured(secured);
                    SecureReportService secureReportService = new SecureReportService();
                    secureReportService.createNewSecureReport(packageName, secured);
                }
            }
        });
    }

    public void setAppSecured(final float threshold) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RealmApp> realmApps = realm.where(RealmApp.class).equalTo("secured", false).findAll();
                for (RealmApp realmApp: realmApps) {
                    if (((float) realmApp.getSecureCount() / (float) Math.max(1, realmApp.getInstallations())) > threshold) {
                        realmApp.setSecured(true);
                        SecureReportService secureReportService = new SecureReportService();
                        secureReportService.createNewSecureReport(realmApp.getPackageName(), true);
                    }
                }
            }
        });
    }

    public Observable<List<RealmApp>> getUnsecured(final float threshold, final Context context) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(RealmApp.class)
                .equalTo("secured", false)
                .findAllAsync()
                .asObservable()
                .map(new Func1<RealmResults<RealmApp>, List<RealmApp>>() {
                    @Override
                    public List<RealmApp> call(RealmResults<RealmApp> realmApps) {
                        List<RealmApp> filtered = new ArrayList<RealmApp>();

                        for (RealmApp realmApp: realmApps) {
                            if (((float) realmApp.getSecureCount() / (float) Math.max(1, realmApp.getInstallations())) > threshold) {
                                filtered.add(realmApp);
                            }
                        }

                        return filtered;
                    }
                })
                .map(new Func1<List<RealmApp>, List<RealmApp>>() {
                    @Override
                    public List<RealmApp> call(List<RealmApp> realmApps) {
                        List<RealmApp> apps = new ArrayList<>();

                        for (RealmApp realmApp: realmApps) {
                            if (!realmApp.getPackageName().equals(context.getPackageName())) {
                                apps.add(realmApp);
                            }
                        }

                        return apps;
                    }
                });
    }

    public static boolean isAppSecured(String packageName) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RealmApp> query = realm.where(RealmApp.class).equalTo("packageName", packageName);

        RealmApp realmApp = query.findFirst();

        return realmApp != null && realmApp.isSecured();
    }

    public void fetchUsageData() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RealmApp> realmApps = realm.where(RealmApp.class)
                        .findAll();

                List<String> packageNames = new ArrayList<>(realmApps.size());
                for (RealmApp realmApp : realmApps) {
                    packageNames.add(realmApp.getPackageName());
                }

                ParseQuery<App> query = ParseQuery.getQuery(App.class);
                query.whereContainedIn(App.KEY_PACKAGE_NAME, packageNames);

                try {
                    List<App> apps = query.find();

                    for (App app : apps) {
                        for (RealmApp realmApp : realmApps) {
                            if (app.getPackageName().equals(realmApp.getPackageName())) {
                                realmApp.setInstallations(app.getInstallationCount());
                                realmApp.setSecureCount(app.getSecureCount());
                            }
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateInternalAppList(Context context) {
        this.context = context;
        this.packageManager = context.getPackageManager();
        ((FragmentActivity) context).getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    private void updateInternalAppList(List<ApplicationInfo> packages) {
        Realm realm = Realm.getDefaultInstance();

        final List<RealmApp> apps = new ArrayList<>();
        final List<InstallationReport> installationReports = new ArrayList<>();
        for (ApplicationInfo applicationInfo : packages) {
            if (realm.where(RealmApp.class).equalTo("packageName", applicationInfo.packageName).count() == 0) {
                RealmApp realmApp = new RealmApp();
                realmApp.setPackageName(applicationInfo.packageName);

                realmApp.setName((String) packageManager.getApplicationLabel(applicationInfo));
                realmApp.setAppIcon(Utility.convertDrawable(packageManager.getApplicationIcon(applicationInfo)));

                InstallationReport installationReport = new InstallationReport();
                installationReport.setPackageName(applicationInfo.packageName);

                installationReports.add(installationReport);
                apps.add(realmApp);
            }
        }

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(installationReports);
                realm.copyToRealm(apps);
            }
        });

        fetchUsageData();
    }

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
    public void onLoaderReset(Loader<List<ApplicationInfo>> loader) {
    }
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

    @Override
    public List<ApplicationInfo> loadInBackground() {
        List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> filteredApplicationInfos = new ArrayList<>();

        for (ApplicationInfo app : applicationInfos) {
            if (packageManager.getLaunchIntentForPackage(app.packageName) != null) {
                if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
                    // updated system apps
                } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    // system apps

                } else {
                    // user installed apps
                    filteredApplicationInfos.add(app);
                }
            }
        }

        return filteredApplicationInfos;
    }
}
