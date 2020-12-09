package com.petarsrzentic.insurance.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.constraintlayout.motion.widget.MotionLayout
import com.petarsrzentic.insurance.R
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.coroutines.delay

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


            val motion = findViewById<MotionLayout>(R.id.splash_screen)

            motion.addTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

                }

                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

                }

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    finish()
                }

                override fun onTransitionTrigger(
                    p0: MotionLayout?,
                    p1: Int,
                    p2: Boolean,
                    p3: Float
                ) {

                }

            })

    }
}