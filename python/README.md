# pyspark-hyperloglog

Python bindings for the spark-hyperloglog package.

## Usage

Include the bindings in your project.

```bash
pip install pyspark_hyperloglog
```

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

Tests are run using tox.

```bash
tox
```
