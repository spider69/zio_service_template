package com.kaspersky.analyzer.api.swagger

import com.kaspersky.analyzer.config.Config
import sttp.tapir.AnyEndpoint
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.SwaggerUIOptions
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio.macros.accessible
import zio.{IO, Task, URLayer, ZIO, ZLayer}
import com.kaspersky.analyzer.errors.Errors.Error
import com.kaspersky.build.BuildInfo

@accessible
trait SwaggerBuilder {
  def build(endpoints: List[AnyEndpoint]): IO[Error, List[ServerEndpoint[Any, Task]]]
}

case class SwaggerBuilderImpl(config: Config) extends SwaggerBuilder {
  override def build(endpoints: List[AnyEndpoint]): IO[Error, List[ServerEndpoint[Any, Task]]] =
    ZIO.succeed(
      SwaggerInterpreter(
        swaggerUIOptions = SwaggerUIOptions(
          pathPrefix = List("swagger"),
          yamlName = "api.yaml",
          contextPath = Nil,
          useRelativePaths = true
        )
      ).fromEndpoints[Task](
        endpoints = endpoints,
        title = "KTAE analyzer API",
        version = BuildInfo.version
      )
    )
}

object SwaggerBuilderImpl {
  val layer: URLayer[Config, SwaggerBuilder] = ZLayer.fromFunction(SwaggerBuilderImpl(_))
}
