name := "spark-hyperloglog"

version := sys.env.getOrElse("CIRCLE_TAG", "v2.2-SNAPSHOT").stripPrefix("v")

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


// Include the contents of the python/ directory at the root of our packaged jar;
// `sbt spPublish` handles including python files for the zip sent to spark-packages.org,
// but we also want the python bindings to be present in the jar we upload to S3 maven
// via `sbt publish`.
val pythonBesidesPyspark = new SimpleFileFilter({ f =>
  val pythonDir = "/spark-hyperloglog/python"
  val pyLibDir = pythonDir + "/pyspark_hyperloglog"
  val p = f.getCanonicalPath
  p match {
    case _ if p.contains(pyLibDir) => false  // Don't exclude contents of pyspark dir
    case _ if p.contains(pythonDir + "/") => true  // Exclude everything else under python/
    case _ => false  // Don't exclude other files not under python/
  }
})
unmanagedResourceDirectories in Compile += baseDirectory.value / "python"
excludeFilter in unmanagedResources :=
  HiddenFileFilter || pythonBesidesPyspark || "*.pyc" || "*.egg*"

// Since we're taking responsibility for including python files above, we need to
// prevent sbt-spark-package from duplicating that work in spPackage.
mappings in (Compile, spPackage) := (mappings in (Compile, packageBin)).value

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
