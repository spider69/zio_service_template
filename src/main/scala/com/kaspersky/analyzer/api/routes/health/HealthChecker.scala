package com.kaspersky.analyzer.api.routes.health

import zio.macros.accessible
import zio.{IO, ULayer, URLayer, ZIO, ZLayer}

@accessible
trait HealthChecker {
  def health: IO[Unit, Unit]
}

case class HealthCheckerImpl() extends HealthChecker {
  override def health: IO[Unit, Unit] =
    ZIO.logTrace("Health check requested")
}

object HealthCheckerImpl {
  val layer: ULayer[HealthChecker] = ZLayer.fromFunction(() => HealthCheckerImpl())
}
