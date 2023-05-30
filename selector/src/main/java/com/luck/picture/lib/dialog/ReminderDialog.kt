package com.luck.picture.lib.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2021/11/17 2:33 下午
 * @describe：Prompt Class Dialog
 */
class ReminderDialog(ctx: Context, tips: String) : Dialog(ctx, R.style.Picture_Theme_Dialog) {
    init {
        setContentView(R.layout.ps_reminder_dialog)
        findViewById<TextView>(R.id.tv_content).text = tips
        findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
            dismiss()
        }
        setDialogSize()
    }

    private fun setDialogSize() {
        window?.apply {
            val params = attributes
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.CENTER
            setWindowAnimations(R.style.PictureThemeDialogWindowStyle)
            attributes = params
        }
    }

    companion object {
        fun buildDialog(context: Context, tips: String): Dialog {
            return ReminderDialog(context, tips)
        }
    }
}