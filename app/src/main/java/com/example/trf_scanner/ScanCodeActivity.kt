package com.example.trf_scanner

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection
import kotlin.text.String as String1


class ScanCodeActivity : AppCompatActivity() {
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var detector :BarcodeDetector

    var name: String? = null
    var prn: String? = null
    var pre_str:String?=null
//    lateinit var pre_str: java.util.ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_code)

        if (ContextCompat.checkSelfPermission(
                this,android.Manifest.permission.CAMERA
        )!=PackageManager.PERMISSION_GRANTED){
            askForCameraPermission()
        }else{
            setupControl()
        }
    }
    private  fun setupControl(){
        var scannerView = findViewById<SurfaceView>(R.id.scannerView)
        detector = BarcodeDetector.Builder(this@ScanCodeActivity).build()
        cameraSource = CameraSource.Builder(this@ScanCodeActivity,detector).setAutoFocusEnabled(true).build()
        scannerView.holder.addCallback(surfaceCallback)
        detector.setProcessor(processor)
    }
    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@ScanCodeActivity,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )}

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == requestCodeCameraPermission && grantResults.isEmpty()) {
                setupControl()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

private val surfaceCallback = object :SurfaceHolder.Callback {

    override fun surfaceCreated(p0: SurfaceHolder) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this@ScanCodeActivity,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                askForCameraPermission()
            }else{
                setupControl()
            }
            cameraSource.start(p0)

        } catch (exeption: java.lang.Exception) {
            Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()

        }
    }
    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        cameraSource.stop()
    }



}
private val processor = object :Detector.Processor<Barcode>{
    override fun release() {

    }

    override fun receiveDetections(p0: Detector.Detections<Barcode>) {

        var textView = findViewById<TextView>(R.id.textView)
        if (p0 != null && p0.detectedItems.isNotEmpty()) {
            val qrCodes: SparseArray<Barcode> = p0.detectedItems
            val code = qrCodes.valueAt(0)
            textView.text = code.displayValue
            val str = textView.text
            val str1 = str.split("-")
            name = textView.text as String
            prn = str1[1]
            Log.d("name", "$name ")
            Log.d("prn", "$prn ")
            if (pre_str == prn) {
            } else {
                SendRequest().execute()
                pre_str = prn.toString()
            }
        }
//            var flag = 0
//            for (i in pre_str){
//            if (pre_str == str){
//                flag =1
//                break
//        }else {
////                SendRequest().execute()
////                pre_str = prn
//                flag = 0
//                continue
//            }
//        }
//            if (flag == 1){
//
//            }else{
//                SendRequest().execute()
//            pre_str.plusElement(str as String)
//            }
//        }
        else{
            textView.text = ""
        }
    }


}
    inner class SendRequest : AsyncTask<String?, Void?, String>() {
        override fun onPreExecute() {}
         override fun doInBackground(vararg params: String?): String? {
            return try {
                val url =
                    URL("https://script.google.com/macros/s/AKfycbzSO9YJnYPWdIYkN2w2f8ZDcskFLviAzJrEs-Y2IGW7qhvptQ_IOl7N8PqumKx-dgo/exec")
                val postDataParams = JSONObject()

                //int i;
                //for(i=1;i<=70;i++)


                //    String usn = Integer.toString(i);
                val id = "1XquZ-0H6akkujhJutO9RYJmvD_hFM_-klh9IYNNTMWQ"
                postDataParams.put("name", name)
                postDataParams.put("PRN", prn)
                postDataParams.put("id", id)
                Log.e("params", postDataParams.toString())
                val conn = url.openConnection() as HttpURLConnection
                conn.readTimeout = 15000
                conn.connectTimeout = 15000
                conn.requestMethod = "POST"
                conn.doInput = true
                conn.doOutput = true
                val os = conn.outputStream
                val writer = BufferedWriter(
                    OutputStreamWriter(os, "UTF-8")
                )
                writer.write(getPostDataString(postDataParams))
                writer.flush()
                writer.close()
                os.close()
                val responseCode = conn.responseCode
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    val `in` = BufferedReader(InputStreamReader(conn.inputStream))
                    val sb = StringBuffer("")
                    var line: String? = ""
                    while (`in`.readLine().also { line = it } != null) {
                        sb.append(line)
                        break
                    }
                    `in`.close()
                    sb.toString()
                } else {
                    Toast.makeText(this@ScanCodeActivity,"Exception: " ,Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ScanCodeActivity,"Exception: ",Toast.LENGTH_SHORT).show()
            }.toString()
        }

        override fun onPostExecute(result: String) {
            Toast.makeText(
                applicationContext, "Attendence Succefully Added",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @Throws(Exception::class)
    fun getPostDataString(params: JSONObject): String {

        val result = StringBuilder()
        var first = true
        val itr = params.keys()
        while (itr.hasNext()) {
            val key = itr.next()
            val value = params[key]
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }
//        Toast.makeText(this,"Attendence Succefully Added",Toast.LENGTH_SHORT)
        return result.toString()

    }

//    fun append(arr: Array<Int>, element: Int): Array<Int> {
//        val list: MutableList<Int> = arr.toMutableList()
//        list.add(element)
//        return list.toTypedArray()
//    }
}


