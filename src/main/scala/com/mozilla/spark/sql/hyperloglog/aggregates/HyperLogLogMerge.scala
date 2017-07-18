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
package com.mozilla.spark.sql.hyperloglog.aggregates

import com.twitter.algebird.{Bytes, DenseHLL, HyperLogLog}
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

/**
 * This class fixes HyperLogLogMerge when there are no
 * rows, or when input rows are NULL.
 */
class HyperLogLogMerge extends UserDefinedAggregateFunction {

  val DefaultBits = 12

  /**
   * This HLL instance has zero counts.
   *
   * scala> (new DenseHLL(12, new Bytes(Array.fill[Byte](1 << 12)(0)))).approximateSize.estimate
   * res0: Long = 0
   */
  val emptyHll = new DenseHLL(DefaultBits, new Bytes(Array.fill[Byte](1 << DefaultBits)(0)))

  def inputSchema: org.apache.spark.sql.types.StructType =
    StructType(StructField("value", BinaryType) :: Nil)

  def bufferSchema: StructType = StructType(StructField("count", BinaryType) ::
    StructField("bits", IntegerType) :: Nil)

  def dataType: DataType = BinaryType

  def deterministic: Boolean = true

  def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = null
    buffer(1) = 0
  }

  def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if (input(0) != null) {
      val hll = HyperLogLog.fromBytes(input.getAs[Array[Byte]](0)).toDenseHLL

      if (buffer(0) != null) {
        hll.updateInto(buffer.getAs[Array[Byte]](0))
      } else {
        buffer(0) = hll.v.array
        buffer(1) = hll.bits
      }
    }
  }

  def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    if (buffer1(0) == null) {
      buffer1(0) = buffer2(0)
      buffer1(1) = buffer2(1)
    } else if (buffer1(0) != null && buffer2(0) != null) {
      val state2 = new DenseHLL(buffer2.getAs[Int](1), new Bytes(buffer2.getAs[Array[Byte]](0)))
      state2.updateInto(buffer1.getAs[Array[Byte]](0))
    }
  }

  def evaluate(buffer: Row): Any = {
    val state = buffer(0) match {
      case null => emptyHll
      case o => new DenseHLL(buffer.getAs[Int](1), new Bytes(buffer.getAs[Array[Byte]](0)))
    }
    com.twitter.algebird.HyperLogLog.toBytes(state)
  }
}

/**
 * This class adds the capability to take in
 * another Boolean column and only adds the
 * associated hll if the Boolean column is `True`.
 */
class FilteredHyperLogLogMerge extends HyperLogLogMerge {

  override def inputSchema: org.apache.spark.sql.types.StructType =
    StructType(StructField("value", BinaryType) :: StructField("filtered", BooleanType) :: Nil)

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if (input(1) != null && input.getAs[Boolean](1)) {
      super.update(buffer, input)
    }
  }
}
