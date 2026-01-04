package com.alcopoune.metertronik.domain.model

import com.google.gson.annotations.SerializedName

data class DeviceData(
     val deviceId: String,
 val deviceName: String,
 val deviceSecret: String,
val isPaired: Boolean,
)