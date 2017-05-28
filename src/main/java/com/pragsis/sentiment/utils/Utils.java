package com.pragsis.sentiment.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;

import com.pragsis.sentiment.learner.semantics.Phrase;
import com.pragsis.sentiment.learner.semantics.Word;

public class Utils {

	public static Phrase splitLine(String text, String language) {
		Phrase phrase = new Phrase();

		for (PattersTokenizer regex : PattersTokenizer.values()) {
			text = text.replaceAll(regex.getRegex(), " " + regex.getToken() + " ");
		}

		String[] words = text.split("[^\\p{L}\\p{N}_*]");

		for (String word : words) {
			if (word.length() < 2) {
				continue;
			}
			if (language == null || language.equals("")) {
				phrase.add(new Word(word.toLowerCase()));
			} else {
				phrase.add(new Word(word.toLowerCase(), language));
			}

		}

		return phrase;
	}

	public static Vector filterIDF(final Vector v, final int[] idfConversor) {
		SparseVector sV = (SparseVector) v;
		List<Integer> indices = new ArrayList<Integer>();
		List<Double> values = new ArrayList<Double>();
		Set<Integer> ints = new HashSet<Integer>();
		for (int j = 0; j < sV.indices().length; j++) {
			ints.add(sV.indices()[j]);
		}

		for (int i = 0; i < idfConversor.length; i++) {
			if (ints.contains(idfConversor[i])) {
				indices.add(i);
				values.add(sV.apply(idfConversor[i]));
			}
		}

		int[] ind = ArrayUtils.toPrimitive(indices.toArray(new Integer[indices.size()]));
		double[] dou = ArrayUtils.toPrimitive(values.toArray(new Double[values.size()]));

		return new SparseVector(idfConversor.length, ind, dou);
	}

	public static int[] generateIDFConversor(final IDFModel idfModel) {
		List<Integer> listNonZero = new ArrayList<Integer>();

		double[] array = idfModel.idf().toArray();
		for (int i = 0; i < array.length; i++) {
			if (array[i] != 0) {
				listNonZero.add(i);
			}
		}

		return ArrayUtils.toPrimitive(listNonZero.toArray(new Integer[listNonZero.size()]));
	}
}
