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


VERSION = '2.1.0'

JARS_TARGET = 'deps/jars'
ASSEMBLY_JAR = "*-assembly-{}-*.jar".format(VERSION)


is_packaging = (
    os.path.isfile("../build.sbt") and
    not os.path.isfile(os.path.join(JARS_TARGET, ASSEMBLY_JAR))
)

if is_packaging:
    SPARK_HLL_HOME = os.path.abspath("../")
    JAR_PATH = glob.glob(os.path.join(
        SPARK_HLL_HOME, "target/scala-*", ASSEMBLY_JAR))

    if len(JAR_PATH) != 1:
        print(os.listdir(SPARK_HLL_HOME))
        print("Could not find assembled jar")
        sys.exit(-1)

    JAR_PATH = JAR_PATH[0]

    try:
        os.makedirs(JARS_TARGET)
    except:
        print("Temporary path to jars already exists {}".format(JARS_TARGET))
        sys.exit(-1)

    os.symlink(JAR_PATH, os.path.join(JARS_TARGET, os.path.basename(JAR_PATH)))
else:
    if not os.path.exists(JARS_TARGET):
        print("The jar folder must exist")

setup(
    name='pyspark-hyperloglog',
    version=VERSION,
    description='PySpark UDFs for HyperLogLog',
    author='Anthony Miyaguchi',
    author_email='amiyaguchi@mozilla.com',
    packages=[
        'pyspark_hyperloglog',
        'pyspark_hyperloglog.jars'
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
        'pyspark_hyperloglog.jars': 'deps/jars'
    },
    package_data={
        'pyspark_hyperloglog.jars': ['*.jar']
    },
)

if is_packaging:
    shutil.rmtree('deps')
