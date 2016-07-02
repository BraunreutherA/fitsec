package secureapps.com.fitsec;

import com.parse.ParseException;
import com.parse.ParseQuery;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Func1;
import secureapps.com.fitsec.data.App;
import secureapps.com.fitsec.data.SecureReport;

/**
 * Created by Alex on 02.07.16.
 */
public class SecureReportService {
    public void createNewSecureReport(final String packageName, final boolean increment) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SecureReport secureReport = realm.createObject(SecureReport.class);
                secureReport.setPackageName(packageName);
                if (increment) {
                    secureReport.setIncrementor(1);
                } else {
                    secureReport.setIncrementor(-1);
                }
            }
        });
    }

    public void syncSecureReports() {
        Realm realm = Realm.getDefaultInstance();
        realm.where(SecureReport.class)
                .findAllAsync()
                .asObservable()
                .map(new Func1<RealmResults<SecureReport>, Object>() {
                    @Override
                    public Object call(RealmResults<SecureReport> secureReports) {
                        for (final SecureReport secureReport : secureReports) {
                            ParseQuery<App> query = ParseQuery.getQuery(App.class);
                            query.whereEqualTo(App.KEY_PACKAGE_NAME, secureReport.getPackageName());
                            try {
                                App app = query.getFirst();
                                app.increment(App.KEY_SECURE_COUNT, secureReport.getIncrementor());
                                app.save();
                                deleteSecureReport(secureReport.getPackageName());
                            } catch (ParseException e) {
                                if (e.getCode() == 101) {
                                    App app = new App();
                                    app.setPackageName(secureReport.getPackageName());
                                    app.increment(App.KEY_SECURE_COUNT, secureReport.getIncrementor());
                                    try {
                                        app.save();
                                        deleteSecureReport(secureReport.getPackageName());
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

    private void deleteSecureReport(final String packageName) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SecureReport secureReportToDelete = realm.where(SecureReport.class)
                        .equalTo("packageName", packageName)
                        .findFirst();
                if (secureReportToDelete != null) {
                    secureReportToDelete.deleteFromRealm();
                }
            }
        });
    }
}
