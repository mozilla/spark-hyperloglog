from pyspark.sql import SparkSession
from pyspark import SparkContext
import os
import glob

import logging


def update_spark_builder():
    # also check for spark-hyperloglog in the path somewhere
    if SparkContext._active_spark_context:
        # TODO: check that the spark-hyperloglog library is attached
        message = """
        In order to dynamically register the spark-hyperloglog
        binary with Spark for local work, the existing spark context should
        be stopped. Alternatively, attach the jar to the spark-shell or
        spark-submit script.
        """
        logging.warning(message)
        return

    filepath = os.path.join(__path__[0], "jars/*.jar")
    jar_path = glob.glob(filepath)
    if not jar_path:
        logging.warning("Cannot find jar matching {}".format(filepath))
        return

    jar_path = jar_path[0]
    print("token " + jar_path)

    # update the builder object on import of this library
    SparkSession.builder = (
        SparkSession.builder.config("spark.driver.extraClassPath", jar_path)
    )

update_spark_builder()
