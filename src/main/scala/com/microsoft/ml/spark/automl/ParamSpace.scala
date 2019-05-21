// Copyright (C) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in project root for information.

package com.microsoft.ml.spark.automl

import org.apache.spark.ml.param.{Param, ParamMap, ParamPair, ParamSpace}

/** Represents a distribution of values.
  * @tparam T The type T of the values generated.
  */
abstract class Dist[T] {

  def getNext(): T

  def getParamPair(param: Param[_]): ParamPair[_] = {
    ParamPair(param.asInstanceOf[Param[T]], getNext())
  }

}

/** Represents a parameter grid for tuning with discrete values.
  * Can be generated with the ParamGridBuilder.
  * @param paramValues The parameter values generated by ParamGridBuilder.
  */
class GridSpace(val paramValues: Array[ParamMap]) extends ParamSpace {

  override def paramMaps: Iterator[ParamMap] = paramValues.toIterator

}

/** Represents a generator of parameters with specified distributions added by the HyperparamBuilder.
  * @param paramDistributions A list of parameters and their distributions generated by HyperparamBuilder.
  */
class RandomSpace(paramDistributions: Array[(Param[_], Dist[_])]) extends ParamSpace {

  val paramMaps = new Iterator[ParamMap] {
    override def hasNext: Boolean = true

    override def next(): ParamMap =
      ParamMap(paramDistributions.map(paramDist => paramDist._2.getParamPair(paramDist._1)): _*)
  }

}