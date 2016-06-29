package secureapps.com.fitsec;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Sarah on 28.06.2016.
 */
public class ControlOpenAppCallback extends Activity implements IControlOpenAppCallback {
    @Override
    public void openAppCallback() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        ComponentName componentName = new ComponentName(this, MyAdminReceiver.class);

        boolean isAdmin = devicePolicyManager.isAdminActive(componentName);
        if (isAdmin) {
            //devicePolicyManager.lockNow();
            Intent lockIntent = keyguardManager.createConfirmDeviceCredentialIntent("test", "Teeeeeeeeeeeeest");
            startActivityForResult(lockIntent, 15);
        }else{
            Toast.makeText(getApplicationContext(), "Not Registered as admin", Toast.LENGTH_SHORT).show();
        }

    }
}
