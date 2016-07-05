package secureapps.com.fitsec;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by Sarah on 26.06.2016.
 */
public class LockScreenActivity extends Activity {

    private DevicePolicyManager devicePolicyManager;
    private KeyguardManager keyguardManager;
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_screen);
        setVisible(false);

        devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        componentName = new ComponentName(this, MyAdminReceiver.class);

        startActivityForResult(keyguardManager.createConfirmDeviceCredentialIntent("Secured Area", "Please enter your PIN"), 15);


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("LOCK", "Result " + resultCode);

        final Activity thisActivity = this;

        if(resultCode == RESULT_OK){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
            prefs.edit().putBoolean("isUnlocked", true).commit();

            moveTaskToBack(true);

        }

        /*
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        //do nothing, user has to enter PIN
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU){
            //do nothing
            return false;
        } else {
            return  super.onKeyDown(keyCode, event);
        }
    }
}
