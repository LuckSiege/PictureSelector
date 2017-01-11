package com.yalantis.ucrop.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yalantis.ucrop.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */
public class BaseActivity extends FragmentActivity {
    protected Context mContext;
    protected int type = 0;
    protected int maxSelectNum = 0;
    protected int spanCount = 4;
    protected int copyMode = 0;
    protected boolean showCamera = false;
    protected boolean enablePreview = false;
    protected boolean enableCrop = false;
    protected boolean enablePreviewVideo = true;
    protected int selectMode = Constants.MODE_MULTIPLE;
    protected int backgroundColor = 0;
    protected int cb_drawable = 0;
    protected int cropW = 100;
    protected int cropH = 100;
    protected int recordVideoSecond = 0;
    protected int definition = 3;
    protected boolean isCompress;
    protected Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    /**
     * 针对6.0动态请求权限问题
     * 判断是否允许此权限
     *
     * @param permissions
     * @return
     */
    protected boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 动态请求权限
     *
     * @param code
     * @param permissions
     */
    protected void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readLocalMedia();
                } else {
                    showToast("读取内存卡权限已被拒绝");
                }
                break;
            case Constants.CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    showToast("拍照权限已被拒绝");
                }
                break;
        }
    }

    /**
     * 启动相机
     */
    protected void startCamera() {

    }

    /**
     * 读取相册信息
     */
    protected void readLocalMedia() {

    }

    protected void startActivity(Class act) {
        Intent intent = new Intent();
        intent.setClass(this, act);
        startActivity(intent);
    }

    protected void startActivity(Class act, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, act);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 广播发送者
     *
     * @param action
     */
    protected void sendBroadcast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }


    /**
     * 注册发送者
     *
     * @param action
     */
    protected void registerReceiver(BroadcastReceiver receiver, String... action) {
        IntentFilter intentFilter = new IntentFilter();
        for (int i = 0; i < action.length; i++) {
            intentFilter.addAction(action[i]);
        }
        registerReceiver(receiver, intentFilter);
    }

    protected void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }


    /**
     * 读取对象
     *
     * @param file
     * @return
     * @throws
     */
    public Serializable readObject(String file) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }


    /**
     * 保存对象
     *
     * @param ser
     * @param file
     * @throws
     */
    public boolean saveObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(file, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {

            }
        }
    }
}
