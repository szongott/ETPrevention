package de.unihannover.dcsec.eviltwin.prevention;

public class Utils {
	public static String trimQuotesFromString(String str) {
		if (str.startsWith("\"")) {
			str = str.substring(1);
		}
		if (str.endsWith("\"")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

}
