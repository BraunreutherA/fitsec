package secureapps.com.fitsec;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

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
    private OnStartAppTimerListener onStartAppTimerListener;

    public void setOnStartAppTimerListener(OnStartAppTimerListener onStartAppTimerListener) {
        this.onStartAppTimerListener = onStartAppTimerListener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActionBar().setTitle(R.string.action_settings);

        devicePolicyManager = (DevicePolicyManager)getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        keyguardManager = (KeyguardManager)getContext().getSystemService(Context.KEYGUARD_SERVICE);

        Button enableUserStats = (Button)view.findViewById(R.id.enableUserStats);

        controlOpenApp = new ControlOpenApp(getContext());
        controlOpenApp.setOnAppOpenListener(new ControlOpenApp.OnAppOpenListener() {
            @Override
            public void openedApp(String packageName) {
                Timber.e("opened secured app..." + packageName);
                // TODO rework lockscreen ?!?!?!
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (prefs.getBoolean("isUnlocked", false)) {
                    Intent lockIntent = new Intent(getActivity(), LockScreenActivity.class);
                    startActivityForResult(lockIntent, ADMIN_INTENT);
                }
                //Intent lockIntent = keyguardManager.createConfirmDeviceCredentialIntent("Secured Area", "Please enter your PIN");
               //getActivity().startActivityForResult(lockIntent, ADMIN_INTENT);
            }
        });

        enableUserStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startCatchOpenAppData();
                if(controlOpenApp.getUsageStatsList().isEmpty()) {
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
                }
            }
        });

        Button activateSecurity = (Button)view.findViewById(R.id.activate_security);
        activateSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Timer has to be managed through main activity
                onStartAppTimerListener.startAppTimer();
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
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Bitte Admin Rechte aktivieren");
                    startActivityForResult(intent, ADMIN_INTENT);
                }
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.settings_fragment;
    }

    public void monitorAppUsage() {
        this.controlOpenApp.printCurrentUsageStatus();
    }

    /*
    private void startCatchOpenAppData(){
        Timer timer = new Timer();
        TimerTask refresher = new TimerTask() {
            public void run() {
                controlOpenApp.printCurrentUsageStatus();
            };
        };
        timer.scheduleAtFixedRate(refresher, 100,100);
    }*/

    interface OnStartAppTimerListener {
        void startAppTimer();
    }
}
