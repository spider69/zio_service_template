package com.kaspersky.analyzer.api.routes.version

import com.kaspersky.build.BuildInfo
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir.{ZServerEndpoint, _}
import zio.macros.accessible
import zio.{UIO, ULayer, ZIO, ZLayer}

@accessible
trait VersionRoute {
  def get: UIO[ZServerEndpoint[Any, Any]]
}

case class VersionRouteImpl() extends VersionRoute {

  override def get: UIO[ZServerEndpoint[Any, Any]] =
    ZIO.succeed(
      endpoint
        .get
        .in("version")
        .out(jsonBody[String].description("Info about service version"))
        .summary("Returns information about service")
        .zServerLogic(_ => logic)
    )

  private def logic: UIO[String] = {
    ZIO.logDebug("Version requested").as {
      BuildInfo.toString
    }
  }
}

object VersionRouteImpl {
  val layer: ULayer[VersionRoute] = ZLayer.fromFunction(() => VersionRouteImpl())
}
