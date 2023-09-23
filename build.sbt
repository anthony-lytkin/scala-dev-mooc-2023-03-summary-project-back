import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "Booking meeting room"
  )

libraryDependencies ++= zio
libraryDependencies +=  zioHTTP
libraryDependencies ++= zioConfig
libraryDependencies ++= slf4j
libraryDependencies ++= circe
libraryDependencies ++= quill
libraryDependencies +=  postgres
libraryDependencies +=  liquibase
libraryDependencies ++= testContainers