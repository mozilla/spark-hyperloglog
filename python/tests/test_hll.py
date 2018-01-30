import pytest
from pyspark.sql import SparkSession
from pyspark.sql import Row, functions as F
from pyspark_hyperloglog import hll


@pytest.fixture(scope="session")
def spark():
    spark = (
        SparkSession
        .builder
        .master("local")
        .appName("pyspark_hyperloglog_test")
        .getOrCreate()
    )
    yield spark
    spark.stop()


@pytest.fixture
def test_data(spark):
    data = [
        Row(uid="uid_1", color="red"),
        Row(uid="uid_2", color="blue"),
        Row(uid="uid_3", color="blue"),
        Row(uid="uid_3", color="red")
    ]
    return spark.createDataFrame(data)


def test_register_functions(test_data):
    hll.register()

    result = (
        test_data
        .select(F.expr("hll_create(uid, 12) as hll"))
        .groupBy()
        .agg(F.expr("hll_cardinality(hll_merge(hll)) as count"))
    )

    assert result.first()[0] == 3


def test_hll_aggregates_correctly(test_data):
    hll.register()

    result = (
        test_data
        .withColumn("hll", hll.create("uid", 12))
        .groupBy("color")
        .agg(hll.merge("hll").alias("hll"))
        .select("color", hll.cardinality("hll").alias("count"))
        .where("color = 'red'")
    )

    assert result.first()['count'] == 2