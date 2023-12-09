name := """PFE-backend"""
organization := "be.vinci.ipl"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.12"

libraryDependencies += guice
libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
libraryDependencies ++= Seq(
  "org.playframework" %% "play-slick"            % "6.0.0",
  "org.playframework" %% "play-slick-evolutions" % "6.0.0",
  "org.postgresql"    %  "postgresql"            % "42.5.4"         // Version actuelle du pilote PostgreSQL
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "be.vinci.ipl.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "be.vinci.ipl.binders._"
