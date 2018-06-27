name := "spark-hyperloglog"

version := scala.io.Source.fromFile("VERSION").mkString.stripLineEnd

scalaVersion := "2.11.8"

organization := "com.mozilla.telemetry"

// As required by https://github.com/databricks/sbt-spark-package#spark-package-developers
spName := "mozilla/spark-hyperloglog"
spShortDescription := "Algebird's HyperLogLog support for Apache Spark"
spDescription := "Algebird's HyperLogLog support for Apache Spark"
sparkVersion := "2.0.2"
sparkComponents ++= Seq("sql")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.twitter" %% "algebird-core" % "0.12.0"
)

// Appropriate environment variables for publishing are provided in the CircleCI environment.
credentials += Credentials(
  "Spark Packages Realm",
  "spark-packages.org",
  sys.env.getOrElse("GITHUB_USERNAME", ""),
  sys.env.getOrElse("GITHUB_PERSONAL_ACCESS_TOKEN", ""))

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
