# spark-hyperloglog
Algebird's HyperLogLog support for Apache Spark. This package can be used in concert
with [presto-hyperloglog](https://github.com/vitillo/presto-hyperloglog) to share
HyperLogLog sets between Spark and Presto.

[![codecov.io](https://codecov.io/github/mozilla/spark-hyperloglog/coverage.svg?branch=master)](https://codecov.io/github/mozilla/spark-hyperloglog?branch=master)
[![CircleCi](https://circleci.com/gh/mozilla/spark-hyperloglog.svg?style=shield&circle-token=5506f56072f0198ece2995a8539c174cc648c9e4)](https://circleci.com/gh/mozilla/spark-hyperloglog)

### Installing

This project is published as 
[mozilla/spark-hyperloglog](https://spark-packages.org/package/mozilla/spark-hyperloglog)
on spark-packages.org, so is available via:

    spark --packages mozilla:spark-hyperloglog:2.2.0


### Example usage

```scala
import com.mozilla.spark.sql.hyperloglog.aggregates._
import com.mozilla.spark.sql.hyperloglog.functions._

val hllMerge = new HyperLogLogMerge
spark.udf.register("hll_merge", hllMerge)
spark.udf.register("hll_create", hllCreate _)
spark.udf.register("hll_cardinality", hllCardinality _)

val frame = sc.parallelize(List("a", "b", "c", "c")).toDF("id")
(frame
  .select(expr("hll_create(id, 12) as hll"))
  .groupBy()
  .agg(expr("hll_cardinality(hll_merge(hll)) as count"))
  .show())
```

yields:

```bash
+-----+
|count|
+-----+
|    3|
+-----+
```

### Deployment

To publish a new version of the package, you need to
[create a new release on GitHub](https://github.com/mozilla/spark-hyperloglog/releases/new)
with a tag version starting with `v` like `v2.2.0`. The tag will trigger a CircleCI build
that publishes to Mozilla's maven repo in S3.

The CircleCI build will also attempt to publish the new tag to spark-packages.org,
but due to
[an outstanding bug in the sbt-spark-package plugin](https://github.com/databricks/sbt-spark-package/issues/31)
that publish will likely fail. You can retry locally until is succeeds by creating a GitHub
personal access token and, exporting the environment variables `GITHUB_USERNAME` and
`GITHUB_PERSONAL_ACCESS_TOKEN`, and then repeatedly running `sbt spPublish` until you get a
non-404 response.
