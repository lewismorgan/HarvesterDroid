package com.lewisjmorgan.harvesterdroid.api

import java.net.URI
import java.net.URL

class UrlBuilder {
  private val self = this
  private val segments = ArrayList<String>()
  private val params = HashMap<String, Any>()
  private var connectionType: String = "http"
  private var host: String = ""

  constructor(host: String, connectionType: String) {
    this.host = host
    this.connectionType = connectionType
  }

  constructor(host: String): this(host, "http")

  fun append(segment: String): UrlBuilder {
    segments.add(segment)
    return self
  }

  @Suppress("unused")
  fun param(parameter: String, value: Any): UrlBuilder {
    params[parameter] = value.toString()
    return self
  }

  private fun buildSegments(builder: StringBuilder): StringBuilder {
    segments.forEach {
      builder.append("/$it")
    }
    return builder
  }

  private fun buildQuery(builder: StringBuilder): StringBuilder {
    params.forEach {
      builder.append(it.key)
      builder.append("=")
      builder.append(it.value)
    }
    return builder
  }

  fun build(): URL {
    val path = buildSegments(StringBuilder())
    val query = buildQuery(StringBuilder())

    return URI(connectionType, null, path.toString(), query.toString(), null).toURL()
  }

  @Suppress("unused")
  fun buildRelativeUrl(): URL {
    val path = buildSegments(StringBuilder())
    val query = buildQuery(StringBuilder())

    return URI(null, null, path.toString(), query.toString(), null).toURL()
  }
}