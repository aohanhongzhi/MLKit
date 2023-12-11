/*
 * Copyright (C) Jenly, MLKit Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.king.mlkit.vision.app.barcode

import android.widget.ImageView
import com.google.mlkit.vision.barcode.common.Barcode
import com.king.app.dialog.AppDialog
import com.king.app.dialog.AppDialogConfig
import com.king.camera.scan.AnalyzeResult
import com.king.camera.scan.CameraScan
import com.king.mlkit.vision.app.R
import com.king.mlkit.vision.app.drawRect
import com.king.mlkit.vision.barcode.BarcodeCameraScanActivity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 条形码/二维码扫描示例
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class BarcodeScanningActivity : BarcodeCameraScanActivity() {

    val log: Logger = LoggerFactory.getLogger(BarcodeScanningActivity::class.java)

    override fun initCameraScan(cameraScan: CameraScan<MutableList<Barcode>>) {
        super.initCameraScan(cameraScan)
        cameraScan.setPlayBeep(true)
            .setVibrate(true)
    }

    override fun onScanResultCallback(result: AnalyzeResult<MutableList<Barcode>>) {

        cameraScan.setAnalyzeImage(false)
        val buffer = StringBuilder()
        val bitmap = result.bitmap?.drawRect { canvas, paint ->
            for ((index, data) in result.result.withIndex()) {
                buffer.append("[$index] ").append(data.displayValue).append("\n")
                log.info("扫描结果： [$index] ${data.displayValue}")
                // TODO 遍历条形码或者二维码，只要符合要求就可以直接自动提交了。
                data.boundingBox?.let { box ->
                    canvas.drawRect(box, paint)
                }
            }
        }

        val config = AppDialogConfig(this, R.layout.barcode_result_dialog)
        config.setContent(buffer).setOnClickConfirm {
            AppDialog.INSTANCE.dismissDialog()
            cameraScan.setAnalyzeImage(true)
        }.setOnClickCancel {
            AppDialog.INSTANCE.dismissDialog()
            finish()
        }
        val imageView = config.getView<ImageView>(R.id.ivDialogContent)
        imageView.setImageBitmap(bitmap)
        AppDialog.INSTANCE.showDialog(config, false)
    }


}