package com.github.cesar1287.iot20mob

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Cyclist(
    val action: String? = null,
    val timestamp: String? = null,
    val timestampDate: Date? = null
): Parcelable
