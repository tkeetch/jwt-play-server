name := """jwt-play-server"""

version := "0.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

libraryDependencies += "org.bitbucket.b_c" % "jose4j" % "0.4.4"
 
// Scala compiler options
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-optimise",
  "-explaintypes",
  "-encoding", "UTF-8",
  "-Xlint"
)
