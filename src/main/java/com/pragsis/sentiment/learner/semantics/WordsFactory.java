package com.pragsis.sentiment.learner.semantics;

import java.util.HashMap;

import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.FrenchStemmer;
import org.tartarus.snowball.ext.German2Stemmer;
import org.tartarus.snowball.ext.ItalianStemmer;
import org.tartarus.snowball.ext.PortugueseStemmer;
import org.tartarus.snowball.ext.SpanishStemmer;

/**
 * WordsFactory.
 **/
public final class WordsFactory {

	private HashMap<String, SnowballProgram> stemmers = new HashMap<String, SnowballProgram>();
	private static WordsFactory wf = new WordsFactory();

	private WordsFactory() {
		stemmers.put("es", new SpanishStemmer());
		stemmers.put("en", new EnglishStemmer());
		stemmers.put("it", new ItalianStemmer());
		stemmers.put("pt", new PortugueseStemmer());
		stemmers.put("fr", new FrenchStemmer());
		stemmers.put("de", new German2Stemmer());
	}

	public static WordsFactory getInstance() {
		return wf;
	}

	public String stem(final String pInput, final String pLanguage) {

		SnowballProgram stemmer = stemmers.get(pLanguage);

		if (stemmer != null) {
			stemmer.setCurrent(pInput);
			if (stemmer.stem()) {
				return stemmer.getCurrent();
			}

		}
		return null;
	}
}
