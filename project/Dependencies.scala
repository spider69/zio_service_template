import sbt._

object Dependencies {
  object Versions {
    // logging
    lazy val ZioLoggingVersion             = "2.1.3"
    lazy val LogbackVersion                = "1.4.4"
    lazy val LogbackLogstashEncoderVersion = "7.2"

    // zio
    lazy val ZioVersion       = "2.0.2"
    lazy val ZioConfigVersion = "3.0.2"

    // test
    lazy val ZioMockVersion = "1.0.0-RC9"
    lazy val MockitoVersion = "1.17.12"

    // metrics
    lazy val ZioMetricsVersion = "2.0.0"

    // http
    lazy val ZHttpVersion       = "2.0.0-RC11"
    lazy val TapirVersion       = "1.1.3"
    lazy val Http4sBlazeVersion = "0.23.12"

    // database
    lazy val ZioQuillVersion       = "4.6.0"
    lazy val FlywayVersion         = "9.6.0"
    lazy val MySqlConnectorVersion = "8.0.31"

    lazy val CodecsVersion = "1.15"
  }

  import Versions._

  lazy val Logging = Seq(
    "dev.zio"             %% "zio-logging-slf4j"        % ZioLoggingVersion,
    "ch.qos.logback"       % "logback-classic"          % LogbackVersion,
    "net.logstash.logback" % "logstash-logback-encoder" % LogbackLogstashEncoderVersion
  )

  lazy val ZioMetrics = Seq(
    "dev.zio" %% "zio-metrics-prometheus" % ZioMetricsVersion
  )

  lazy val Zio = Seq(
    "dev.zio" %% "zio"         % ZioVersion,
    "dev.zio" %% "zio-streams" % ZioVersion,
    "dev.zio" %% "zio-macros"  % ZioVersion
  )

  lazy val ZioTest = Seq(
    "dev.zio"     %% "zio-test"          % ZioVersion     % "test",
    "dev.zio"     %% "zio-test-sbt"      % ZioVersion     % "test",
    "dev.zio"     %% "zio-test-magnolia" % ZioVersion     % "test",
    "dev.zio"     %% "zio-mock"          % ZioMockVersion % "test",
    "org.mockito" %% "mockito-scala"     % MockitoVersion % Test
  )

  lazy val ZioConfig = Seq(
    "dev.zio" %% "zio-config"          % ZioConfigVersion,
    "dev.zio" %% "zio-config-magnolia" % ZioConfigVersion,
    "dev.zio" %% "zio-config-typesafe" % ZioConfigVersion
  )

  lazy val Tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % TapirVersion
  )

  lazy val ZioHttp = Seq(
    "io.d11"     %% "zhttp"               % ZHttpVersion,
    "org.http4s" %% "http4s-blaze-server" % Http4sBlazeVersion
  )

  lazy val ZioQuill = Seq(
    "io.getquill" %% "quill-jdbc-zio" % ZioQuillVersion
  )

  lazy val MySql = Seq(
    "org.flywaydb" % "flyway-mysql"         % FlywayVersion,
    "mysql"        % "mysql-connector-java" % MySqlConnectorVersion
  )

  lazy val ApacheCodecs = Seq(
    "commons-codec" % "commons-codec" % CodecsVersion
  )
}
