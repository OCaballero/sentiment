package com.pragsis.sentiment.learner.semantics;

public final class Word {

	public static int hashCodeStyle = 1;

	private String original;
	private String modified;

	private LexicalCategoryEnum category;
	
	public Word(final String pOriginal) {
		original = pOriginal;
		obtainCategory();
	}

	public Word(final String pOriginal, final String pLanguage) {
		original = pOriginal;
		modified = obtainStemming(pLanguage);
		obtainCategory();
	}

	public Word(final String pOriginal, final String pModified, final String pLanguage) {
		original = pOriginal;
		modified = pModified;
		obtainCategory();
	}

	public String obtainStemming(final String pLanguage) {
		return WordsFactory.getInstance().stem(original, pLanguage);
	}

	public void obtainCategory() {

	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(final String pOriginal) {
		original = pOriginal;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(final String pModified) {
		modified = pModified;
	}

	public LexicalCategoryEnum getCategory() {
		return category;
	}

	public void setCategory(final Object pCategory) {
		category = (LexicalCategoryEnum) pCategory;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		switch (hashCodeStyle) {
		case 0:
			result = prime * result + ((original == null) ? 0 : original.hashCode());
			return result;
		case 1:
			result = prime * result + ((modified == null) ? (original == null) ? 0 : original.hashCode() : modified.hashCode());
			return result;
		default:
			result = prime * result + ((category == null) ? 0 : category.hashCode());
			result = prime * result + ((modified == null) ? 0 : modified.hashCode());
			result = prime * result + ((original == null) ? 0 : original.hashCode());
			return result;
		}

		
	}

	@Override
	public String toString() {
		return original;
	}

}
