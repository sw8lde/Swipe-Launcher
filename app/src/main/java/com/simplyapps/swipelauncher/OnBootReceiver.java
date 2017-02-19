package com.simplyapps.swipelauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Log.d("onReceive", "autostarting");
        Log.d("onReceive", Boolean.toString(prefs.getBoolean("autostart", true)));
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) && prefs.getBoolean("autostart", false)) {
            Intent serviceIntent = new Intent(context, LauncherTriggerService.class);
            context.startService(serviceIntent);
        }
    }
}
