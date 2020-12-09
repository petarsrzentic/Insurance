package com.petarsrzentic.insurance.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.petarsrzentic.insurance.R
import com.petarsrzentic.insurance.util.sendNotification
import kotlin.random.Random
import kotlin.random.nextInt

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val info = Random.nextInt(1..6)
        var textFromNotification = ""
        when(info) {
            1 -> textFromNotification = context.getString(R.string.nismo_se_videli)
            2 -> textFromNotification = context.getString(R.string.popravka_u_neovlascenom_servisu)
            3 -> textFromNotification = context.getString(R.string.popravka_nije_uvek_moguca)
            4 -> textFromNotification = context.getString(R.string.lom_telefona_u_prvoj_godini)
            5 -> textFromNotification = context.getString(R.string.neovlasceni_servis_rizican)
            6 -> textFromNotification = context.getString(R.string.vip_prvi_uveo_osiguranje)
        }

        sendNotification(
            textFromNotification,
            context
        )

        Log.d("TAG", "$info , $textFromNotification")
    }

}