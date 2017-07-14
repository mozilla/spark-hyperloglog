/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mozilla.spark.sql.hyperloglog.test

import com.mozilla.spark.sql.hyperloglog.aggregates._
import com.mozilla.spark.sql.hyperloglog.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.expr
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

case class ClientRow(id: String, os: String)

class HyperLogLogTest extends FlatSpec with Matchers with BeforeAndAfterAll {

  val spark = SparkSession.builder()
      .master("local[*]")
      .appName("Spark HyperLogLog Test")
      .getOrCreate()

  val osList = "windows" :: "macos" :: "linux" :: Nil
  val predata = List(
    ClientRow("a", "windows"),
    ClientRow("b", "windows"),
    ClientRow("c", "macos"),
    ClientRow("c", "macos"),
    ClientRow(null, "linux")
  )

  "Algebird's HyperLogLog" can "be used from Spark" in {
    import spark.implicits._

    val hllMerge = new HyperLogLogMerge
    spark.udf.register("hll_merge", hllMerge)
    spark.udf.register("hll_create", hllCreate _)
    spark.udf.register("hll_cardinality", hllCardinality _)

    val rows = predata.toDS().toDF()
      .selectExpr("hll_create(id, 12) as hll")
      .groupBy()
      .agg(expr("hll_cardinality(hll_merge(hll)) as count"))
      .collect()
    rows(0)(0) should be (3)
  }

  "HyperLogLog" can "handle null and missing values" in {
    import spark.implicits._

    val hllMerge = new HyperLogLogMerge
    spark.udf.register("hll_merge", hllMerge)
    spark.udf.register("hll_create", hllCreate _)
    spark.udf.register("hll_cardinality", hllCardinality _)

    val rows = predata.toDS()
      .selectExpr("os", "hll_create(id, 12) as hll")
      .groupBy()
      .pivot("os", osList)
      .agg(expr("hll_cardinality(hll_merge(hll)) as count"))
      .collect()

    rows.length should be (1)
    rows(0).getAs[Integer]("windows") should be (2)
    rows(0).getAs[Integer]("macos") should be (1)
    rows(0).getAs[Integer]("linux") should be (0)
  }

  override def afterAll = {
    spark.stop()
  }
}
