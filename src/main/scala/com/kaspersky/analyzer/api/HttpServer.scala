package com.kaspersky.analyzer.api

import com.kaspersky.analyzer.config.Config
import com.kaspersky.analyzer.errors.Errors.{Error, HttpServerError}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import zio.interop.catz._
import zio.macros.accessible
import zio.{IO, Task, URLayer, ZIO, ZLayer}
import zio._

@accessible
trait HttpServer {
  def start(): IO[Error, Unit]
}

case class HttpServerImpl(config: Config, apiRoutes: ApiRoutes) extends HttpServer {
  override def start(): IO[Error, Unit] =
    for {
      _   <- ZIO.logInfo("Starting http server...")
      api <- apiRoutes.routes()
      _   <- ZIO
               .executor
               .flatMap { executor =>
                 BlazeServerBuilder[Task]
                   .withExecutionContext(executor.asExecutionContext)
                   .bindHttp(config.api.port, config.api.host)
                   .withHttpApp(
                     Router[Task](
                       "" -> api
                     ).orNotFound
                   )
                   .serve
                   .compile
                   .drain
               }
               .mapError(e => HttpServerError(e))
    } yield ()
}

object HttpServerImpl {
  val layer: URLayer[Config & ApiRoutes, HttpServer] = ZLayer.fromFunction(HttpServerImpl(_, _))
}
