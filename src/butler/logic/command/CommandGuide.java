// @author A0097836L
package butler.logic.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * The CommandGuide class provides quick help functionalities by parsing the
 * partial command string entered by the user and give appropriate help and
 * tips.
 */
public class CommandGuide {

	private static final String GENERAL_GUIDE = buildGeneralGuideString();
	private static final String ADD_GUIDE = buildAddGuideString();
	private static final String UPDATE_GUIDE = buildUpdateGuideString();
	private static final String DELETE_GUIDE = buildDeleteGuideString();
	private static final String LIST_GUIDE = buildListGuideString();
	private static final String SEARCH_GUIDE = buildSearchGuideString();
	private static final String MARK_FINISHED_GUIDE = buildMarkFinishedGuideString();
	private static final String MARK_UNFINISHED_GUIDE = buildMarkUnfinishedGuideString();
	private static final String UNDO_GUIDE = buildUndoGuideString();
	
	private static final String NEAR_DATE_TIP = "Tip: Use '.on' to enter your date and add as a normal task.";	
	private static final String NEAR_DUE_DATE_TIP = "Tip: Use '.by' to enter your due date and add as a deadline task.";	
	private static final String NEAR_TIME_TIP = "Tip: Use '.at' to enter your task's time interval.";
	private static final String BLANK_TIP = " ";
	
	private static final String NEAR_DATE_IDENTIFIER = " on ";
	private static final String NEAR_DUE_DATE_IDENTIFIER = " by ";
	private static final String NEAR_TIME_IDENTIFIER = " at ";

	private static final String HTML_OPEN = "<html>";
	private static final String HTML_CLOSE = "</html>";
	private static final String HTML_BREAK = "<br>";
	private static final String HTML_UNDERLINE_OPEN = "<u>";
	private static final String HTML_UNDERLINE_CLOSE = "</u>";
	private static final String TAG_WRAP_STRING = "%s%s%s";

	private static final String DATE_FORMAT = "dd/MM/yy";
	private static final String WHITESPACE_PATTERN = "\\s+";
	private static final String IDENTIFIER_PLACEHOLDER = "%1$s";
	private static final int NOT_FOUND = -1;
	private static final int MIN_TOKENS_LENGTH = 1;
	private static final int ACTION_IDENTIFIER_INDEX = 0;

	public CommandGuide() {

	}

	/**
	 * Returns appropriate tips to the user when the command string is detected
	 * that the user might want to enter date or time.
	 */
	public String getTipMessage(String cmdStr) {
		
		Entry<Integer, String> nearMatchEntry = getLastNearMatchEntry(cmdStr);
		boolean hasNearMatch = (nearMatchEntry.getKey() != NOT_FOUND);
		
		if (!hasNearMatch) {
			return BLANK_TIP;
		}
		
		String entry = nearMatchEntry.getValue();
		return selectTipMessage(cmdStr, entry);
	}
	
	private Entry<Integer, String> getLastNearMatchEntry(String cmdStr) {
		
		TreeMap<Integer, String> map = new TreeMap<Integer, String>();
		
		map.put(cmdStr.lastIndexOf(NEAR_DATE_IDENTIFIER), NEAR_DATE_IDENTIFIER);
		map.put(cmdStr.lastIndexOf(NEAR_DUE_DATE_IDENTIFIER), NEAR_DUE_DATE_IDENTIFIER);
		map.put(cmdStr.lastIndexOf(NEAR_TIME_IDENTIFIER), NEAR_TIME_IDENTIFIER);
		
		return map.lastEntry();
	}
	
	private String selectTipMessage(String cmdStr, String entry) {
		
		boolean hasDateIdentifier =
				cmdStr.indexOf(Keywords.getDateIdentifier()) != NOT_FOUND;
		boolean hasDueDateIdentifier =
				cmdStr.indexOf(Keywords.getDueDateIdentifier()) != NOT_FOUND;
		boolean hasTimeIdentifier =
				cmdStr.indexOf(Keywords.getTimeIdentifier()) != NOT_FOUND;
		
		if (!hasDateIdentifier && entry.equals(NEAR_DATE_IDENTIFIER)) {
			return NEAR_DATE_TIP;
			
		} else if (!hasDueDateIdentifier && entry.equals(NEAR_DUE_DATE_IDENTIFIER)) {
			return NEAR_DUE_DATE_TIP;
			
		} else if (!hasTimeIdentifier && entry.equals(NEAR_TIME_IDENTIFIER)) {
			return NEAR_TIME_TIP;
			
		} else {
			return BLANK_TIP;
		}
	}
	
	/**
	 * Returns appropriate guide message to the user when the user enters the
	 * action identifier.
	 */
	public String getGuideMessage(String commandString) {

		/* Check that there is at least 1 token */
		String[] tokens = commandString.split(WHITESPACE_PATTERN);
		boolean isValidLength = (tokens.length >= MIN_TOKENS_LENGTH);

		if (!isValidLength) {
			return GENERAL_GUIDE;
		}

		String identifier = tokens[ACTION_IDENTIFIER_INDEX];
		String message = buildGuideMessage(identifier);

		return message;
	}

	private String buildGuideMessage(String identifier) {

		identifier = identifier.toLowerCase();
		Action action = Keywords.resolveActionIdentifier(identifier);

		switch (action) {
			case ADD:
				return String.format(ADD_GUIDE, identifier);

			case UPDATE:
				return String.format(UPDATE_GUIDE, identifier);

			case DELETE:
				return String.format(DELETE_GUIDE, identifier);

			case LIST:
				return String.format(LIST_GUIDE, identifier);

			case SEARCH:
				return String.format(SEARCH_GUIDE, identifier);

			case MARK_AS_FINISHED:
				return String.format(MARK_FINISHED_GUIDE, identifier);

			case MARK_AS_UNFINISHED:
				return String.format(MARK_UNFINISHED_GUIDE, identifier);

			case UNDO:
				return String.format(UNDO_GUIDE, identifier);

			default:
				return GENERAL_GUIDE;
		}
	}

	private static String buildGeneralGuideString() {

		StringBuilder str = new StringBuilder();

		str.append("Type any of the following to begin:");
		str.append(HTML_BREAK);
		str.append(Keywords.getAddTaskIdentifier() + " | ");
		str.append(Keywords.getUpdateTaskIdentifier() + " | ");
		str.append(Keywords.getDeleteTaskIdentifier() + " | ");
		str.append(Keywords.getListTaskIdentifier() + " | ");
		str.append(Keywords.getSearchTaskIdentifier() + " | ");
		str.append(Keywords.getMarkAsFinishedIdentifier() + " | ");
		str.append(Keywords.getMarkAsUnfinishedIdentifier() + " | ");
		str.append(Keywords.getUndoActionIdentifier());

		return wrapWithHtmlTag(str.toString());
	}

	private static String buildAddGuideString() {

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String date = sdf.format(new Date());
		StringBuilder str = new StringBuilder();

		str.append("To schedule a task, type:");
		str.append(HTML_BREAK);

		str.append("- Normal Task: ");
		str.append(underline(IDENTIFIER_PLACEHOLDER) + " meet client ");
		str.append(underline(Keywords.getDateIdentifier()) + " " + date + " ");
		str.append(underline(Keywords.getTimeIdentifier()) + " 14.00-16.30");
		str.append(HTML_BREAK);

		str.append("- Deadline Task: ");
		str.append(underline(IDENTIFIER_PLACEHOLDER) + " submit proposal ");
		str.append(underline(Keywords.getDueDateIdentifier()) + " " + date
				+ " ");
		str.append(underline(Keywords.getTimeIdentifier()) + " 10.00");
		str.append(HTML_BREAK);

		str.append("- Floating Task: ");
		str.append(underline(IDENTIFIER_PLACEHOLDER) + " learn cooking");

		return wrapWithHtmlTag(str.toString());
	}

	private static String buildUpdateGuideString() {

		StringBuilder str = new StringBuilder();

		str.append("To edit a task, ");
		str.append("select its number code and type:" + HTML_BREAK);
		str.append(underline(IDENTIFIER_PLACEHOLDER)
				+ " t1 (followed by a space)");

		return wrapWithHtmlTag(str.toString());
	}

	private static String buildDeleteGuideString() {

		StringBuilder str = new StringBuilder();

		str.append("To delete a task, ");
		str.append("select its number code and type:" + HTML_BREAK);
		str.append(underline(IDENTIFIER_PLACEHOLDER) + " t1");

		return wrapWithHtmlTag(str.toString());
	}

	private static String buildListGuideString() {

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String date = sdf.format(new Date());
		StringBuilder str = new StringBuilder();

		str.append("To list tasks for today, type:" + HTML_BREAK);
		str.append(underline(IDENTIFIER_PLACEHOLDER));
		str.append(HTML_BREAK);

		str.append("To list tasks for a certain day, type:" + HTML_BREAK);
		str.append(underline(IDENTIFIER_PLACEHOLDER) + " " + date);

		return wrapWithHtmlTag(str.toString());
	}

	private static String buildSearchGuideString() {

		StringBuilder str = new StringBuilder();

		str.append("To search for unfinished tasks, type:" + HTML_BREAK);
		str.append(underline(IDENTIFIER_PLACEHOLDER) + " grocery milk");
		str.append(HTML_BREAK);

		str.append("To search for finished tasks, type:" + HTML_BREAK);
		str.append(underline(IDENTIFIER_PLACEHOLDER) + " grocery milk "
				+ underline(Keywords.getSearchForFinishedIdentifier()));

		return wrapWithHtmlTag(str.toString());
	}

	private static String buildMarkFinishedGuideString() {

		StringBuilder str = new StringBuilder();

		str.append("To mark a task as finished, ");
		str.append("select its number code and type:" + HTML_BREAK);
		str.append(underline(IDENTIFIER_PLACEHOLDER) + " t1");

		return wrapWithHtmlTag(str.toString());
	}

	private static String buildMarkUnfinishedGuideString() {

		StringBuilder str = new StringBuilder();

		str.append("To mark a task as unfinished, ");
		str.append("select its number code and type:" + HTML_BREAK);
		str.append(underline(IDENTIFIER_PLACEHOLDER) + " t1");

		return wrapWithHtmlTag(str.toString());
	}

	private static String buildUndoGuideString() {

		StringBuilder str = new StringBuilder();

		str.append("To undo your previous action, type:" + HTML_BREAK);
		str.append(underline(IDENTIFIER_PLACEHOLDER));

		return wrapWithHtmlTag(str.toString());
	}

	private static String wrapWithHtmlTag(String text) {
		return String.format(TAG_WRAP_STRING, HTML_OPEN, text, HTML_CLOSE);
	}

	private static String underline(String text) {
		return String.format(TAG_WRAP_STRING, HTML_UNDERLINE_OPEN, text,
				HTML_UNDERLINE_CLOSE);
	}

}
