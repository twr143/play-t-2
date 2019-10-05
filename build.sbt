name := """play-t-2"""
organization := "iv"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala,SwaggerPlugin)

scalaVersion := "2.12.6"
val akkaVersion = "2.5.12"


libraryDependencies ++= Seq(
  jdbc,
  filters,
  evolutions)

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.1"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.typesafe.play" %% "play-guice" % "2.6.15"
libraryDependencies += "ai.x" %% "play-json-extensions" % "0.40.2"
libraryDependencies += "org.webjars" % "swagger-ui" % "2.2.0"

swaggerDomainNameSpaces := Seq("model.Model")
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "iv.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "iv.binders._"
