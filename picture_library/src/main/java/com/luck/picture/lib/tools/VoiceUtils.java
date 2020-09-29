package com.luck.picture.lib.tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.luck.picture.lib.R;

/**
 * @author：luck
 * @data：2017/5/25 19:12
 * @描述: voice utils
 */
public class VoiceUtils {

    private static VoiceUtils instance;

    public static VoiceUtils getInstance() {
        if (instance == null) {
            synchronized (VoiceUtils.class) {
                if (instance == null) {
                    instance = new VoiceUtils();
                }
            }
        }
        return instance;
    }

    public VoiceUtils() {
    }

    private SoundPool soundPool;
    /**
     * 创建某个声音对应的音频ID
     */
    private int soundID;

    public void init(Context context) {
        initPool(context);
    }

    private void initPool(Context context) {
        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
            soundID = soundPool.load(context.getApplicationContext(), R.raw.picture_music, 1);
        }
    }

    /**
     * 播放音频
     */
    public void play() {
        if (soundPool != null) {
            soundPool.play(soundID, 0.1f, 0.5f, 0, 1, 1);
        }
    }

    /**
     * 释放资源
     */
    public void releaseSoundPool() {
        try {
            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
            }
            instance = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
