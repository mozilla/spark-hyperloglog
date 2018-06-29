# pyspark-hyperloglog

Python bindings for the spark-hyperloglog package.

## Usage

The python bindings are included in the distribution on spark-packages.org,
so they should be automatically available if the spark-hyperloglog library
is loaded on the cluster or specified via `--packages`
(but see the section below about EMR for caveats on that platform):

    pyspark --packages mozilla:spark-hyperloglog:2.2.0

The package will register itself with the current pyspark installation
location in the current site-packages. This allows for tests against spark in standalone mode.

```python
from pyspark.sql import SparkSession
from pyspark.sql.functions import expr

from pyspark_hyperloglog import hll


spark = SparkSession.builder.getOrCreate()

frame = spark.createDataFrame([{'id': x} for x in ['a', 'b', 'c', 'c']])
hll.register()

(
    frame
    .select(expr("hll_create(id, 12) as hll"))
    .groupBy()
    .agg(expr("hll_cardinality(hll_merge(hll)) as count"))
    .show()
)

```

If you run into issues during `.register()`, make sure that the dataframe has been created before registering the
User Defined Functions.

## Building

In the top-level directory, build the `spark-hyperloglog` package.

```bash
sbt assembly
```

Then build and install the package.

```bash
python setup.py sdist
pip install dist/*.tar.gz
```

## Tests

Tests are run using tox and assume you've already run `sbt assembly` as discussed in the previous section:

```bash
PYSPARK_SUBMIT_ARGS="--jars ../target/scala-2.11/spark-hyperloglog-assembly-*.jar pyspark-shell" tox
```

## Using the package on Amazon EMR

EMR does not correctly build the python environment to include python code from
Spark packages, but you can work around this in your pySpark session via:

```python
import sys

pyfiles = str(sc.getConf().get(u'spark.submit.pyFiles')).split(',')
sys.path.extend(pyfiles)
```
