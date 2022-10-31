package com.kaspersky.analyzer

import com.kaspersky.analyzer.api.routes.analyze.AnalyzeRouteImpl
import com.kaspersky.analyzer.api.routes.health.{HealthCheckerImpl, HealthRouteImpl}
import com.kaspersky.analyzer.api.routes.version.VersionRouteImpl
import com.kaspersky.analyzer.api.swagger.SwaggerBuilderImpl
import com.kaspersky.analyzer.api.{ApiRoutesImpl, HttpServer, HttpServerImpl}
import com.kaspersky.analyzer.config.Configuration
import zio.logging.backend.SLF4J
import zio.{ULayer, ZIOAppDefault}

object Main extends ZIOAppDefault {
  override val bootstrap: ULayer[Unit] =
    zio.Runtime.removeDefaultLoggers ++ SLF4J.slf4j

  def run =
    HttpServer
      .start()
      .provide(
        // config
        Configuration.layer,
        // metrics
//        Registry.live,
//        Exporters.live,
//        MetricsImpl.layer,
//        MetricsExporterImpl.layer,
        // api
        SwaggerBuilderImpl.layer,
        HealthCheckerImpl.layer,
        HealthRouteImpl.layer,
        VersionRouteImpl.layer,
        AnalyzeRouteImpl.layer,
        ApiRoutesImpl.layer,
        HttpServerImpl.layer
      )
}
