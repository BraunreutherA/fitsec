package secureapps.com.fitsec.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * @author Alexander Braunreuther
 */
@ParseClassName("App")
public class App extends ParseObject {
    public static final String KEY_PACKAGE_NAME = "packageName";
    private static final String KEY_INSTALLATION_COUNT = "installationCount";
    public static final String KEY_SECURE_COUNT = "secureCount";


    public App() {}

    public void setPackageName(String packageName) {
        put(KEY_PACKAGE_NAME, packageName);
    }

    public String getPackageName() {
        return getString(KEY_PACKAGE_NAME);
    }

    public void incrementSecureCount() {
        increment(KEY_SECURE_COUNT);
    }

    public void decrementSecureCount() {
        increment(KEY_SECURE_COUNT, -1);
    }

    public int getSecureCount() {
        return getInt(KEY_SECURE_COUNT);
    }

    public void incrementInstallationCount() {
        increment(KEY_INSTALLATION_COUNT);
    }

    public int getInstallationCount() {
        return getInt(KEY_INSTALLATION_COUNT);
    }
}
