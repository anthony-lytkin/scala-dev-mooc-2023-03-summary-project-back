import sbt._

object Dependencies {

  //// VERSIONS ////
  lazy val zioVersion       = "2.0.16"
  lazy val zioConfigVersion = "3.0.7"
  lazy val zioHttpVersion   = "1.0.0.0-RC27"
  lazy val circeVersion     = "0.14.1"

  //// TESTS LIBS VERSIONS ////
  lazy val scalaTestContainersVersions = "0.40.12"

  lazy val zio = Seq(
    "dev.zio" %% "zio"          % zioVersion,
    "dev.zio" %% "zio-test"     % zioVersion,
    "dev.zio" %% "zio-test-sbt" % zioVersion,
    "dev.zio" %% "zio-macros"   % zioVersion,
  )

  lazy val zioHTTP = "io.d11"  %% "zhttp" % zioHttpVersion

  lazy val zioConfig = Seq(
    "dev.zio" %% "zio-config"          % zioConfigVersion,
    "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
    "dev.zio" %% "zio-config-typesafe" % zioConfigVersion
  )

  lazy val circe = Seq(
    "io.circe" %% "circe-core"       % circeVersion,
    "io.circe" %% "circe-generic"    % circeVersion,
    "io.circe" %% "circe-parser"     % circeVersion,
    "io.circe" %% "circe-literal"    % circeVersion,
    "io.circe" %% "circe-derivation" % "0.13.0-M4",
  )

  lazy val quill = Seq(
    "io.getquill" %% "quill-jdbc-zio" % "4.6.0",
    "io.github.kitlangton" %% "zio-magic" % "0.3.12"
  )

  lazy val postgres = "org.postgresql" % "postgresql" % "42.5.4"
  lazy val liquebase = "org.liquibase" % "liquibase-core" % "4.20.0"


  lazy val testContainers = Seq(
    "com.dimafeng" %% "testcontainers-scala-postgresql" % scalaTestContainersVersions % Test,
    "com.dimafeng" %% "testcontainers-scala-scalatest"  % scalaTestContainersVersions % Test
  )






}
