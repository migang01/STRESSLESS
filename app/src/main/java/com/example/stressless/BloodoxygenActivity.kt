package com.example.stressless

import android.content.Context
import android.widget.Toast
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.content.ContentResolver
import android.os.Bundle
import android.widget.Button

import android.widget.ImageView
import android.widget.TextView

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.stressless.ui.theme.STRESSLESSTheme
import com.googlecode.tesseract.android.TessBaseAPI

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.FileNotFoundException

class BloodoxygenActivity : ComponentActivity() {

    private lateinit var bitmap: Bitmap
    private var mTess: TessBaseAPI? = null // Tess API refernece
    var datapath = "" // 언어데이터가 있는 경로

    var OCRTextView: TextView? = null // OCR 결과
//    var textView: TextView? = null // 시인 이름
    var dateTextView: TextView? = null
    var timeTextView: TextView? = null
    var bloodoxygenTextView: TextView? = null
    /** 이전 메뉴로 가기 **/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            STRESSLESSTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
        setContentView(R.layout.activity_bloodoxygen)
//        OCRTextView = findViewById(R.id.OCRTextView)
        dateTextView = findViewById(R.id.date)
        timeTextView = findViewById(R.id.time)
        bloodoxygenTextView = findViewById(R.id.bloodoxygen)

        /**이전 버튼**/
        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        /**OCR **/
        // 언어파일 경로
        datapath = "$filesDir/tesseract/"
        // 트레이닝데이터가 카피되어 있는지 체크
        checkFile(File(datapath + "tessdata/"))
        // Tesseract API 언어 세팅
        var lang = "kor"
        //OCR세팅
        mTess = TessBaseAPI()
        mTess!!.init(datapath, lang)


        // 액티비티에 전달된 Intent를 얻어옴
        val intent: Intent = intent
        // Intent의 action과 type을 가져옴
        val action: String? = intent.action
        val type: String? = intent.type

        // Intent action이 SEND이고 type이 null이 아닌 경우
        if (Intent.ACTION_SEND == action && type != null) {
            // Intent type이 "image/"로 시작하는 경우
            if (type.startsWith("image/")) {
                // 이미지를 공유받은 경우

                // Intent에서 이미지 Uri 가져오기
                val imageUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                // 이미지 Uri가 null이 아닌 경우
                if (imageUri != null) {
                    // ContentResolver를 사용하여 Uri에서 Bitmap으로 변환
                    val resolver: ContentResolver = contentResolver
                    try {
                        // ContentResolver를 사용해 Uri에서 Bitmap으로 반환
                        bitmap = BitmapFactory.decodeStream(resolver.openInputStream(imageUri))

                        // 이미지 잘 전달받았는지 이미지 뷰로 확인
                        val imageView: ImageView = findViewById(R.id.imageView)
                        imageView.setImageBitmap(bitmap)

                        // 이미지에서 텍스트 추출 및 TextView에 설정
                        processImage()



                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }


    // OCR 결과에서 "윤동주"를 찾아서 반환하는 함수
//    fun findWriter(ocrResult: String?): String {
//        // OCR 결과가 null이거나 빈 문자열인 경우 기본값으로 빈 문자열 반환
//        if (ocrResult.isNullOrEmpty()) {
//            return ""
//        }
//
//        // OCR 결과에서 "윤동주"를 찾기
//        val writerKeyword = "윤동주"
//        val index = ocrResult.indexOf(writerKeyword)
//
//        // "윤동주"를 찾은 경우 해당 부분을 추출하여 반환
//        return if (index != -1) {
//            ocrResult.substring(index, index + writerKeyword.length)
//        } else {
//            // "윤동주"를 찾지 못한 경우 기본값으로 빈 문자열 반환
//            ""
//        }
//    }

    /**날짜 찾기**/
    fun extractMonthAndDay(ocrResult: String): Pair<String?, String?> {
        // 정규 표현식을 사용하여 "00월 00일" 또는 "00월 00알" 패턴을 찾음
        val dateRegex = Regex("(\\d{1,2})월 (\\d{1,2})(일|알)")
        val matchResult = dateRegex.find(ocrResult)

        // 패턴을 찾은 경우 해당 그룹의 값을 month와 day에 저장
        return if (matchResult != null && matchResult.groupValues.size >= 4) {
            val month = matchResult.groupValues[1].takeIf { it.isNotEmpty() }
            val day = matchResult.groupValues[2].takeIf { it.isNotEmpty() }
            Pair(month, day)
        } else {
            // 패턴을 찾지 못한 경우 빈 문자열 반환
            Pair(null, null)
        }
    }

    /**시간 찾기 **/
    fun parseTime(ocrResult: String): String {
        val timeRegex = Regex("(오전|오후) (\\d{1,2}):(\\d{1,2})")
        val matchResult = timeRegex.find(ocrResult)

        return if (matchResult != null && matchResult.groupValues.size == 4) {
            val period = matchResult.groupValues[1]
            val hour = matchResult.groupValues[2].toInt()
            val minute = matchResult.groupValues[3]

            val formattedHour = when {
                period == "오전" && hour == 12 -> "00"
                period == "오후" && hour in 1..11 -> (hour + 12).toString()
                else -> hour.toString().padStart(2, '0')
            }

            formattedHour + minute
        } else {
            // 패턴을 찾지 못한 경우 빈 문자열 반환
            ""
        }
    }
    /**혈중산소농도 찾기**/
    fun extractPercentage(text: String): String? {
        val percentageRegex = Regex("(\\d{2}) %")
        val matchResults = percentageRegex.find(text)

        return matchResults?.groupValues?.get(1)
    }

    /**이미지에서 텍스트 읽기**/
    fun processImage() {
        var OCRresult: String? = null
        mTess!!.setImage(bitmap)
        OCRresult = mTess!!.utF8Text
        OCRTextView!!.text = OCRresult
//        윤동주 테스트
//        var writer: String = findWriter(OCRresult)
//        textView!!.text = writer
       /**날짜 찾기**/
        var (month, day) = extractMonthAndDay(OCRresult)
        if(month != null && day != null){
            // 하나의 스트링으로 합치기
            var date = "$month$day"
            dateTextView!!.text = "$month 월 $day 일" // 테스트용, 실제론 구글 시트에 저장
            /**********************************구글 시트 저장*************************************/
        }
        /**시간 찾기**/
        var time = parseTime(OCRresult)
        if(time != null){
            val hour = time.substring(0, 2).toIntOrNull() ?: 0
            val minute = time.substring(2).toIntOrNull() ?: 0
            timeTextView!!.text = "$hour 시 $minute 분" // 테스트용, 실제론 구글 시트에 저장
            /**********************************구글 시트 저장*************************************/
        }        
        /**혈중산소농도 찾기**/
        var bloodOxygen = extractPercentage(OCRresult)
        if(bloodOxygen != null){
            bloodoxygenTextView!!.text = "$bloodOxygen%"// 테스트용, 실제론 구글 시트에 저장
            /**********************************구글 시트 저장*************************************/
        }

        
        /**데이터 저장**/
        if((month == null && day == null) || time == null || bloodOxygen == null){
            val errorMessage = "올바르지 않은 형식입니다"
            showToast(this, errorMessage)
        }
        else if((month != null && day != null) && time != null && bloodOxygen != null){
            /************************ if -> 모든 데이터가 성공적으로 저장됐으면 ************************/
            val errorMessage = "데이터 저장에 성공했습니다"
            showToast(this, errorMessage)
        }
    }

    /***
     * 언어 데이터 파일, 디바이스에 복사
     */

    // 언어 파일 이름
    private val langFileName = "kor.traineddata"
    private fun copyFiles() {
        try {
            val filepath = datapath + "tessdata/" + langFileName
            val assetManager = assets
            val instream: InputStream = assetManager.open(langFileName)
            val outstream: OutputStream = FileOutputStream(filepath)
            val buffer = ByteArray(1024)
            var read: Int
            while (instream.read(buffer).also { read = it } != -1) {
                outstream.write(buffer, 0, read)
            }
            outstream.flush()
            outstream.close()
            instream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /***
     * 디바이스에 언어 데이터 파일 존재 유무 체크
     * @param dir
     */
    private fun checkFile(dir: File) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles()
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if (dir.exists()) {
            val datafilepath = datapath + "tessdata/" + langFileName
            val datafile = File(datafilepath)
            if (!datafile.exists()) {
                copyFiles()
            }
        }
    }




    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        STRESSLESSTheme {
            Greeting("Android")
        }
    }
}