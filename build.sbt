name := "akka-tensorflow-service"

organization := "com.lightbend.akka.samples"
organizationHomepage := Some(url("https://akka.io"))
licenses := Seq(("CC0", url("https://creativecommons.org/publicdomain/zero/1.0")))

scalaVersion := "2.13.12"

Compile / scalacOptions ++= Seq(
  "-target:11",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlog-reflective-calls",
  "-Xlint")

Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

Test / parallelExecution := false
Test / testOptions += Tests.Argument("-oDF")
Test / logBuffered := false

run / fork := true
// pass along config selection to forked jvm
run / javaOptions ++= sys.props
  .get("config.resource")
  .fold(Seq.empty[String])(res => Seq(s"-Dconfig.resource=$res"))

Global / cancelable := false // ctrl-c

//javaOptions ++= Seq(
//  "-Dorg.tensorflow.NativeLibrary.DEBUG=1",
//  "-Dorg.bytedeco.javacpp.logger.debug=true"
//)

val AkkaVersion = "2.9.2"
val AkkaHttpVersion = "10.6.1"
val AkkaManagementVersion = "1.5.1"
val AkkaDiagnosticsVersion = "2.1.0"

enablePlugins(AkkaGrpcPlugin, JavaAppPackaging, DockerPlugin)

dockerBaseImage := "docker.io/library/eclipse-temurin:17.0.3_7-jre-jammy"
dockerUsername := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")
dockerUpdateLatest := true

ThisBuild / dynverSeparator := "-"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies ++= Seq(
      // 1. Basic dependencies for a clustered application
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,

      "ai.djl" % "api" % "0.28.0",
      "org.tensorflow" % "tensorflow-core-native" % "1.0.0-rc.1" classifier "macosx-arm64",
//      "ai.djl.tensorflow" % "tensorflow-native-auto" % "2.4.1",
      "ai.djl.tensorflow" % "tensorflow-model-zoo" % "0.28.0",

//      "ai.djl.tensorflow" % "tensorflow-engine" % "0.28.0",

      "com.lightbend.akka.management" %% "akka-management" % AkkaManagementVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.13",
      "org.scalatest" %% "scalatest" % "3.1.2" % Test,
    )
