package com.wakemate.ai.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wakemate.ai.service.ChargingChallengeService
import com.wakemate.ai.util.BatteryUtils

class PowerConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_POWER_CONNECTED ||
            (intent.action == Intent.ACTION_BATTERY_CHANGED && BatteryUtils.isDeviceCharging(context))
        ) {
            ChargingChallengeService.onChargingDetected(context)
        }
    }
}
