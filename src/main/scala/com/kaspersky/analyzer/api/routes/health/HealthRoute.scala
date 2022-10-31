package com.kaspersky.analyzer.api.routes.health

import sttp.model.StatusCode
import sttp.tapir.ztapir._
import zio.macros.accessible
import zio.{UIO, URLayer, ZIO, ZLayer}

@accessible
trait HealthRoute {
  def get: UIO[ZServerEndpoint[Any, Any]]
}

case class HealthRouteImpl(
  healthChecker: HealthChecker
) extends HealthRoute {

  override def get: UIO[ZServerEndpoint[Any, Any]] =
    ZIO.succeed(
      endpoint
        .get
        .in("health")
        .errorOut(
          statusCode(StatusCode.InternalServerError)
            .description("ERROR. Server is not healthy")
        )
        .summary("Does health checking of service")
        .zServerLogic(_ => healthChecker.health)
    )
}

object HealthRouteImpl {
  val layer: URLayer[HealthChecker, HealthRoute] = ZLayer.fromFunction(HealthRouteImpl(_))
}
