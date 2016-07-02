package secureapps.com.fitsec;

import com.parse.ParseException;
import com.parse.ParseQuery;


import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Func1;
import secureapps.com.fitsec.data.App;
import secureapps.com.fitsec.data.InstallationReport;

/**
 * Created by Alex on 02.07.16.
 */
public class InstallationReportService {
    public void createNewInstallationReport(final String packageName) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                InstallationReport installationReport = realm.createObject(InstallationReport.class);
                installationReport.setPackageName(packageName);
            }
        });
    }

    public void syncInstallationReports() {
        Realm realm = Realm.getDefaultInstance();
        realm.where(InstallationReport.class)
                .equalTo("done", false)
                .findAllAsync()
                .asObservable()
                .map(new Func1<RealmResults<InstallationReport>, Object>() {
                    @Override
                    public Object call(RealmResults<InstallationReport> installationReports) {
                        for (final InstallationReport installationReport : installationReports) {
                            ParseQuery<App> query = ParseQuery.getQuery(App.class);
                            query.whereEqualTo(App.KEY_PACKAGE_NAME, installationReport.getPackageName());
                            try {
                                App app = query.getFirst();
                                app.incrementInstallationCount();
                                app.save();
                                setInstallationReportDone(installationReport.getPackageName());
                            } catch (ParseException e) {
                                if (e.getCode() == 101) {
                                    App app = new App();
                                    app.setPackageName(installationReport.getPackageName());
                                    app.incrementInstallationCount();
                                    try {
                                        app.save();
                                        setInstallationReportDone(installationReport.getPackageName());
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        }

                        return null;
                    }
                }).subscribe();
    }

    private void setInstallationReportDone(final String packageName) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                InstallationReport installationReport = realm
                        .where(InstallationReport.class)
                        .equalTo("packageName", packageName)
                        .findFirst();

                if (installationReport != null) {
                    installationReport.setDone(true);
                }
            }
        });
    }
}
