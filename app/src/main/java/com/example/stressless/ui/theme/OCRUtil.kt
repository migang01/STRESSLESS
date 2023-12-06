package com.example.stressless.ui.theme

import android.graphics.Bitmap
import android.os.Environment
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream

class OCRUtil {
    private val tessDataPath: String = "/path/to/tessdata"  // 실제 경로로 변경하세요.

    fun performOCR(bitmap: Bitmap): String {
        val tessBaseAPI = TessBaseAPI()

        // traineddata 파일의 경로를 직접 지정하고 OCR 엔진 모드를 설정합니다.
        tessBaseAPI.init(tessDataPath, "kor", TessBaseAPI.OEM_TESSERACT_ONLY)

        // OCR을 수행하고 결과를 반환합니다.
        tessBaseAPI.setImage(bitmap)
        val result: String = tessBaseAPI.utF8Text

        // OCR 작업 종료
        tessBaseAPI.end()

        return result
    }
}