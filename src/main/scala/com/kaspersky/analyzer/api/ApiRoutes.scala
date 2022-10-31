package com.kaspersky.analyzer.api

import com.kaspersky.analyzer.api.routes.analyze.AnalyzeRoute
import com.kaspersky.analyzer.api.routes.health.HealthRoute
import com.kaspersky.analyzer.api.routes.version.VersionRoute
import com.kaspersky.analyzer.api.swagger.SwaggerBuilder
import com.kaspersky.analyzer.errors.Errors.Error
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import zio._
import zio.macros.accessible

import scala.concurrent.ExecutionContext

@accessible
trait ApiRoutes {
  def routes(): IO[Error, HttpRoutes[Task]]
}

case class Test(
  a: Int,
  b: Int
)(implicit ec: ExecutionContext)
  extends AutoCloseable with Cloneable {
  override def close() = ???
}

case class ApiRoutesImpl(
  swaggerBuilder: SwaggerBuilder,
  healthRoute: HealthRoute,
  versionRoute: VersionRoute,
  analyzeRoute: AnalyzeRoute
) extends ApiRoutes {
  override def routes(): IO[Error, HttpRoutes[Task]] =
    for {
      health       <- healthRoute.get
      version      <- versionRoute.get
      postAnalysis <- analyzeRoute.post
      getAnalysis  <- analyzeRoute.get
      routes        = List(health, version, postAnalysis, getAnalysis)
      swagger      <- swaggerBuilder.build(routes.map(_.endpoint))
      api           = ZHttp4sServerInterpreter().from(routes ++ swagger).toRoutes
    } yield api
}

object ApiRoutesImpl {
  val layer: URLayer[SwaggerBuilder & HealthRoute & VersionRoute & AnalyzeRoute, ApiRoutes] = ZLayer.fromFunction(ApiRoutesImpl(_, _, _, _))
}
