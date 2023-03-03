ThisBuild / scalaVersion     := "3.2.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.scalajobs"
ThisBuild / organizationName := "ScalaJobs"

lazy val root = (project in file("."))
  .settings(
    name := "enum-serialization",
    libraryDependencies ++= Seq(
      "io.circe"      %% "circe-core"   % "0.14.4",
      "io.circe"      %% "circe-parser" % "0.14.4",
      "org.scalameta" %% "munit"        % "0.7.29" % Test,
    )
  )

