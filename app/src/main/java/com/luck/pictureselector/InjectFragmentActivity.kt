package com.luck.pictureselector

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.model.PictureSelector
import com.luck.picture.lib.utils.SelectorLogUtils

class InjectFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inject_fragment)
        val tvResult = findViewById<TextView>(R.id.tv_result)
        findViewById<Button>(R.id.btn_camera).setOnClickListener {
            PictureSelector.create(this)
                .openCamera(MediaType.ALL)
                .buildLaunch(R.id.container_fragment, object : OnResultCallbackListener {
                    override fun onResult(result: List<LocalMedia>) {
                        val stringBuilder = StringBuilder()
                        result.forEach { media ->
                            stringBuilder.append(media.path).append("\n")
                        }
                        tvResult.text = stringBuilder
                    }

                    override fun onCancel() {
                        SelectorLogUtils.info("onCancel")
                    }
                })
        }
        findViewById<Button>(R.id.btn_photo).setOnClickListener {
            PictureSelector.create(this)
                .openGallery(MediaType.ALL)
                .setImageEngine(GlideEngine.create())
                .buildLaunch(R.id.container_fragment, object : OnResultCallbackListener {
                    override fun onResult(result: List<LocalMedia>) {
                        val stringBuilder = StringBuilder()
                        result.forEach { media ->
                            stringBuilder.append(media.path).append("\n")
                        }
                        tvResult.text = stringBuilder
                    }

                    override fun onCancel() {
                        SelectorLogUtils.info("onCancel")
                    }

                })
        }
    }
}