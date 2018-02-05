""""
This build script is modeled after the pyspark package in the apache/spark
repository.

https://github.com/apache/spark/blob/master/python/setup.py
"""

from setuptools import setup
import os
import glob
import sys
import shutil


# read the version file in the package or in the root project directory
version_file = "VERSION" if os.path.isfile("VERSION") else "../VERSION"
with open(version_file, 'r') as f:
    VERSION = f.read().strip()

JARS_TARGET = 'deps/jars'
JAR_FILE = "*-assembly-{}.jar".format(VERSION)


is_packaging = (
    os.path.isfile("../build.sbt") and
    not os.path.isfile(os.path.join(JARS_TARGET, JAR_FILE))
)

if is_packaging:
    SPARK_HLL_HOME = os.path.abspath("../")
    JAR_PATH = glob.glob(os.path.join(
        SPARK_HLL_HOME, "target/scala-*", JAR_FILE))

    if len(JAR_PATH) != 1:
        print("Could not find assembled jar")
        sys.exit(-1)

    JAR_PATH = JAR_PATH[0]

    try:
        os.makedirs(JARS_TARGET)
    except:
        print("Temporary path to jars already exists {}".format(JARS_TARGET))
        sys.exit(-1)

    os.symlink(JAR_PATH, os.path.join(JARS_TARGET, os.path.basename(JAR_PATH)))
    os.symlink("../VERSION", "VERSION")
else:
    if not os.path.exists(JARS_TARGET):
        print("The jar folder must exist")

setup(
    name='pyspark-hyperloglog',
    version=VERSION.split('-')[0],
    description='PySpark UDFs for HyperLogLog',
    keywords=['spark', 'udf', 'hyperloglog'],
    author='Anthony Miyaguchi',
    author_email='amiyaguchi@mozilla.com',
    url='https://github.com/mozilla/spark-hyperloglog',
    packages=[
        'pyspark_hyperloglog',
        'pyspark.jars'
    ],
    install_requires=['pyspark'],
    extras_require={
        'dev': [
            'pytest',
            'tox'
        ]
    },
    include_package_data=True,
    package_dir={
        'pyspark_hyperloglog': 'src',
        'pyspark.jars': 'deps/jars'
    },
    package_data={
        'pyspark.jars': ['*.jar']
    },
)

if is_packaging:
    shutil.rmtree('deps')
    os.remove("VERSION")
