package secureapps.com.fitsec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Sarah on 03.07.2016.
 */
public class Receiver extends BroadcastReceiver {
    public Receiver () {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Received", String.format("Intent Detected. %s", intent));

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.e("Received", "Screen off detected");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit().putBoolean("isUnlocked", false).commit();

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.e("Received", "Screen on detected");

        }

    }
}
