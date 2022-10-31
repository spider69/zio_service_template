package com.kaspersky.analyzer.metrics

import com.kaspersky.analyzer.config.Config
import com.kaspersky.analyzer.errors.Errors.{Error, MetricsError}
import zio.macros.accessible
import zio.metrics.prometheus._
import zio.metrics.prometheus.exporters._
import zio.metrics.prometheus.helpers._
import zio._

import java.net.InetAddress

@accessible
trait MetricsExporter {
  def run: IO[Error, Unit]
}

case class MetricsExporterImpl(
  config: Config,
  registry: Registry,
  exporters: Exporters
) extends MetricsExporter {
  private val jobName = InetAddress.getLocalHost.getHostName

  override def run: IO[Error, Unit] = {
    val pushGatewayInit = for {
      cfg <- ZIO.succeed(config.metrics)
      r   <- getCurrentRegistry()
      _   <- initializeDefaultExports(r).mapError(e => MetricsError(e))
      _   <-
        (ZIO.logTrace(s"Push metrics to ${cfg.pushgatewayUrl}:${cfg.pushgatewayPort}") *>
          pushGateway(r, cfg.pushgatewayUrl, cfg.pushgatewayPort, jobName))
          .tapError(e => ZIO.logError(e.toString))
          .repeat(Schedule.spaced(cfg.getPushInterval))
          .retry(Schedule.spaced(cfg.getPushInterval))
          .mapError(e => MetricsError(e))
    } yield ()

    pushGatewayInit
      .provide(ZLayer.succeed(registry), ZLayer.succeed(exporters))
  }
}

object MetricsExporterImpl {
  val layer: URLayer[Config & Registry & Exporters, MetricsExporter] =
    ZLayer.fromFunction(MetricsExporterImpl(_, _, _))
}
