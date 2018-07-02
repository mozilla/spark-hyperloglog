from setuptools import setup
import os

version = os.environ.get('CIRCLE_TAG', 'v2.2.snapshot').lstrip('v')

setup(
    name='pyspark-hyperloglog',
    version=version,
    description='PySpark UDFs for HyperLogLog',
    keywords=['spark', 'udf', 'hyperloglog'],
    author='Anthony Miyaguchi',
    author_email='amiyaguchi@mozilla.com',
    url='https://github.com/mozilla/spark-hyperloglog',
    packages=[
        'pyspark_hyperloglog',
    ],
    install_requires=['pyspark'],
    extras_require={
        'dev': [
            'pytest',
            'tox'
        ]
    },
)
