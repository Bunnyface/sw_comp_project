val finchVersion = "0.26.0"
val circeVersion = "0.10.1"
val scalatestVersion = "3.0.5"
val postgresqlVersion = "42.2.18"
val sprayVersion = "1.3.6"
val logbackVersion = "1.2.3"
val loggingVersion = "3.9.3"
val mockitoVersion = "1.5.12"


lazy val root = (project in file("."))
  .settings(
    organization := "com.example",
    name := "playground",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finchx-core"  % finchVersion,
      "com.github.finagle" %% "finchx-circe"  % finchVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "org.scalatest"      %% "scalatest"    % scalatestVersion % "test",
      "org.postgresql" % "postgresql" % postgresqlVersion,
      "io.spray" %%  "spray-json" % sprayVersion,
      "org.mockito" %% "mockito-scala" % mockitoVersion % "test"
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % loggingVersion
    )
  )
