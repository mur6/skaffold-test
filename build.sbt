val akkaVersion = "2.5.25"
val akkaHttpVersion = "10.1.10"
val akkaManagementVersion = "1.0.3"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(
    name := "akka-cluster-test",
    scalaVersion := "2.12.8",
    resolvers += Resolver.bintrayRepo("tanukkii007", "maven"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
      "com.github.TanUkkii007" %% "akka-cluster-custom-downing" % "0.0.13",
      "com.ajjpj.simple-akka-downing" %% "simple-akka-downing" % "0.9.1",
      "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % akkaManagementVersion,
      "com.lightbend.akka.management" %% "akka-management" % akkaManagementVersion,
      "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion,
      "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaManagementVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ),
    dockerBaseImage := "java:8-jdk-alpine",
    version in Docker := "latest",
    daemonUser in Docker := "root",
    dockerExposedPorts := Seq(8080, 8558, 2552),
    dockerUsername := Some("mur6")
  )
