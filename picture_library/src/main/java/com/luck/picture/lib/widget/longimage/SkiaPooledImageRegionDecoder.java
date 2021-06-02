package com.luck.picture.lib.widget.longimage;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * <p>
 * An implementation of {@link ImageRegionDecoder} using a pool of {@link BitmapRegionDecoder}s,
 * to provide true parallel loading of tiles. This is only effective if parallel loading has been
 * enabled in the view by calling {@link SubsamplingScaleImageView#setExecutor(Executor)}
 * with a multi-threaded {@link Executor} instance.
 * </p><p>
 * One decoder is initialised when the class is initialised. This is enough to decode base layer tiles.
 * Additional decoders are initialised when a subregion of the image is first requested, which indicates
 * interaction with the view. Creation of additional encoders stops when {@link #allowAdditionalDecoder(int, long)}
 * returns false. The default implementation takes into account the file size, number of CPU cores,
 * low memory status and a hard limit of 4. Extend this class to customise this.
 * </p><p>
 * <b>WARNING:</b> This class is highly experimental and not proven to be stable on a wide range of
 * devices. You are advised to test it thoroughly on all available devices, and code your app to use
 * {@link SkiaImageRegionDecoder} on old or low powered devices you could not test.
 * </p>
 */
public class SkiaPooledImageRegionDecoder implements ImageRegionDecoder {

    private static final String TAG = SkiaPooledImageRegionDecoder.class.getSimpleName();

    private static boolean debug = false;

    private DecoderPool decoderPool = new DecoderPool();
    private final ReadWriteLock decoderLock = new ReentrantReadWriteLock(true);

    private static final String FILE_PREFIX = "file://";
    private static final String ASSET_PREFIX = FILE_PREFIX + "/android_asset/";
    private static final String RESOURCE_PREFIX = ContentResolver.SCHEME_ANDROID_RESOURCE + "://";

    private final Bitmap.Config bitmapConfig;

    private Context context;
    private Uri uri;

    private long fileLength = Long.MAX_VALUE;
    private final Point imageDimensions = new Point(0, 0);
    private final AtomicBoolean lazyInited = new AtomicBoolean(false);

    @Keep
    @SuppressWarnings("unused")
    public SkiaPooledImageRegionDecoder() {
        this(null);
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    public SkiaPooledImageRegionDecoder(@Nullable Bitmap.Config bitmapConfig) {
        Bitmap.Config globalBitmapConfig = SubsamplingScaleImageView.getPreferredBitmapConfig();
        if (bitmapConfig != null) {
            this.bitmapConfig = bitmapConfig;
        } else if (globalBitmapConfig != null) {
            this.bitmapConfig = globalBitmapConfig;
        } else {
            this.bitmapConfig = Bitmap.Config.RGB_565;
        }
    }

    /**
     * Controls logging of debug messages. All instances are affected.
     * @param debug true to enable debug logging, false to disable.
     */
    @Keep
    @SuppressWarnings("unused")
    public static void setDebug(boolean debug) {
        SkiaPooledImageRegionDecoder.debug = debug;
    }

    /**
     * Initialises the decoder pool. This method creates one decoder on the current thread and uses
     * it to decode the bounds, then spawns an independent thread to populate the pool with an
     * additional three decoders. The thread will abort if {@link #recycle()} is called.
     */
    @Override
    @NonNull
    public Point init(final Context context, @NonNull final Uri uri) throws Exception {
        this.context = context;
        this.uri = uri;
        initialiseDecoder();
        return this.imageDimensions;
    }

    /**
     * Initialises extra decoders for as long as {@link #allowAdditionalDecoder(int, long)} returns
     * true and the pool has not been recycled.
     */
    private void lazyInit() {
        if (lazyInited.compareAndSet(false, true) && fileLength < Long.MAX_VALUE) {
            debug("Starting lazy init of additional decoders");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (decoderPool != null && allowAdditionalDecoder(decoderPool.size(), fileLength)) {
                        // New decoders can be created while reading tiles but this read lock prevents
                        // them being initialised while the pool is being recycled.
                        try {
                            if (decoderPool != null) {
                                long start = System.currentTimeMillis();
                                debug("Starting decoder");
                                initialiseDecoder();
                                long end = System.currentTimeMillis();
                                debug("Started decoder, took " + (end - start) + "ms");
                            }
                        } catch (Exception e) {
                            // A decoder has already been successfully created so we can ignore this
                            debug("Failed to start decoder: " + e.getMessage());
                        }
                    }
                }
            };
            thread.start();
        }
    }

    /**
     * Initialises a new {@link BitmapRegionDecoder} and adds it to the pool, unless the pool has
     * been recycled while it was created.
     */
    private void initialiseDecoder() throws Exception {
        String uriString = uri.toString();
        BitmapRegionDecoder decoder;
        long fileLength = Long.MAX_VALUE;
        if (uriString.startsWith(RESOURCE_PREFIX)) {
            Resources res;
            String packageName = uri.getAuthority();
            if (context.getPackageName().equals(packageName)) {
                res = context.getResources();
            } else {
                PackageManager pm = context.getPackageManager();
                res = pm.getResourcesForApplication(packageName);
            }

            int id = 0;
            List<String> segments = uri.getPathSegments();
            int size = segments.size();
            if (size == 2 && segments.get(0).equals("drawable")) {
                String resName = segments.get(1);
                id = res.getIdentifier(resName, "drawable", packageName);
            } else if (size == 1 && TextUtils.isDigitsOnly(segments.get(0))) {
                try {
                    id = Integer.parseInt(segments.get(0));
                } catch (NumberFormatException ignored) {
                }
            }
            try {
                AssetFileDescriptor descriptor = context.getResources().openRawResourceFd(id);
                fileLength = descriptor.getLength();
            } catch (Exception e) {
                // Pooling disabled
            }
            decoder = BitmapRegionDecoder.newInstance(context.getResources().openRawResource(id), false);
        } else if (uriString.startsWith(ASSET_PREFIX)) {
            String assetName = uriString.substring(ASSET_PREFIX.length());
            try {
                AssetFileDescriptor descriptor = context.getAssets().openFd(assetName);
                fileLength = descriptor.getLength();
            } catch (Exception e) {
                // Pooling disabled
            }
            decoder = BitmapRegionDecoder.newInstance(context.getAssets().open(assetName, AssetManager.ACCESS_RANDOM), false);
        } else if (uriString.startsWith(FILE_PREFIX)) {
            decoder = BitmapRegionDecoder.newInstance(uriString.substring(FILE_PREFIX.length()), false);
            try {
                File file = new File(uriString);
                if (file.exists()) {
                    fileLength = file.length();
                }
            } catch (Exception e) {
                // Pooling disabled
            }
        } else {
            InputStream inputStream = null;
            try {
                ContentResolver contentResolver = context.getContentResolver();
                inputStream = contentResolver.openInputStream(uri);
                decoder = BitmapRegionDecoder.newInstance(inputStream, false);
                try {
                    AssetFileDescriptor descriptor = contentResolver.openAssetFileDescriptor(uri, "r");
                    if (descriptor != null) {
                        fileLength = descriptor.getLength();
                    }
                } catch (Exception e) {
                    // Stick with MAX_LENGTH
                }
            } finally {
                if (inputStream != null) {
                    try { inputStream.close(); } catch (Exception e) { /* Ignore */ }
                }
            }
        }

        this.fileLength = fileLength;
        this.imageDimensions.set(decoder.getWidth(), decoder.getHeight());
        decoderLock.writeLock().lock();
        try {
            if (decoderPool != null) {
                decoderPool.add(decoder);
            }
        } finally {
            decoderLock.writeLock().unlock();
        }
    }

    /**
     * Acquire a read lock to prevent decoding overlapping with recycling, then check the pool still
     * exists and acquire a decoder to load the requested region. There is no check whether the pool
     * currently has decoders, because it's guaranteed to have one decoder after {@link #init(Context, Uri)}
     * is called and be null once {@link #recycle()} is called. In practice the view can't call this
     * method until after {@link #init(Context, Uri)}, so there will be no blocking on an empty pool.
     */
    @Override
    @NonNull
    public Bitmap decodeRegion(@NonNull Rect sRect, int sampleSize) {
        debug("Decode region " + sRect + " on thread " + Thread.currentThread().getName());
        if (sRect.width() < imageDimensions.x || sRect.height() < imageDimensions.y) {
            lazyInit();
        }
        decoderLock.readLock().lock();
        try {
            if (decoderPool != null) {
                BitmapRegionDecoder decoder = decoderPool.acquire();
                try {
                    // Decoder can't be null or recycled in practice
                    if (decoder != null && !decoder.isRecycled()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = sampleSize;
                        options.inPreferredConfig = bitmapConfig;
                        Bitmap bitmap = decoder.decodeRegion(sRect, options);
                        if (bitmap == null) {
                            throw new RuntimeException("Skia image decoder returned null bitmap - image format may not be supported");
                        }
                        return bitmap;
                    }
                } finally {
                    if (decoder != null) {
                        decoderPool.release(decoder);
                    }
                }
            }
            throw new IllegalStateException("Cannot decode region after decoder has been recycled");
        } finally {
            decoderLock.readLock().unlock();
        }
    }

    /**
     * Holding a read lock to avoid returning true while the pool is being recycled, this returns
     * true if the pool has at least one decoder available.
     */
    @Override
    public synchronized boolean isReady() {
        return decoderPool != null && !decoderPool.isEmpty();
    }

    /**
     * Wait until all read locks held by {@link #decodeRegion(Rect, int)} are released, then recycle
     * and destroy the pool. Elsewhere, when a read lock is acquired, we must check the pool is not null.
     */
    @Override
    public synchronized void recycle() {
        decoderLock.writeLock().lock();
        try {
            if (decoderPool != null) {
                decoderPool.recycle();
                decoderPool = null;
                context = null;
                uri = null;
            }
        } finally {
            decoderLock.writeLock().unlock();
        }
    }

    /**
     * Called before creating a new decoder. Based on number of CPU cores, available memory, and the
     * size of the image file, determines whether another decoder can be created. Subclasses can
     * override and customise this.
     * @param numberOfDecoders the number of decoders that have been created so far
     * @param fileLength the size of the image file in bytes. Creating another decoder will use approximately this much native memory.
     * @return true if another decoder can be created.
     */
    @SuppressWarnings("WeakerAccess")
    protected boolean allowAdditionalDecoder(int numberOfDecoders, long fileLength) {
        if (numberOfDecoders >= 4) {
            debug("No additional decoders allowed, reached hard limit (4)");
            return false;
        } else if (numberOfDecoders * fileLength > 20 * 1024 * 1024) {
            debug("No additional encoders allowed, reached hard memory limit (20Mb)");
            return false;
        } else if (numberOfDecoders >= getNumberOfCores()) {
            debug("No additional encoders allowed, limited by CPU cores (" + getNumberOfCores() + ")");
            return false;
        } else if (isLowMemory()) {
            debug("No additional encoders allowed, memory is low");
            return false;
        }
        debug("Additional decoder allowed, current count is " + numberOfDecoders + ", estimated native memory " + ((fileLength * numberOfDecoders)/(1024 * 1024)) + "Mb");
        return true;
    }


    /**
     * A simple pool of {@link BitmapRegionDecoder} instances, all loading from the same source.
     */
    private static class DecoderPool {
        private final Semaphore available = new Semaphore(0, true);
        private final Map<BitmapRegionDecoder, Boolean> decoders = new ConcurrentHashMap<>();

        /**
         * Returns false if there is at least one decoder in the pool.
         */
        private synchronized boolean isEmpty() {
            return decoders.isEmpty();
        }

        /**
         * Returns number of encoders.
         */
        private synchronized int size() {
            return decoders.size();
        }

        /**
         * Acquire a decoder. Blocks until one is available.
         */
        private BitmapRegionDecoder acquire() {
            available.acquireUninterruptibly();
            return getNextAvailable();
        }

        /**
         * Release a decoder back to the pool.
         */
        private void release(BitmapRegionDecoder decoder) {
            if (markAsUnused(decoder)) {
                available.release();
            }
        }

        /**
         * Adds a newly created decoder to the pool, releasing an additional permit.
         */
        private synchronized void add(BitmapRegionDecoder decoder) {
            decoders.put(decoder, false);
            available.release();
        }

        /**
         * While there are decoders in the map, wait until each is available before acquiring,
         * recycling and removing it. After this is called, any call to {@link #acquire()} will
         * block forever, so this call should happen within a write lock, and all calls to
         * {@link #acquire()} should be made within a read lock so they cannot end up blocking on
         * the semaphore when it has no permits.
         */
        private synchronized void recycle() {
            while (!decoders.isEmpty()) {
                BitmapRegionDecoder decoder = acquire();
                decoder.recycle();
                decoders.remove(decoder);
            }
        }

        private synchronized BitmapRegionDecoder getNextAvailable() {
            for (Map.Entry<BitmapRegionDecoder, Boolean> entry : decoders.entrySet()) {
                if (!entry.getValue()) {
                    entry.setValue(true);
                    return entry.getKey();
                }
            }
            return null;
        }

        private synchronized boolean markAsUnused(BitmapRegionDecoder decoder) {
            for (Map.Entry<BitmapRegionDecoder, Boolean> entry : decoders.entrySet()) {
                if (decoder == entry.getKey()) {
                    if (entry.getValue()) {
                        entry.setValue(false);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

    }

    private int getNumberOfCores() {
        if (Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return getNumCoresOldPhones();
        }
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     * @return The number of cores, or 1 if failed to get result
     */
    private int getNumCoresOldPhones() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                return Pattern.matches("cpu[0-9]+", pathname.getName());
            }
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch(Exception e) {
            return 1;
        }
    }

    private boolean isLowMemory() {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            return memoryInfo.lowMemory;
        } else {
            return true;
        }
    }

    private void debug(String message) {
        if (debug) {
            Log.d(TAG, message);
        }
    }

}
