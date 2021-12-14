package com.luck.pictureselector;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;

/**
 * @author：luck
 * @date：2020/4/30 11:32 AM
 * @describe：VideoRequestHandler
 */
public class VideoRequestHandler extends RequestHandler {
    public String SCHEME_VIDEO = "video";

    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return (SCHEME_VIDEO.equals(scheme));
    }

    @Nullable
    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        Uri uri = request.uri;
        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            Bitmap bm = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
            return new Result(bm, Picasso.LoadedFrom.DISK);
        }
        return null;
    }
}
