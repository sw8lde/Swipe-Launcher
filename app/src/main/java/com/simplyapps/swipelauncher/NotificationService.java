package com.simplyapps.swipelauncher;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;

public class NotificationService extends NotificationListenerService {
    private final IBinder binder = new ServiceBinder();
    private boolean isBound = false;

    public NotificationService() {
    }

    public class ServiceBinder extends Binder {
        NotificationService getService() {
            return NotificationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        isBound = true;
        String action = intent.getAction();

        if (SERVICE_INTERFACE.equals(action)) {
            return super.onBind(intent);
        } else {
            return binder;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isBound() {
        return isBound;
    }
}
