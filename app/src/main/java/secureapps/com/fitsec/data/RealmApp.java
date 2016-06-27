package secureapps.com.fitsec.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alex on 27.06.16.
 */
public class RealmApp extends RealmObject {
    @PrimaryKey
    private String packageName;

    private String name;
    private boolean secured;
    private byte[] appIcon;

    private int installations;
    private int secureCount;

    public RealmApp() {}

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    public byte[] getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(byte[] appIcon) {
        this.appIcon = appIcon;
    }

    public int getInstallations() {
        return installations;
    }

    public void setInstallations(int installations) {
        this.installations = installations;
    }

    public int getSecureCount() {
        return secureCount;
    }

    public void setSecureCount(int secureCount) {
        this.secureCount = secureCount;
    }
}
