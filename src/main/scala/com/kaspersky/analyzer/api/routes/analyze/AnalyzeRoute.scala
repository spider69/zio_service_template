package com.kaspersky.analyzer.api.routes.analyze

import io.circe.generic.auto._
import sttp.model.Part
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir.{ZServerEndpoint, _}
import sttp.tapir.{endpoint, multipartBody, TapirFile}
import zio.macros.accessible
import zio.{UIO, ULayer, ZIO, ZLayer}

import java.util.UUID

@accessible
trait AnalyzeRoute {
  def get: UIO[ZServerEndpoint[Any, Any]]
  def post: UIO[ZServerEndpoint[Any, Any]]
}

case class File(data: Part[TapirFile])

case class AnalyzeRouteImpl() extends AnalyzeRoute {
  override def get: UIO[ZServerEndpoint[Any, Any]] =
    ZIO.succeed(
      endpoint
        .get
        .in("analyze")
        .in(
          query[String]("id")
            .description("Analysis id")
            .example("<UUID>")
        )
        .out(
          jsonBody[String]
            .description("Analysis result")
            .example("...")
        )
        .summary("Returns analysis result by id")
        .zServerLogic(id => ZIO.logInfo(id).as("Success"))
    )

  override def post: UIO[ZServerEndpoint[Any, Any]] =
    ZIO.succeed(
      endpoint
        .post
        .in("analyze")
        .in(multipartBody[File])
        .out(jsonBody[AnalyzeResponse].description("ID of posted analysis").example(AnalyzeResponse("<UUID>")))
        .summary("Start new analysis")
        .zServerLogic(f =>
          ZIO.logInfo(f.data.name) *>
            ZIO.succeed(AnalyzeResponse(UUID.randomUUID().toString))
        )
    )

}

object AnalyzeRouteImpl {
  val layer: ULayer[AnalyzeRoute] = ZLayer.succeed(AnalyzeRouteImpl())
}
