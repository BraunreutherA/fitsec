package secureapps.com.fitsec;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;


public class SettingsActivity extends FragmentActivity {
    private DevicePolicyManager devicePolicyManager;
    private KeyguardManager keyguardManager;

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
            }
        });

        enableUserStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCatchOpenAppData();
//                if(controlOpenApp.getUsageStatsList().isEmpty()){
//                    new AlertDialog.Builder(SettingsActivity.this)
//                            .setTitle("Nutzungsdatenzugriff")
//                            .setMessage("fITsec benötigt eine weitere Berechtigung. Bitte aktivieren Sie diese in der folgenden Anwendung.")
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                                    startActivity(intent);
//                                }
//                            })
//                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //Nothing happens
//                                }
//                            })
//                            .show();
//                    if (controlOpenApp.getUsageStatsList().isEmpty()){
//                        startCatchOpenAppData();
//                    }
//                }
//                else{
//                    startCatchOpenAppData();
//                }
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
                    startActivityForResult(intent, 15);
                } else {
                    Intent lockIntent = keyguardManager.createConfirmDeviceCredentialIntent("test", "Teeeeeeeeeeeeest");
                    startActivityForResult(lockIntent, 15);
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
