// Project metadata
name := "PFE-backend"
organization := "be.vinci.ipl"
version := "1.0-SNAPSHOT"

// Define the root project using PlayScala
lazy val root = (project in file(".")).enablePlugins(PlayScala)

// Scala version
scalaVersion := "2.13.12"

libraryDependencies += "org.apache.pekko" %% "pekko-actor" % "1.0.0"

// Dependencies
libraryDependencies += guice
libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
libraryDependencies ++= Seq(
  "org.playframework" %% "play-slick"            % "6.0.0",
  "org.playframework" %% "play-slick-evolutions" % "6.0.0",
  "org.postgresql"    %  "postgresql"            % "42.5.4" // PostgreSQL driver version
)
