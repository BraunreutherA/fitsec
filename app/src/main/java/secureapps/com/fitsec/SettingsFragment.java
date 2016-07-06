package secureapps.com.fitsec;

import android.app.Activity;
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
import android.widget.CompoundButton;
import android.widget.Switch;

import secureapps.com.fitsec.base.BaseFragment;
import timber.log.Timber;

/**
 * Created by Sarah on 30.06.2016.
 */
public class SettingsFragment extends BaseFragment {

    private DevicePolicyManager devicePolicyManager;
    private KeyguardManager keyguardManager;
    private ComponentName componentName;

    private static final int LOCK_INTENT = 15;
    private int firstTime = 0;
    private boolean hasStarted = false;

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
        componentName = new ComponentName(getContext(), MyAdminReceiver.class);

        final Switch enableUserStats = (Switch)view.findViewById(R.id.enableUserStats);

        controlOpenApp = new ControlOpenApp(getContext());
        controlOpenApp.setOnAppOpenListener(new ControlOpenApp.OnAppOpenListener() {
            @Override
            public void openedApp(String packageName) {
                Timber.e("opened secured app..." + packageName);
                // TODO rework lockscreen ?!?!?!

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (!prefs.getBoolean("isUnlocked", false)) {

                    Intent lockIntent = keyguardManager.createConfirmDeviceCredentialIntent("Secured Area", "Please enter your PIN");
                    startActivityForResult(lockIntent, LOCK_INTENT);
                }
            }
        });

        checkSwitchState(enableUserStats);

        enableUserStats.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(firstTime == 0){
                    if (controlOpenApp.getUsageStatsList().isEmpty()) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Apps with Usage Access")
                                .setMessage("fITsec requires further authorization. Please enable them in the following application.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        firstTime--;
                                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        enableUserStats.setChecked(false);
                                        //Nothing happens
                                    }
                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Apps with Usage Access")
                                .setMessage("fITsec requires further authorization. If you disable them the app wont work anymore.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        firstTime--;
                                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        enableUserStats.setChecked(true);
                                        //Nothing happens
                                    }
                                })
                                .show();
                    }
                }
                if(firstTime == 1){
                    firstTime--;
                }
                else {
                    firstTime++;
                }
            }
        });


        final Switch setDeviceAdmin = (Switch) view.findViewById(R.id.setDeviceAdmin);
        final ComponentName componentName = new ComponentName(getContext(), MyAdminReceiver.class);

        if(!devicePolicyManager.isAdminActive(componentName)){
            setDeviceAdmin.setChecked(false);
        }
        else{
            setDeviceAdmin.setChecked(true);
        }

        setDeviceAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(firstTime == 0) {
                    Log.e("ADMIN", "admin is " + devicePolicyManager.isAdminActive(componentName));
                    if (!devicePolicyManager.isAdminActive(componentName)) {
                        //enable admin device rights
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Please enable admin rights");
                        startActivityForResult(intent, LOCK_INTENT);
                        firstTime--;
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Device administrator")
                                .setMessage("Disabling can only be done manually. Please go to Settings -> Security -> Device administrator menu.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        setDeviceAdmin.setChecked(true);
                                        //Nothing happens
                                    }
                                })
                                .show();
                    }
                }
                if(firstTime == 1){
                    firstTime--;
                }
                else{
                    firstTime++;
                }
            }
        });

        Switch activateSecurity = (Switch)view.findViewById(R.id.activate_security);

        if(hasStarted){
            activateSecurity.setChecked(true);
        }
        else{
            activateSecurity.setChecked(false);
        }

        activateSecurity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set fitsec itself as a secured app
                AppService appService = new AppService();
                appService.setAppSecured(getContext().getPackageName(), true);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putBoolean("isUnlocked", false).commit();

                onStartAppTimerListener.startAppTimer(isChecked);
            }
        });

    }

    private void checkSwitchState(Switch switchPosition){
        if(controlOpenApp.getUsageStatsList().isEmpty()){
            switchPosition.setChecked(false);
        }
        else{
            switchPosition.setChecked(true);
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.settings_fragment;
    }

    public void monitorAppUsage() {
        this.controlOpenApp.printCurrentUsageStatus();
    }

    interface OnStartAppTimerListener {
        void startAppTimer(boolean isRunning);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOCK_INTENT){
            if(resultCode == Activity.RESULT_OK){
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putBoolean("isUnlocked", true).commit();

                getActivity().moveTaskToBack(true);

            }
        }
    }
}
