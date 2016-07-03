package secureapps.com.fitsec;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

        devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        componentName = new ComponentName(this, MyAdminReceiver.class);

        startActivityForResult(keyguardManager.createConfirmDeviceCredentialIntent("Secured Area", "Please enter your PIN"), 15);

        /*Button button = (Button)findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRes
                finish();
            }
        });*/

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("LOCK", "Result " + resultCode);

        if(resultCode == RESULT_OK){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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

    /*
    public void openLockScreen(){

        boolean isAdmin = devicePolicyManager.isAdminActive(componentName);
        if(!isAdmin){
            //enable admin device rights
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Please enable admin rights");
            startActivityForResult(intent, ADMIN_INTENT);
        }

        Intent lockIntent = keyguardManager.createConfirmDeviceCredentialIntent("test", "Teeeeeeeeeeeeest");
        startActivityForResult(lockIntent, ADMIN_INTENT);


    }*/
}
