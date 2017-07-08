
lazy val commonSettings = Seq(
  version             := "0.1-SNAPSHOT",
  name                := "regressr",
  organization        := "org.ebayopensource.regression",
  scalaVersion        := "2.11.8",
  test in assembly := {}
)

lazy val app = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("org.ebayopensource.regression.CommandLineMain"),
    assemblyJarName in assembly := "regressr.jar"
  )

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.7",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.8.7",
  "com.ebay.n" %% "play-svc-common" % "2.0.2",
  "org.scalatra.scalate" %% "scalate-core" % "1.8.0",
  "org.skyscreamer" % "jsonassert" % "1.5.0",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5"
)

libraryDependencies ++= {
  if (coverageEnabled.value) {
    Seq("org.scoverage" %% "scalac-scoverage-runtime" % "1.1.1")
  }
  else {
    Nil
  }
}

// Test dependencies

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.1" % Test
libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % Test

scalastyleConfig := file("project/scalastyle-config.xml")

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value
(compile in Compile) <<= (compile in Compile) dependsOn compileScalastyle

publishTo := {
  val repo = if (version.value endsWith "SNAPSHOT") "snapshots" else "releases"
  Some("nexus.nchant.us" at "http://nexus.nchant.us/nexus/content/repositories/" + repo)
}
