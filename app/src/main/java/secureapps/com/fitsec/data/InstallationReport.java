package secureapps.com.fitsec.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alex on 30.06.16.
 */
public class InstallationReport extends RealmObject {
    @PrimaryKey
    private String packageName;
    private boolean done;

    public InstallationReport() {}

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
