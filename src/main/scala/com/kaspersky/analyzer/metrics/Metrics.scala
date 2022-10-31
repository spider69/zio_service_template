package com.kaspersky.analyzer.metrics

import com.kaspersky.analyzer.errors.Errors.{Error, MetricsError}
import com.kaspersky.build.BuildInfo
import io.prometheus.client.CollectorRegistry
import zio.macros.accessible
import zio.metrics.prometheus._
import zio.metrics.prometheus.exporters._
import zio.metrics.prometheus.helpers.{counter => prometheusCounter, gauge => prometheusGauge, getCurrentRegistry}
import zio.{IO, Runtime, Unsafe, ZLayer, _}

import java.net.InetAddress

@accessible
trait Metrics {
  def getRegistry: IO[Error, CollectorRegistry]
  def incCounter(nameTag: String): IO[Error, Unit]
  def incBatchCounter(amount: Double, nameTag: String): IO[Error, Unit]
  def setGauge(amount: Double, nameTag: String): IO[Error, Unit]
}

case class MetricsImpl(
  registry: Registry,
  exporters: Exporters
) extends Metrics {
  private val tagNames                   = Array("instance", "env", "deployNum", "name")
  private val (instance, env, deployNum) = parseHost()
  private val (counter, gauge)           = Unsafe.unsafe { implicit unsafe =>
    val rtLayer = Runtime.unsafe.fromLayer(ZLayer.succeed(registry) ++ ZLayer.succeed(exporters))
    rtLayer
      .unsafe
      .run(
        for {
          c <- prometheusCounter.register(s"${BuildInfo.name}_counter", tagNames)
          g <- prometheusGauge.register(s"${BuildInfo.name}_gauge", tagNames)
        } yield (c, g)
      )
      .getOrThrowFiberFailure()
  }

  override def getRegistry: IO[Error, CollectorRegistry] =
    getCurrentRegistry().provideLayer(Registry.live)

  override def incCounter(nameTag: String): IO[Error, Unit] =
    incBatchCounter(1.0, nameTag)

  override def incBatchCounter(amount: Double, nameTag: String): IO[Error, Unit] =
    counter.inc(amount, Array(instance, env, deployNum, nameTag)).mapError(e => MetricsError(e))

  override def setGauge(amount: Double, nameTag: String): IO[Error, Unit] =
    gauge.set(amount, Array(instance, env, deployNum, nameTag)).mapError(e => MetricsError(e))

  private def parseHost(): (String, String, String) = {
    val instance         = InetAddress.getLocalHost.getHostName
    val serviceName      = BuildInfo.name.replace('_', '-')
    val pattern          = s"$serviceName-(.*)-(\\d+)-.*".r
    val (env, deployNum) = pattern.findFirstMatchIn(instance) match {
      case Some(m) => (m.group(1), m.group(2))
      case None    => ("", "")
    }
    (instance, env, deployNum)
  }
}

object MetricsImpl {
  val layer: URLayer[Registry & Exporters, Metrics] = ZLayer.fromFunction(MetricsImpl(_, _))
}
