# pyspark-hyperloglog

Python bindings for the spark-hyperloglog package.

## Usage

Include the bindings in your project.

```bash
pip install pyspark_hyperloglog
```

The package will register itself with the current pyspark installation
location in the current site-packages. This allows for tests against spark in standalone mode.

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