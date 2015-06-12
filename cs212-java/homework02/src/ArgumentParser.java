import java.util.HashMap;

/**
 * Class that handles parsing an array of arguments into flag/value pairs. A
 * flag is considered to be a non-null String that starts with a "-" dash
 * symbol. A value optionally follows a flag, and must not start with a "-" dash
 * symbol.
 * 
 * @ Jinpeng Bi (Koby) // TODO: PUT YOUR NAME HERE
 */
public class ArgumentParser {

	/** Stores flag/value pairs of arguments. */
	private HashMap<String, String> argumentMap;

	public ArgumentParser(String[] args) {
		argumentMap = new HashMap<String, String>();
		parseArgs(args);
	}

	/**
	 * Parses the provided array of arguments into flag/value pairs, storing the
	 * results in an internal map.
	 * 
	 * @param arguments
	 *            to parse
	 */
	private void parseArgs(String[] arguments) {
		// run only if arguments is not null
		if (arguments != null) {
			// set a temporary flag
			String tempFlag = null;
			// take all strings in arguments out
			for (String string : arguments) {
				// change all strings into lower case
				string = string.toLowerCase();
				// check whether the string is flag
				if (isFlag(string)) {
					tempFlag = string;
					argumentMap.put(tempFlag, null);
					// check whether the string is value
				} else if (isValue(string)) {
					if (hasFlag(tempFlag)) {
						if (!hasValue(tempFlag)) {
							argumentMap.put(tempFlag, string);
						}
					}
				}
			}
		}
	}

	/**
	 * Tests whether the provided String is a flag, i.e. whether the String is
	 * non-null, starts with a "-" dash, and has at least one character
	 * following the dash.
	 * 
	 * @param text
	 *            to test
	 * @return <code>true</code> if the text is non-null, start with the "-"
	 *         dash symbol, and has a flag name of at least one character
	 */
	public static boolean isFlag(String text) {
		if (text == null || text == ""
				|| text.toCharArray()[0] != ("-".toCharArray()[0])
				|| text.length() <= 1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Tests whether the provided String is a value, i.e. whether the String is
	 * non-null, non-empty, and does NOT start with a "-" dash.
	 * 
	 * @param text
	 *            to test
	 * @return <code>true</code> if the text is non-null, non-empty, and does
	 *         NOT start with the "-" dash symbol
	 */
	public static boolean isValue(String text) {
		if (text == null || text == ""
				|| text.toCharArray()[0] == ("-".toCharArray()[0])
				|| text.length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns the number of flags stored in the argument map.
	 * 
	 * @return number of flags stored in the argument map
	 */
	public int numFlags() {
		if (argumentMap.isEmpty()) {
			return 0;
		}
		return argumentMap.size();
	}

	/**
	 * Checks whether the provided flag exists in the argument map.
	 * 
	 * @param flag
	 *            to check for
	 * @return <code>true</code> if the flag exists
	 */
	public boolean hasFlag(String flag) {
		if (argumentMap.containsKey(flag)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the provided flag has an associated non-null value.
	 * Returns <code>false</code> if there is no value for the flag, or if the
	 * flag does not exist.
	 * 
	 * @param flag
	 *            to check for value
	 * @return <code>true</code> if the flag has a non-null value, or
	 *         <code>false</code> if the value or flag does not exist
	 */
	public boolean hasValue(String flag) {
		if (argumentMap.get(flag) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the value associated with a flag, or <code>null</code> if the
	 * flag does not exist, or the flag does not have an associated value.
	 * 
	 * @param flag
	 *            to fetch associated value
	 * @return value of the flag if it exists, or <code>null</code>
	 */
	public String getValue(String flag) {
		if (argumentMap.containsKey(flag) && hasValue(flag)) {
			return argumentMap.get(flag);
		}
		return null;
	}
}
