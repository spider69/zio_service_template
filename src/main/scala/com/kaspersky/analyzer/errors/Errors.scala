package com.kaspersky.analyzer.errors

object Errors {
  sealed trait Error

  case class ConfigError(e: Throwable)     extends Error
  case class MetricsError(e: Throwable)    extends Error
  case class HttpServerError(e: Throwable) extends Error
}
