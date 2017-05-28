package com.pragsis.sentiment.learner

import java.util.Date

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.ClassificationModel
import org.apache.spark.mllib.classification.ClassificationModel
import org.apache.spark.mllib.classification.ClassificationModel
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.feature.IDF
import org.apache.spark.mllib.feature.IDFModel
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.Saveable
import org.apache.spark.rdd.RDD

import com.pragsis.sentiment.learner.semantics.Phrase
import com.pragsis.sentiment.models.AlgorithmsEnum
import com.pragsis.sentiment.utils.Utils

class SentimenTrainer {

  private val modelDir = "models/"

  private var info: String = "";

  def learner(rdd: RDD[(Double, Phrase)], sc: SparkContext) {
    rdd.cache()

    var tf = new HashingTF()

    val result: RDD[(Double, Vector)] = rdd.map(line => (line._1, tf.transform(line._2)))

    var modelMeans = scala.collection.mutable.Map[AlgorithmsEnum, Double]()
    for (alg <- AlgorithmsEnum.values()) {
      modelMeans.put(alg, modelMean(result, 4, alg))
    }

    var max: Double = 0
    var bestAlg: AlgorithmsEnum = null;
    for ((alg, mean) <- modelMeans) {
      info = info.concat("\n" + alg + " media total:" + mean + "\n")
      if (math.max(max, mean) >= max) {
        max = mean
        bestAlg = alg
      }
    }

    if (bestAlg != null) {
      val totalModels = modelTrainer(result, bestAlg);
      totalModels._1.asInstanceOf[Saveable].save(sc, modelDir + "algorithm")
      var listIDF = totalModels._2 :: Nil
      sc.parallelize(listIDF, 1).saveAsObjectFile(modelDir + "idf");
      var listCompress = totalModels._3 :: Nil
      sc.parallelize(listCompress, 1).saveAsObjectFile(modelDir + "compress");
      var infos = info :: Nil
      sc.parallelize(infos, 1).saveAsTextFile(modelDir + "info")

    }

  }

  private def modelMean(result: RDD[(Double, Vector)], num: Int, alg: AlgorithmsEnum): Float = {

    val weights: Array[Double] = Array(0.8, 0.2)
    var mean: Float = 0;
    for (iteration <- 1 to num) {
      var split: Array[RDD[(Double, Vector)]] = result.randomSplit(weights, new Date().getTime())
      var train: RDD[(Double, Vector)] = split(0)
      var test: RDD[(Double, Vector)] = split(1)
      val models = modelTrainer(train, alg)

      var iteraMean = modelTester(test, models._1, models._2, models._3)
      mean = mean + iteraMean;

      info = info.concat("\n" + alg + " iteracion " + iteration + " resultado:" + iteraMean)
    }
    mean = mean / num;

    return mean;
  }

  private def modelTrainer(rddTrain: RDD[(Double, Vector)], alg: AlgorithmsEnum): (ClassificationModel, IDFModel, Array[Int]) = {

    val values: RDD[Vector] = rddTrain.map(line => line._2)

    val idfModel = new IDF(2).fit(values);
    var compress: Array[Int] = Utils.generateIDFConversor(idfModel);

    val points = rddTrain.map(line =>
      (line._1.doubleValue(),
        Utils.filterIDF(idfModel.transform(line._2), compress)))
    val labels = points.map(line =>
      new LabeledPoint(line._1, line._2))

    val model = alg.train(labels)

    return (model, idfModel, compress)
  }

  private def modelTester(rddTest: RDD[(Double, Vector)], model: ClassificationModel, idfModel: IDFModel, compress: Array[Int]): Float = {
    rddTest.cache();
    val points = rddTest.map({ case (x, y) => (x, Utils.filterIDF(idfModel.transform(y), compress)) })

    val results = points.filter(line => line._1 == model.predict(line._2))
    var count: Float = results.count().toFloat / rddTest.count().toFloat;
    rddTest.unpersist();
    return count;
  }

}



