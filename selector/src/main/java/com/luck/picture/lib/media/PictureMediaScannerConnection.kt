package com.luck.picture.lib.media

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.text.TextUtils

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：Refresh System Album
 */
class PictureMediaScannerConnection(context: Context, path: String?, l: ScanListener?) :
    MediaScannerConnection.MediaScannerConnectionClient {

    private var mPath: String? = null
    private var mListener: ScanListener? = null
    private var mMs: MediaScannerConnection? = null

    init {
        this.mPath = path
        this.mListener = l
        if (path == null || TextUtils.isEmpty(path)) {
            mListener?.onScanFinish()
        } else {
            this.mMs = MediaScannerConnection(context.applicationContext, this)
            this.mMs?.connect()
        }
    }

    override fun onScanCompleted(path: String?, uri: Uri?) {
        mMs?.disconnect()
        mListener?.onScanFinish()
    }

    override fun onMediaScannerConnected() {
        if (!TextUtils.isEmpty(mPath)) {
            mMs?.scanFile(mPath, null)
        }
    }
}