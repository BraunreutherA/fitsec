package secureapps.com.fitsec;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;


public class SettingsActivity extends FragmentActivity {
    private DevicePolicyManager devicePolicyManager;
    private KeyguardManager keyguardManager;
    private final int ADMIN_INTENT = 15;

    private ControlOpenApp controlOpenApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);

        Button enableUserStats = (Button)findViewById(R.id.enableUserStats);

        controlOpenApp = new ControlOpenApp(this);
        controlOpenApp.setOnAppOpenListener(new ControlOpenApp.OnAppOpenListener() {
            @Override
            public void openedApp(String packageName) {
                Timber.e("opened secured app..." + packageName);
                // TODO secured app was opened -> open lock screen or something else.
                Intent lockIntent = keyguardManager.createConfirmDeviceCredentialIntent("Secured Area", "Please enter your PIN");
                startActivityForResult(lockIntent, ADMIN_INTENT);
            }
        });

        enableUserStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCatchOpenAppData();
                if(controlOpenApp.getUsageStatsList().isEmpty()){
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Apps with Usage Access")
                            .setMessage("fITsec requires further authorization. Please enable them in the following application.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Nothing happens
                                }
                            })
                            .show();
                    if (controlOpenApp.getUsageStatsList().isEmpty()){
                        //startCatchOpenAppData();
                    }
                }
                else{
                    //startCatchOpenAppData();
                }
            }
        });

        Button activateSecurity = (Button)findViewById(R.id.activate_security);
        activateSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCatchOpenAppData();
            }
        });

        Button setDeviceAdmin = (Button)findViewById(R.id.setDeviceAdmin);
        final ComponentName componentName = new ComponentName(this, MyAdminReceiver.class);

        setDeviceAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ADMIN", "admin is " + devicePolicyManager.isAdminActive(componentName));
                if(!devicePolicyManager.isAdminActive(componentName)){
                    //enable admin device rights
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Please enable admin rights");
                    startActivityForResult(intent, ADMIN_INTENT);
                }
            }
        });

    }

    private void startCatchOpenAppData(){
        Timer timer = new Timer();
        TimerTask refresher = new TimerTask() {
            public void run() {
                controlOpenApp.printCurrentUsageStatus();
            };
        };
        timer.scheduleAtFixedRate(refresher, 100,100);
    }
}
