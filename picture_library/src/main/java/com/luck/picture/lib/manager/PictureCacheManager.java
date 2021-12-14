package com.luck.picture.lib.manager;

import android.content.Context;
import android.os.Environment;
import com.luck.picture.lib.PictureMediaScannerConnection;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.listener.OnCallbackListener;
import com.luck.picture.lib.thread.PictureThreadUtils;

import java.io.File;

/**
 * @author：luck
 * @date：2021/5/28 5:50 PM
 * @describe：PictureCacheManager
 */
public class PictureCacheManager {

    /**
     * set empty PictureSelector Cache
     */
    public static void deleteCacheDirFile(String cacheDir) {
        deleteCacheDirFile(cacheDir, null);
    }

    /**
     * set empty PictureSelector Cache
     */
    public static void deleteCacheDirFile(String cacheDir, OnCallbackListener<String> listener) {
        File cacheFileDir = new File(cacheDir);
        File[] files = cacheFileDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    boolean isResult = file.delete();
                    if (isResult) {
                        if (listener != null) {
                            listener.onCall(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    /**
     * set empty PictureSelector Cache
     *
     * @param context
     * @param type    image or video ...
     */
    public static void deleteCacheRefreshDirFile(Context context, int type) {
        deleteCacheDirFile(context, type, true, null);
    }

    /**
     * set empty PictureSelector Cache
     *
     * @param context
     * @param type    image or video ...
     */
    public static void deleteCacheDirFile(Context context, int type) {
        deleteCacheDirFile(context, type, false, null);
    }

    /**
     * set empty PictureSelector Cache
     *
     * @param context
     * @param type    image or video ...
     */
    public static void deleteCacheDirFile(Context context, int type, OnCallbackListener<String> listener) {
        deleteCacheDirFile(context, type, false, listener);
    }

    /**
     * set empty PictureSelector Cache
     *
     * @param context
     * @param type    image or video ...
     */
    private static void deleteCacheDirFile(Context context, int type, boolean isRefresh, OnCallbackListener<String> listener) {
        File cutDir = context.getExternalFilesDir(type == PictureMimeType.ofImage()
                ? Environment.DIRECTORY_PICTURES : Environment.DIRECTORY_MOVIES);
        if (cutDir != null) {
            File[] files = cutDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean isResult = file.delete();
                        if (isResult) {
                            if (isRefresh) {
                                PictureThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new PictureMediaScannerConnection(context, file.getAbsolutePath());
                                    }
                                });
                            } else {
                                if (listener != null) {
                                    listener.onCall(file.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * set empty PictureSelector Cache
     *
     * @param context
     */
    public static void deleteAllCacheDirFile(Context context) {
        deleteAllCacheDirFile(context, false, null);
    }

    /**
     * set empty PictureSelector Cache
     *
     * @param context
     */
    public static void deleteAllCacheDirFile(Context context, OnCallbackListener<String> listener) {
        deleteAllCacheDirFile(context, false, listener);
    }

    /**
     * set empty PictureSelector Cache
     *
     * @param context
     */
    public static void deleteAllCacheDirRefreshFile(Context context) {
        deleteAllCacheDirFile(context, true, null);
    }

    /**
     * set empty PictureSelector Cache
     *
     * @param context
     */
    private static void deleteAllCacheDirFile(Context context, boolean isRefresh, OnCallbackListener<String> listener) {

        File dirPictures = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dirPictures != null) {
            File[] files = dirPictures.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean isResult = file.delete();
                        if (isResult) {
                            if (isRefresh) {
                                PictureThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new PictureMediaScannerConnection(context, file.getAbsolutePath());
                                    }
                                });
                            } else {
                                if (listener != null) {
                                    listener.onCall(file.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
            }
        }

        File dirMovies = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (dirMovies != null) {
            File[] files = dirMovies.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean isResult = file.delete();
                        if (isResult) {
                            if (isRefresh) {
                                PictureThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new PictureMediaScannerConnection(context, file.getAbsolutePath());
                                    }
                                });
                            } else {
                                if (listener != null) {
                                    listener.onCall(file.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
            }
        }

        File dirMusic = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dirMusic != null) {
            File[] files = dirMusic.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean isResult = file.delete();
                        if (isResult) {
                            if (isRefresh) {
                                PictureThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new PictureMediaScannerConnection(context, file.getAbsolutePath());
                                    }
                                });
                            } else {
                                if (listener != null) {
                                    listener.onCall(file.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
