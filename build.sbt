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

javaOptions ++= Seq(
//  "-Dorg.tensorflow.NativeLibrary.DEBUG=1",
//  "-Dorg.bytedeco.javacpp.logger.debug=true"
//  "-Dai.djl.default_engine=PyTorch"
)

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
      "com.lightbend.akka.management" %% "akka-management" % AkkaManagementVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,

      "ai.djl" % "api" % "0.28.0",
      "org.tensorflow" % "tensorflow-core-native" % "1.0.0-rc.1" classifier "macosx-arm64",
      "ai.djl.tensorflow" % "tensorflow-model-zoo" % "0.28.0",
      //somehow tensorflow-native-auto is not needed
      //      "ai.djl.tensorflow" % "tensorflow-native-auto" % "2.4.1",
      //tensorflow-engine is not needed too
      //      "ai.djl.tensorflow" % "tensorflow-engine" % "0.28.0",

        // not needed for mac m1, may be needed for other os
//      "ai.djl.pytorch" % "pytorch-engine" % "0.28.0" % Runtime,

      //for hugging face tokenizer
      "ai.djl.huggingface" % "tokenizers" % "0.28.0",
//      "ai.djl.pytorch" % "pytorch-model-zoo" % "0.28.0",
      //for mac m1
      //https://docs.djl.ai/engines/pytorch/pytorch-engine/index.html#macos-m1
      "ai.djl.pytorch" % "pytorch-native-cpu" % "2.2.2" % Runtime classifier "osx-aarch64",
      "ai.djl.pytorch" % "pytorch-jni" % "2.2.2-0.28.0" % Runtime,

      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    )
