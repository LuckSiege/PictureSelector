package com.luck.picture.lib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author：luck
 * @date：2019-11-20 13:45
 * @describe：本地广播
 */
public class BroadcastManager {
    private static final String TAG = BroadcastManager.class.getSimpleName();
    private LocalBroadcastManager localBroadcastManager;

    private Intent intent;
    private String action;

    public static BroadcastManager getInstance(Context ctx) {
        BroadcastManager broadcastManager = new BroadcastManager();
        broadcastManager.localBroadcastManager = LocalBroadcastManager.getInstance(ctx.getApplicationContext());
        return broadcastManager;
    }


    public BroadcastManager intent(Intent intent) {
        this.intent = intent;
        return this;
    }


    public BroadcastManager action(String action) {
        this.action = action;
        return this;
    }

    public BroadcastManager extras(Bundle bundle) {
        createIntent();

        if (intent == null) {
            Log.e(TAG, "intent create failed");
            return this;
        }
        intent.putExtras(bundle);
        return this;
    }


    public BroadcastManager put(String key, ArrayList<? extends Parcelable> value) {
        createIntent();

        if (intent == null) {
            Log.e(TAG, "intent create failed");
            return this;
        }

        intent.putExtra(key, value);
        return this;
    }

    public BroadcastManager put(String key, Parcelable[] value) {
        createIntent();

        if (intent == null) {
            Log.e(TAG, "intent create failed");
            return this;
        }

        intent.putExtra(key, value);
        return this;
    }


    public BroadcastManager put(String key, Parcelable value) {
        createIntent();

        if (intent == null) {
            Log.e(TAG, "intent create failed");
            return this;
        }

        intent.putExtra(key, value);
        return this;
    }

    public BroadcastManager put(String key, float value) {
        createIntent();

        if (intent == null) {
            Log.e(TAG, "intent create failed");
            return this;
        }

        intent.putExtra(key, value);
        return this;
    }

    public BroadcastManager put(String key, double value) {
        createIntent();

        if (intent == null) {
            Log.e(TAG, "intent create failed");
            return this;
        }

        intent.putExtra(key, value);
        return this;
    }

    public BroadcastManager put(String key, long value) {
        createIntent();

        if (intent == null) {
            Log.e(TAG, "intent create failed");
            return this;
        }

        intent.putExtra(key, value);
        return this;
    }

    public BroadcastManager put(String key, boolean value) {
        createIntent();

        if (intent == null) {
            Log.e(TAG, "intent create failed");
            return this;
        }

        intent.putExtra(key, value);
        return this;
    }

    public BroadcastManager put(String key, int value) {
        createIntent();

        if (intent == null) {
            Log.e(TAG, "intent create failed");
            return this;
        }

        intent.putExtra(key, value);
        return this;
    }


    public BroadcastManager put(String key, String str) {
        createIntent();

        if (intent == null) {
            Log.e(TAG, "intent create failed");
            return this;
        }

        intent.putExtra(key, str);
        return this;
    }

    private void createIntent() {
        if (intent == null) {
            Log.d(TAG, "intent is not created");
        }

        if (intent == null) {
            if (!TextUtils.isEmpty(action)) {
                intent = new Intent(action);
            }
            Log.d(TAG, "intent created with action");
        }
    }


    public void broadcast() {

        createIntent();

        if (intent == null) {
            return;
        }

        if (action == null) {
            return;
        }

        intent.setAction(action);

        if (null != localBroadcastManager) {
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    public void registerReceiver(BroadcastReceiver br, List<String> actions) {
        if (null == br || null == actions) {
            return;
        }
        IntentFilter iFilter = new IntentFilter();
        if (actions != null) {
            for (String action : actions) {
                iFilter.addAction(action);
            }
        }
        if (null != localBroadcastManager) {
            localBroadcastManager.registerReceiver(br, iFilter);
        }
    }


    public void registerReceiver(BroadcastReceiver br, String... actions) {
        if (actions == null || actions.length <= 0) {
            return;
        }
        registerReceiver(br, Arrays.asList(actions));
    }


    /**
     * @param br
     */
    public void unregisterReceiver(BroadcastReceiver br) {
        if (null == br) {
            return;
        }

        try {
            localBroadcastManager.unregisterReceiver(br);
        } catch (Exception e) {

        }
    }

    /**
     * @param br
     * @param actions 至少传入一个
     */
    public void unregisterReceiver(BroadcastReceiver br, @NonNull String... actions) {
        unregisterReceiver(br);
    }
}
