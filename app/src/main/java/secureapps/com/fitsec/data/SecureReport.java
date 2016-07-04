package secureapps.com.fitsec.data;

import io.realm.RealmObject;

/**
 * Created by Alex on 30.06.16.
 */
public class SecureReport extends RealmObject {
    private int incrementor;
    private String packageName;

    public SecureReport() {}

    public void setIncrementor(int incrementor) {
        this.incrementor = incrementor;
    }

    public int getIncrementor() {
        return incrementor;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}