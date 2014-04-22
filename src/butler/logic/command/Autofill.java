// @author A0097836L
package butler.logic.command;

import butler.common.Task;
import butler.logic.state.DisplayListType;
import butler.logic.state.DisplayState;

/**
 * The Autofill class provides the function to generate and complete an update
 * command so that users do not need to enter all task details again when they
 * update a task.
 */
public class Autofill {

	private static final String MESSAGE_INVALID_TASK_CODE = "You selected an invalid task code to update.";

	private static final String WHITESPACE_PATTERN = "\\s+";
	private static final String EMPTY_STRING = "";
	private static final char SPACE_CHAR = ' ';

	private static final int VALID_TOKENS_LENGTH = 2;
	private static final int ACTION_IDENTIFIER_INDEX = 0;
	private static final int TASK_CODE_INDEX = 1;
	private static final int TASK_CODE_NUM_INDEX = 1;

	private static final int PREFIX_CHAR_INDEX = 0;

	private DisplayState state;

	public Autofill(DisplayState state) {
		this.state = state;
	}

	/**
	 * Completes the specified command string so that users do not need to enter
	 * all task details.
	 * 
	 * @param commandString - the partial command string that the user typed
	 * @return the completed command string if the string can be completed, else
	 *         the original command string
	 * @throws Exception if the task code extracted from the command string is
	 *             invalid
	 */
	public String complete(String commandString) throws Exception {

		String[] tokens = commandString.split(WHITESPACE_PATTERN);

		if (!isValidLength(tokens) || !isLastCharSpace(commandString)
				|| !isUpdateAction(tokens)) {
			return commandString;
		}

		String taskDetail = getTaskDetail(tokens[TASK_CODE_INDEX]);

		return buildCompletedCommandString(tokens, taskDetail);
	}

	/**
	 * Checks that the current command string tokens contains only 2 tokens
	 * (action identifier and task code).
	 * 
	 * @param cmdStrTokens - a tokenized array of the current command string
	 * @return true if <code>commandString</code> contains only 2 tokens
	 */
	private boolean isValidLength(String[] cmdStrTokens) {

		boolean isValidLength = (cmdStrTokens.length == VALID_TOKENS_LENGTH);

		return isValidLength;
	}

	/**
	 * Checks that the last character the current command string is a space.
	 * This helps to ensure that the autofill function is activated after a
	 * space is entered.
	 * 
	 * @param commandString - the partial command string that the user typed
	 * @return true if the last character of <code>commandString</code> is a
	 *         space
	 */
	private boolean isLastCharSpace(String commandString) {

		int lastIndex = commandString.length() - 1;
		boolean isLastCharSpace = (commandString.charAt(lastIndex) == SPACE_CHAR);

		return isLastCharSpace;
	}

	/**
	 * Checks that the action identifier is Action.UPDATE.
	 * 
	 * @param cmdStrTokens - a tokenized array of the current command string
	 * @return true if the command action is Action.UPDATE
	 */
	private boolean isUpdateAction(String[] cmdStrTokens) {

		assert cmdStrTokens.length == VALID_TOKENS_LENGTH;

		String identifier = cmdStrTokens[ACTION_IDENTIFIER_INDEX];
		Action action = Keywords.resolveActionIdentifier(identifier);
		boolean isUpdateAction = (action == Action.UPDATE);

		return isUpdateAction;
	}

	/**
	 * Returns the details of the task associated with the specified task code
	 * that is currently showing in the display list.
	 * 
	 * @param taskCode
	 * @return the task details in command format form
	 * @throws Exception if the task code is invalid
	 */
	private String getTaskDetail(String taskCode) throws Exception {

		try {
			String prefix = String.valueOf(taskCode.charAt(PREFIX_CHAR_INDEX));
			DisplayListType type = DisplayListType.fromPrefix(prefix);
			String taskCodeNum = taskCode.substring(TASK_CODE_NUM_INDEX);
			int index = Integer.parseInt(taskCodeNum) - 1;

			Task task = state.getTaskInDisplayList(type, index);
			String details = buildTaskDetailString(task);

			return details;

		} catch (Exception ex) {
			throw new Exception(MESSAGE_INVALID_TASK_CODE);
		}
	}

	private String buildTaskDetailString(Task task) {

		switch (task.getTaskType()) {
			case NORMAL:
				return buildNormalTaskDetailString(task);

			case DEADLINE:
				return buildDeadlineTaskDetailString(task);

			case FLOATING:
				return buildFloatingTaskDetailString(task);
		}

		return EMPTY_STRING;
	}

	private String buildNormalTaskDetailString(Task task) {

		StringBuilder detail = new StringBuilder();

		detail.append(task.getName() + SPACE_CHAR);
		detail.append(Keywords.getDateIdentifier() + SPACE_CHAR);
		detail.append(task.getPeriod().getEarliestDateString());

		if (!task.isFullDay()) {
			detail.append(SPACE_CHAR + Keywords.getTimeIdentifier());
			detail.append(SPACE_CHAR + task.getPeriod().toTimeString());
		}

		return detail.toString();
	}

	private String buildDeadlineTaskDetailString(Task task) {

		StringBuilder detail = new StringBuilder();

		detail.append(task.getName() + SPACE_CHAR);
		detail.append(Keywords.getDueDateIdentifier() + SPACE_CHAR);
		detail.append(task.getPeriod().getEarliestDateString() + SPACE_CHAR);
		detail.append(Keywords.getTimeIdentifier() + SPACE_CHAR);
		detail.append(task.getPeriod().toTimeString());

		return detail.toString();
	}

	private String buildFloatingTaskDetailString(Task task) {
		return task.getName();
	}

	private String buildCompletedCommandString(String[] tokens,
			String taskDetail) {
		return String.format("%s %s %s", tokens[ACTION_IDENTIFIER_INDEX],
				tokens[TASK_CODE_INDEX], taskDetail);
	}

}
