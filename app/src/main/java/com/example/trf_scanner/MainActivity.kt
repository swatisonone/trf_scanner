package com.example.trf_scanner

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

//    private var scan:Button? =null
    private var textView:TextView?=null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var scan = findViewById<Button>(R.id.button)
        textView =findViewById(R.id.textView)

        scan?.setOnClickListener(
            object : OnClickListener {
                override fun onClick(p0: View?) {
                    var intent = Intent(this@MainActivity, ScanCodeActivity::class.java)
                    startActivity(intent)
                }

            } )
        }
    }
