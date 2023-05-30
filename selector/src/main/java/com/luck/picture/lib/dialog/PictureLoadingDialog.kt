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
        val params = window!!.attributes
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.CENTER
        window!!.setWindowAnimations(R.style.PictureThemeDialogWindowStyle)
        window!!.attributes = params
    }

    init {
        setContentView(R.layout.ps_alert_dialog)
        setCancelable(true)
        setCanceledOnTouchOutside(false)
        setDialogSize()
    }
}