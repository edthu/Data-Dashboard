ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "DataDashboard",
    libraryDependencies += "com.lihaoyi" %% "requests" % "0.8.0",
    //libraryDependencies += "org.scala-lang.module" % "scala-swing_3" % "3.0.0"
  )
