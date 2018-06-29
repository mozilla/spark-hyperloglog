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
sqlContext.udf.register("hll_merge", hllMerge)
sqlContext.udf.register("hll_create", hllCreate _)
sqlContext.udf.register("hll_cardinality", hllCardinality _)

val frame = sc.parallelize(List("a", "b", "c", "c")).toDF("id")
val count = frame
  .select(expr("hll_create(id, 12) as hll"))
  .groupBy()
  .agg(expr("hll_cardinality(hll_merge(hll)) as count"))
  .show()
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
Any commits to master should also trigger a circleci build that will do the sbt publishing for you
to our local maven repo in s3 and to spark-packages.org.
