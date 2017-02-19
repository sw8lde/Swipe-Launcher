package com.simplyapps.swipelauncher;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;

public class LauncherTriggerService extends Service implements
        View.OnTouchListener,
        View.OnHoverListener,
        View.OnLongClickListener {
    public static Boolean isRunning = false;
    private static NotificationService notifs;
    private static ServiceConnection connection;
    private WindowManager manager;
    private View launcherTrigger;
    private boolean longPressRemove;
    private boolean vibrateOnLongPress;
    private boolean hoverTrigger;
    private int triggerWidth;
    private int triggerHeight;
    private float triggerOffset;
    private int triggerColor;
    private int widthPixels;
    private int heightPixels;

    public LauncherTriggerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        initSettings();

        launcherTrigger = new View(this);

        Drawable background = ContextCompat.getDrawable(this, R.drawable.spot_background);
        background.setTint(triggerColor);
        launcherTrigger.setBackground(background);

        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
                triggerWidth,
                triggerHeight);
        launcherTrigger.setLayoutParams(lp);
        launcherTrigger.setOnLongClickListener(this);
        launcherTrigger.setOnTouchListener(this);
        launcherTrigger.setOnHoverListener(this);

        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                triggerWidth,
                triggerHeight,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.horizontalMargin = triggerOffset;
        manager.addView(launcherTrigger, params);

        notifs = new NotificationService();
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                NotificationService.ServiceBinder binder = (NotificationService.ServiceBinder)service;
                notifs = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        getApplicationContext().startService(intent);
        getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void initSettings() {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        longPressRemove = prefs.getBoolean("longPressRemove", true);
        hoverTrigger = prefs.getBoolean("hoverTrigger", false);
        vibrateOnLongPress = prefs.getBoolean("vibrateOnLongPress", false);
        widthPixels = getResources().getDisplayMetrics().widthPixels;
        heightPixels = getResources().getDisplayMetrics().heightPixels;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            triggerWidth = (prefs.getInt("triggerWidth", 4) + 1) * widthPixels / 20;
        } else {
            triggerWidth = (prefs.getInt("triggerWidth", 4) + 1) * heightPixels / 20;
        }
        triggerHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, prefs.getInt("triggerHeight", 4) + 1,
                getResources().getDisplayMetrics());
        triggerOffset = (prefs.getInt("triggerOffset", 0) * 10 - 50) / 100.0f;
        Log.d("OFFSET", "" + triggerOffset);
        triggerColor = prefs.getInt("triggerColor", ContextCompat.getColor(this, R.color.triggerColor));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            triggerWidth = ((prefs.getInt("triggerWidth", 4) + 1) * widthPixels) / 20;
            manager.removeView(launcherTrigger);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    triggerWidth,
                    triggerHeight,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.horizontalMargin = triggerOffset;
            ViewGroup.LayoutParams triggerParams = launcherTrigger.getLayoutParams();
            triggerParams.width = triggerWidth;
            manager.addView(launcherTrigger, params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            triggerWidth = ((prefs.getInt("triggerWidth", 4) + 1) * heightPixels) / 20;
            manager.removeView(launcherTrigger);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    triggerWidth,
                    triggerHeight,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.horizontalMargin = triggerOffset;
            ViewGroup.LayoutParams triggerParams = launcherTrigger.getLayoutParams();
            triggerParams.width = triggerWidth;
            manager.addView(launcherTrigger, params);
        }
    }

    @Override
    public void onDestroy() {
        if(manager != null && launcherTrigger != null) {
            manager.removeView(launcherTrigger);
        }

        isRunning = false;
        getApplicationContext().unbindService(connection);
        connection = null;

        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        double initialY = 0, finalY;
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            initialY = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            finalY = event.getY();
            if(initialY > finalY) {
                LauncherPopupWindow launcher = new LauncherPopupWindow(LauncherTriggerService.this, R.style.LauncherTheme);
                launcher.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                launcher.show();

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        if(longPressRemove){
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 250 milliseconds
            if(vibrateOnLongPress) {
                vibrator.vibrate(250);
            }

            //send notif to restart service
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(getString(R.string.notif_title))
                    .setAutoCancel(true)
                    .setContentText(getString(R.string.notif_text));

            Intent notifIntent = new Intent(this, LauncherTriggerService.class);
            PendingIntent notifPendingIntent = PendingIntent.getService(
                    this,
                    0,
                    notifIntent,
                    0);
            builder.setContentIntent(notifPendingIntent);

            // Sets an ID for the notification
            int notifId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            notifManager.notify(notifId, builder.build());

            this.stopService(new Intent(this, LauncherTriggerService.class));
            return true;
        }
        return false;
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        if(hoverTrigger) {
            Toast.makeText(LauncherTriggerService.this, "Hover", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public static int getFilter() {
        return notifs.getCurrentInterruptionFilter();
    }

    public static void setFilter(int filter) {
        notifs.requestInterruptionFilter(filter);
    }
}
