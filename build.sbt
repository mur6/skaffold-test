val akkaVersion = "2.5.25"
val akkaHttpVersion = "10.1.10"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(
    name := "akka-http-test",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ),
    dockerBaseImage := "java:8-jdk-alpine",
    version in Docker := "latest",
    dockerExposedPorts := Seq(8080),
    dockerUsername := Some("mur6")
  )
