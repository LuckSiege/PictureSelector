package com.luck.picture.lib;

import android.content.Context;
import android.net.Uri;

import com.luck.picture.lib.io.ArrayPoolProvide;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author：luck
 * @date：2021/5/26 9:22 PM
 * @describe：PictureContentResolver
 */
public final class PictureContentResolver {

    /**
     * ContentResolver openInputStream
     *
     * @param context
     * @param uri
     * @return
     */
    public static InputStream getContentResolverOpenInputStream(Context context, Uri uri) {
        return ArrayPoolProvide.getInstance().openInputStream(context.getContentResolver(), uri);
    }

    /**
     * ContentResolver OutputStream
     *
     * @param context
     * @param uri
     * @return
     */
    public static OutputStream getContentResolverOpenOutputStream(Context context, Uri uri) {
        try {
            return context.getContentResolver().openOutputStream(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
