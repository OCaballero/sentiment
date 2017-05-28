package com.pragsis.sentiment.models;

import org.apache.spark.mllib.classification.ClassificationModel;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.rdd.RDD;

public enum AlgorithmsEnum {
	
	naiveBayes;
	
	
	public ClassificationModel train(RDD<LabeledPoint> rdd){
		switch (this) {
		case naiveBayes:
			return NaiveBayes.train(rdd);		

		default:
			break;
		}
		
		return null;
	}

}
