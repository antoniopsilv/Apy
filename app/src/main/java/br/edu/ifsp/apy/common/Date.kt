package br.edu.ifsp.apy.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun setDateFromMillis(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd | HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    return sdf.format(calendar.time)
}

