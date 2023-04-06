package com.luck.picture.lib.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.luck.picture.lib.BuildConfig;
import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.utils.SdkVersionUtils;

/**
 * @author：luck
 * @date：2021/12/2 11:01 上午
 * @describe：ForegroundService
 */
public class ForegroundService extends Service {
    private static final String CHANNEL_ID = BuildConfig.LIBRARY_PACKAGE_NAME + "." + ForegroundService.class.getName();
    private static final String CHANNEL_NAME = BuildConfig.LIBRARY_PACKAGE_NAME;
    private static final int NOTIFICATION_ID = 1;
    private static boolean isForegroundServiceIng = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = createForegroundNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ForegroundService.isForegroundServiceIng = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        ForegroundService.isForegroundServiceIng = false;
        stopForeground(true);
        super.onDestroy();
    }

    /**
     * 创建前台通知Notification
     *
     * @return
     */
    private Notification createForegroundNotification() {
        int importance = 0;
        if (SdkVersionUtils.isMaxN()) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        if (SdkVersionUtils.isO()) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setLightColor(Color.BLUE);
            channel.canBypassDnd();
            channel.setBypassDnd(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        SelectorConfig config = SelectorProviders.getInstance().getSelectorConfig();
        String contentText = config.chooseMode == SelectMimeType.ofAudio()
                ? getString(R.string.ps_use_sound) : getString(R.string.ps_use_camera);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ps_ic_trans_1px)
                .setContentTitle(getAppName())
                .setContentText(contentText)
                .setOngoing(true)
                .build();
    }

    private String getAppName() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * start foreground service
     *
     * @param context
     */
    public static void startForegroundService(Context context, boolean isCameraForegroundService) {
        try {
            if (!isForegroundServiceIng && isCameraForegroundService) {
                Intent intent = new Intent(context, ForegroundService.class);
                if (SdkVersionUtils.isO()) {
                    context.startForegroundService(intent);
                } else {
                    context.startService(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * stop foreground service
     *
     * @param context
     */
    public static void stopService(Context context) {
        try {
            if (isForegroundServiceIng) {
                Intent foregroundService = new Intent(context, ForegroundService.class);
                context.stopService(foregroundService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
