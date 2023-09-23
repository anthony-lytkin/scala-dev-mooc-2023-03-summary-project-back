import sbt._

object Dependencies {

  object versions {

    lazy val zioVersion = "1.0.4"
    lazy val zioConfigVersion = "1.0.5"
    lazy val zioHttpVersion = "1.0.0.0-RC27"
    lazy val circeVersion = "0.14.1"

    lazy val slf4jVersion = "1.7.36"
    lazy val log4jVersion = "2.17.1"

    lazy val scalaTestContainersVersions = "0.40.12"
  }


  import versions._

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

  lazy val slf4j = Seq(
    "org.slf4j" % "slf4j-reload4j" % slf4jVersion
    //    "dev.zio" %% "zio-logging" % "0.5.16",
//    "dev.zio" %% "zio-logging-slf4j" % "0.5.16"
//    "org.slf4j" % "slf4j-simple" % slf4jVersion
  )


  lazy val circe = Seq(
    "io.circe" %% "circe-core"       % circeVersion,
    "io.circe" %% "circe-generic"    % circeVersion,
    "io.circe" %% "circe-parser"     % circeVersion,
    "io.circe" %% "circe-literal"    % circeVersion,
    "io.circe" %% "circe-derivation" % "0.13.0-M4",
  )

  lazy val quill = Seq(
    "io.getquill" %% "quill-jdbc-zio" % "3.12.0",
    "io.github.kitlangton" %% "zio-magic" % "0.3.12"
  )

  lazy val postgres = "org.postgresql" % "postgresql" % "42.5.4"
  lazy val liquibase = "org.liquibase" % "liquibase-core" % "3.4.2"


  lazy val testContainers = Seq(
    "com.dimafeng" %% "testcontainers-scala-postgresql" % scalaTestContainersVersions % Test,
    "com.dimafeng" %% "testcontainers-scala-scalatest"  % scalaTestContainersVersions % Test
  )






}
