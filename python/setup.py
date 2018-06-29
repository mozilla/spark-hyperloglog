from setuptools import setup

with open('VERSION', 'r') as f:
    VERSION = f.read().strip()

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
    ],
    install_requires=['pyspark'],
    extras_require={
        'dev': [
            'pytest',
            'tox'
        ]
    },
)
