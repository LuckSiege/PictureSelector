package com.luck.picture.lib.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2021/11/19 5:11 下午
 * @describe：PictureCommonDialog
 */
class PictureCommonDialog(context: Context, title: String, content: String) :
    Dialog(context, R.style.Picture_Theme_Dialog), View.OnClickListener {

    private fun setDialogSize() {
        window?.attributes?.let { params ->
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.CENTER
            window!!.setWindowAnimations(R.style.PictureThemeDialogWindowStyle)
            window!!.attributes = params
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.btn_cancel) {
            dismiss()
        } else if (id == R.id.btn_commit) {
            dismiss()
            if (eventListener != null) {
                eventListener!!.onConfirm()
            }
        }
    }

    /**
     * 对外暴露的点击事件
     *
     * @param eventListener
     */
    fun setOnDialogEventListener(eventListener: OnDialogEventListener?) {
        this.eventListener = eventListener
    }

    private var eventListener: OnDialogEventListener? = null

    interface OnDialogEventListener {
        fun onConfirm()
    }

    companion object {
        fun showDialog(context: Context, title: String, content: String): PictureCommonDialog {
            val dialog = PictureCommonDialog(context, title, content)
            dialog.show()
            return dialog
        }
    }

    init {
        setContentView(R.layout.ps_common_dialog)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        val btnCommit = findViewById<Button>(R.id.btn_commit)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvContent = findViewById<TextView>(R.id.tv_content)
        tvTitle.text = title
        tvContent.text = content
        btnCancel.setOnClickListener(this)
        btnCommit.setOnClickListener(this)
        setDialogSize()
    }
}