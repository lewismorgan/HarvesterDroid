package com.lewisjmorgan.harvesterdroid.galaxyharvester

import java.text.SimpleDateFormat
import java.util.*

fun String.dateFromTimestamp(): Date {
  val dateFormat = SimpleDateFormat("yyyy-MM-DD HH:mm:ss")
  return dateFormat.parse(this)
}

fun String.dateFromString(): Date {
  val dateFormat = SimpleDateFormat("E, dd MMMM yyyy HH:mm:ss Z")
  return dateFormat.parse(this)
}