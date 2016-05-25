package secureapps.com.fitsec.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * @author Alexander Braunreuther
 */
@ParseClassName("App")
public class App extends ParseObject {
    public App() {}

    public String getAppImageUrl() {
        return getString("imageUrl");
    }

    public String getAppName() {
        return getString("name");
    }

    public int getSecuredCount() {
        return getInt("secureCount");
    }

    public void incrementSecuredCount() {
        increment("secureCount");
    }

    public void decrementSecureCount() {
        increment("secureCount", -1);
    }

    public int getFakeSecureCount() {
        return getInt("fakeSecureCount");
    }

    public int getUserCount() {
        return getInt("userCount");
    }
}
