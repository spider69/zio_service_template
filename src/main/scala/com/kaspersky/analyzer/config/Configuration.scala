package com.kaspersky.analyzer.config

import com.kaspersky.analyzer.errors.Errors.{ConfigError, Error}
import zio.config._
import zio.config.magnolia.descriptor
import zio.config.typesafe.TypesafeConfigSource
import zio.{Layer, ZLayer}

object Configuration {
  val layer: Layer[Error, Config] =
    ZLayer {
      read {
        descriptor[Config].from(
          TypesafeConfigSource.fromResourcePath
        )
      }
    }.mapError(e => ConfigError(e))
}
