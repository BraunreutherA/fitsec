package secureapps.com.fitsec;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);

        final Activity thisActivity = this;

        Button enableUserStats = (Button)findViewById(R.id.enableUserStats);
        enableUserStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ControlOpenApp.getUsageStatsList(thisActivity).isEmpty()){
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                }
            }
        });

        Button setDeviceAdmin = (Button)findViewById(R.id.setDeviceAdmin);
        final ComponentName componentName = new ComponentName(thisActivity, MyAdminReceiver.class);

        setDeviceAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!devicePolicyManager.isAdminActive(componentName)){
                    //enable admin device rights
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Please enable admin rights");
                    startActivityForResult(intent, 15);
                }

            }
        });
    }
}
