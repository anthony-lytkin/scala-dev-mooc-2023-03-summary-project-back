ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    organization := "Otus",
    name := "Booking meeting room",
  )

// zio
libraryDependencies ++= Dependencies.zio
libraryDependencies ++= Dependencies.zioConfig
libraryDependencies +=  Dependencies.zioHTTP
libraryDependencies ++= Dependencies.quill

// circe json
libraryDependencies ++= Dependencies.circe

// db
libraryDependencies += Dependencies.postgres
libraryDependencies += Dependencies.liquebase

// test
libraryDependencies ++= Dependencies.testContainers