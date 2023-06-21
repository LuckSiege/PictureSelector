package com.luck.picture.lib.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：loading dialog
 */
class PictureLoadingDialog(context: Context) : Dialog(context, R.style.Picture_Theme_AlertDialog) {

    private fun setDialogSize() {
        window?.attributes?.apply {
            this.width = ViewGroup.LayoutParams.WRAP_CONTENT
            this.height = ViewGroup.LayoutParams.WRAP_CONTENT
            this.gravity = Gravity.CENTER
            window?.setWindowAnimations(R.style.PictureThemeDialogWindowStyle)
            window?.attributes = this
        }
    }

    init {
        setContentView(R.layout.ps_alert_dialog)
        setCancelable(true)
        setCanceledOnTouchOutside(false)
        setDialogSize()
    }
}