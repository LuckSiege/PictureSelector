package com.luck.picture.lib.io;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.LruCache;

import com.luck.picture.lib.tools.PictureFileUtils;

import java.io.InputStream;
import java.util.HashSet;

/**
 * @author：luck
 * @date：2021/8/26 4:07 下午
 * @describe：ArrayPoolProvide
 */
public class ArrayPoolProvide {
    /**
     * Uri对应的BufferedInputStreamWrap缓存Key
     */
    private final HashSet<String> keyCache = new HashSet<>();

    /**
     * Uri对应的BufferedInputStreamWrap缓存数据
     */
    private final LruCache<String, BufferedInputStreamWrap> bufferedLruCache = new LruCache<>(20);

    /**
     * byte[]数组的缓存队列
     */
    private final LruArrayPool arrayPool = new LruArrayPool(LruArrayPool.DEFAULT_SIZE);

    /**
     * 获取相应的byte数组
     *
     * @param bufferSize
     */
    public byte[] get(int bufferSize) {
        return arrayPool.get(bufferSize, byte[].class);
    }

    /**
     * 缓存相应的byte数组
     *
     * @param buffer
     */
    public void put(byte[] buffer) {
        arrayPool.put(buffer);
    }

    /**
     * ContentResolver openInputStream
     *
     * @param resolver ContentResolver
     * @param uri      data
     * @return
     */
    public InputStream openInputStream(ContentResolver resolver, Uri uri) {
        BufferedInputStreamWrap bufferedInputStreamWrap;
        try {
            bufferedInputStreamWrap = bufferedLruCache.get(uri.toString());
            if (bufferedInputStreamWrap != null) {
                bufferedInputStreamWrap.reset();
            } else {
                bufferedInputStreamWrap = wrapInputStream(resolver, uri);
            }
        } catch (Exception e) {
            bufferedInputStreamWrap = wrapInputStream(resolver, uri);
        }
        return bufferedInputStreamWrap;
    }

    /**
     * BufferedInputStreamWrap
     *
     * @param resolver ContentResolver
     * @param uri      data
     */
    private BufferedInputStreamWrap wrapInputStream(ContentResolver resolver, Uri uri) {
        BufferedInputStreamWrap bufferedInputStreamWrap = null;
        try {
            bufferedInputStreamWrap = new BufferedInputStreamWrap(resolver.openInputStream(uri));
            int available = bufferedInputStreamWrap.available();
            bufferedInputStreamWrap.mark(available > 0 ? available : BufferedInputStreamWrap.DEFAULT_MARK_READ_LIMIT);
            bufferedLruCache.put(uri.toString(), bufferedInputStreamWrap);
            keyCache.add(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufferedInputStreamWrap;
    }


    /**
     * 清空内存占用
     */
    public void clearMemory() {
        for (String key : keyCache) {
            BufferedInputStreamWrap inputStreamWrap = bufferedLruCache.get(key);
            PictureFileUtils.close(inputStreamWrap);
            bufferedLruCache.remove(key);
        }
        keyCache.clear();
        arrayPool.clearMemory();
    }

    private static final ArrayPoolProvide mInstance = new ArrayPoolProvide();

    public static ArrayPoolProvide getInstance() {
        return mInstance;
    }

}
