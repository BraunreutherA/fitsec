package secureapps.com.fitsec;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import secureapps.com.fitsec.base.BaseFragment;
import timber.log.Timber;

/**
 * Created by Sarah on 30.06.2016.
 */
public class SettingsFragment extends BaseFragment {

    private DevicePolicyManager devicePolicyManager;
    private KeyguardManager keyguardManager;
    private final int ADMIN_INTENT = 15;

    private ControlOpenApp controlOpenApp;

    public SettingsFragment() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        devicePolicyManager = (DevicePolicyManager)getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        keyguardManager = (KeyguardManager)getContext().getSystemService(Context.KEYGUARD_SERVICE);

        Button enableUserStats = (Button)view.findViewById(R.id.enableUserStats);

        controlOpenApp = new ControlOpenApp(getContext());
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
                    new AlertDialog.Builder(getContext())
                            .setTitle("Nutzungsdatenzugriff")
                            .setMessage("fITsec benötigt eine weitere Berechtigung. Bitte aktivieren Sie diese in der folgenden Anwendung.")
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

        Button activateSecurity = (Button)view.findViewById(R.id.activate_security);
        activateSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCatchOpenAppData();
            }
        });

        Button setDeviceAdmin = (Button)view.findViewById(R.id.setDeviceAdmin);
        final ComponentName componentName = new ComponentName(getContext(), MyAdminReceiver.class);

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

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_settings;
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
