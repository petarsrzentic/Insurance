package com.petarsrzentic.insurance.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.applyLinks
import com.petarsrzentic.insurance.R
import kotlinx.android.synthetic.main.activity_help.*
import java.io.File

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

//        linkSetup()
    }

//    private fun linkSetup() {
//
//        val ovdeLink = Link("OVDE!")
//            .setTextColor(Color.RED)
//            .setOnClickListener{
//                File(getExternalFilesDir("Insurance")?.absolutePath, "Insurance.xls")
//            }
//
//        textView6.applyLinks(ovdeLink)
//    }
}
