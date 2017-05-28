package com.pragsis.sentiment.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum PattersTokenizer {

	HTTP("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "*http*"), USER_TWITTER(
			"(^|[^\\w])@\\w{1,15}", "*person*"), HASTAG_TWITTER("\\S*#(?:\\[[^\\]]+\\]|\\S+)", "*hashtag*"),
			// EMAIL("/^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$/",
			// "*email*"),
			// IP("/^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/","*ip*"),
	EMOTICON_POSITIVE("[=:;xX8]['-]*[\\)dD\\]}]+|[\\(\\[{]+['-]*[=:;xX8]",
			"*emoji_pos*"), EMOTICON_NEGATIVE("[=:;xX8]['-]*[\\(\\[{]+|[\\)dD\\]}]+['-]*[=:;xX8]", "*emoji_neg*");

	private String regex;
	private String token;
	private Pattern pattern;

	PattersTokenizer(String regex, String token) {
		this.regex = regex;
		this.token = token;
		this.pattern = Pattern.compile(regex);
	}

	public String getRegex() {
		return regex;
	}

	public String getToken() {
		return token;
	}

	public Matcher matcher(String input) {
		return pattern.matcher(input);
	}

	public static List<String> allKeys() {
		List<String> list = new ArrayList<String>();
		
		for(PattersTokenizer patt:values()){
			list.add(patt.getToken());
		}
		return list;
	}

}
