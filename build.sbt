ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "DataDashboard",
    libraryDependencies += "com.lihaoyi" %% "requests" % "0.8.0",
    libraryDependencies += "org.scalafx" % "scalafx_3" % "19.0.0-R30",
    libraryDependencies += "com.lihaoyi" %% "upickle" % "3.1.0",
  )
