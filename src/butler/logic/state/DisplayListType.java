// @author A0097836L
package butler.logic.state;

public enum DisplayListType {

	NORMAL("T"), DEADLINE("D"), FLOATING("F");

	private static final String MESSAGE_INVALID_PREFIX =
			"Prefix specified has no corresponding DisplayListType";
	
	private final String prefix;

	private DisplayListType(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	/**
	 * Returns a DisplayListType enumeration based on its prefix.
	 * 
	 * @param prefix - a string that is obtained from <code>getPrefix()</code>
	 *            method of a DisplayListType object
	 * @return a DisplayListType enumeration based on its prefix
	 * @throws IllegalArgumentException if <code>prefix</code> is neither equals
	 *             to <code>NORMAL.getPrefix()</code>,
	 *             <code>DEADLINE.getPrefix()</code>, nor
	 *             <code>FLOATING.getPrefix()</code>
	 */
	public static DisplayListType fromPrefix(String prefix) {
		
		assert prefix != null;

		if (prefix.equalsIgnoreCase(NORMAL.getPrefix())) {
			return NORMAL;
		}

		if (prefix.equalsIgnoreCase(DEADLINE.getPrefix())) {
			return DEADLINE;
		}

		if (prefix.equalsIgnoreCase(FLOATING.getPrefix())) {
			return FLOATING;
		}

		throw new IllegalArgumentException(MESSAGE_INVALID_PREFIX);
	}

}
