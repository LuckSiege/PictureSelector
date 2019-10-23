package com.luck.picture.lib.tools;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.luck.picture.lib.entity.LocalMedia;

/**
 * @author：luck
 * @date：2019-10-21 17:10
 * @describe：资源处理工具类
 */
public class MediaUtils {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long extractVideoDuration(Context context, boolean isAndroidQ, String path) {
        return !TextUtils.isEmpty(path) ? isAndroidQ ? getLocalVideoDurationToAndroidQ(context, path)
                : getLocalVideoDuration(path) : 0;
    }

    /**
     * 是否是长图
     *
     * @param media
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(LocalMedia media) {
        if (null != media) {
            int width = media.getWidth();
            int height = media.getHeight();
            int h = width * 3;
            return height > h;
        }
        return false;
    }

    /**
     * get Local video duration
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getLocalVideoDurationToAndroidQ(Context context, String videoPath) {
        int duration = 0;
        if (TextUtils.isEmpty(videoPath)) {
            return duration;
        }
        try {
            Cursor query = context.getApplicationContext().getContentResolver().query(Uri.parse(videoPath),
                    null, null, null);
            if (query != null) {
                query.moveToFirst();
                duration = query.getInt(query.getColumnIndexOrThrow(MediaStore.Video
                        .Media.DURATION));
                return duration;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * get Local video duration
     *
     * @return
     */
    public static int getLocalVideoDuration(String videoPath) {
        int duration = 0;
        if (TextUtils.isEmpty(videoPath)) {
            return duration;
        }
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return duration;
    }
}
