package com.luck.picture.lib.tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;

import com.luck.picture.lib.R;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.tool
 * email：893855882@qq.com
 * data：2017/5/25
 */

public class VoiceUtils {
    private static SoundPool soundPool;
    private static int soundID;//创建某个声音对应的音频ID
    private boolean isPlay;

    /**
     * start SoundPool
     */
    public static void playVoice(Context mContext, final boolean enableVoice) {

        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
            soundID = soundPool.load(mContext, R.raw.music, 1);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                play(enableVoice, soundPool);
            }
        }, 20);
    }

    public static void play(boolean enableVoice, SoundPool soundPool) {
        if (enableVoice) {
            soundPool.play(
                    soundID,
                    0.1f,
                    0.5f,
                    0,
                    1,
                    1
            );
        }
    }

    /**
     * release SoundPool
     */
    public static void release() {
        if (soundPool != null) {
            soundPool.stop(soundID);
        }
        soundPool = null;
    }
}
