package com.pragsis.sentiment.learner

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import com.pragsis.sentiment.utils.Utils

object Main extends App {
  val conf = new SparkConf().setAppName("learner").setMaster("local[2]")
  val sc = new SparkContext(conf) 

  val values = sc.textFile("/tmp", 1)
    .filter { x => x.split("\",").size > 1 }
    .filter { x => x.split("\",")(1) == "negativo" || x.split("\",")(1) == "positivo" || x.split("\",")(1) == "neutro" }

  val values2 = values.map { x =>
    (
      x.split("\",").apply(1).hashCode().toDouble,
      Utils.splitLine(x.split("\",").apply(0), ""))
  }

  new SentimenTrainer().learner(values2, sc)

}



