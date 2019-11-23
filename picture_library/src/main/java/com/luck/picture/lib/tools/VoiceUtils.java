package com.luck.picture.lib.tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;

import com.luck.picture.lib.R;

/**
 * @author：luck
 * @data：2017/5/25 19:12
 * @描述: voice utils
 */
public class VoiceUtils {
    private static SoundPool soundPool;
    private static int soundID;//创建某个声音对应的音频ID

    /**
     * start SoundPool
     */
    public static void playVoice(Context mContext, final boolean enableVoice) {

        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
            soundID = soundPool.load(mContext, R.raw.picture_music, 1);
        }
        new Handler().postDelayed(() -> play(enableVoice, soundPool), 20);
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
}
