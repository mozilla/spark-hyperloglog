name := "spark-hyperloglog"

version := "2.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

organization := "com.mozilla.telemetry"

sparkVersion := "2.0.2"

sparkComponents ++= Seq("core", "sql")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.twitter" %% "algebird-core" % "0.12.0"
)

credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")

publishMavenStyle := true

publishTo := {
  val localMaven = "s3://net-mozaws-data-us-west-2-ops-mavenrepo/"
  if (isSnapshot.value)
    Some("snapshots" at localMaven + "snapshots")
  else
    Some("releases"  at localMaven + "releases")
}

licenses += "Apache-2.0" -> url("http://opensource.org/licenses/Apache-2.0")

homepage := Some(url("http://github.com/mozilla/spark-hyperloglog"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/mozilla/spark-hyperloglog"),
    "scm:git@github.com:mozilla/spark-hyperloglog.git"
  )
)

developers := List(
  Developer(
    id    = "fbertsch",
    name  = "Frank Bertsch",
    email = "frank@mozilla.com",
    url   = url("http://frankbertsch.com")
  )
)
