name         := "zio_service_template"
version      := sys.env.getOrElse("RELEASE_VERSION", "undefined")
organization := "yusupov"

scalaVersion          := "2.13.10"
maxErrors             := 5
watchTriggeredMessage := Watch.clearScreenOnTrigger
scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:postfixOps",
  "-Ymacro-annotations"
)

// testing
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

dependencyUpdatesFilter -= moduleFilter(organization = "commons-codec")
dependencyUpdatesFailBuild := true

// build info
//enablePlugins(BuildInfoPlugin)

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.Logging,
      Dependencies.Zio,
      Dependencies.ZioHttp,
      Dependencies.Tapir,
      Dependencies.ZioTest,
      Dependencies.ZioConfig,
      Dependencies.ZioQuill,
      Dependencies.ZioMetrics,
      Dependencies.MySql,
      Dependencies.ApacheCodecs
    ).flatten
  )
