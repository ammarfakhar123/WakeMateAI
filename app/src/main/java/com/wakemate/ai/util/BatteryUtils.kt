package com.wakemate.ai.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build

object BatteryUtils {

    /**
     * Checks if the device is currently connected to power/charging.
     */
    fun isDeviceCharging(context: Context): Boolean {
        return try {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && batteryManager != null) {
                if (batteryManager.isCharging) return true
            }

            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, filter)
            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Gets current battery level percentage (0 to 100).
     */
    fun getBatteryPercentage(context: Context): Int {
        return try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, filter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level >= 0 && scale > 0) {
                ((level / scale.toFloat()) * 100).toInt()
            } else {
                50
            }
        } catch (e: Exception) {
            50
        }
    }

    /**
     * Gets charging plug type description (AC, USB, Wireless).
     */
    fun getChargingPlugType(context: Context): String {
        return try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, filter)
            when (batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                BatteryManager.BATTERY_PLUGGED_AC -> "AC Wall Charger"
                BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Pad"
                else -> "Not Charging"
            }
        } catch (e: Exception) {
            "Not Charging"
        }
    }
}
