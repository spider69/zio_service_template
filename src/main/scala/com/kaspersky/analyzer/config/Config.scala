package com.kaspersky.analyzer.config

import scala.concurrent.duration.{Duration, DurationInt}

case class ApiConfig(
  protocol: String = "http",
  host: String = "localhost",
  port: Int = 8080
)

case class MetricsConfig(
  pushgatewayUrl: String = "",
  pushgatewayPort: Int = 9091,
  pushgatewayPushInterval: Duration = 30 seconds
) {
  def getPushInterval: zio.Duration = zio.Duration.fromScala(pushgatewayPushInterval)
}

case class Config(
  api: ApiConfig,
  metrics: MetricsConfig
)
