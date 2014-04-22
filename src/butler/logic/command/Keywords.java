// @author A0097836L
package butler.logic.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The Keywords class contains all command identifier constants that the
 * application uses. Any processing of command identifier should go through this
 * class and make use of its methods.
 */
public final class Keywords {

	private static final String ADD_TASK_IDENTIFIER_1 = "schedule";
	private static final String ADD_TASK_IDENTIFIER_2 = "add";
	private static final String ADD_TASK_IDENTIFIER_3 = "new";

	private static final String UPDATE_TASK_IDENTIFIER_1 = "edit";
	private static final String UPDATE_TASK_IDENTIFIER_2 = "update";
	private static final String UPDATE_TASK_IDENTIFIER_3 = "change";

	private static final String DELETE_TASK_IDENTIFIER_1 = "delete";
	private static final String DELETE_TASK_IDENTIFIER_2 = "remove";
	private static final String DELETE_TASK_IDENTIFIER_3 = "forget";

	private static final String LIST_TASK_IDENTIFIER_1 = "list";
	private static final String LIST_TASK_IDENTIFIER_2 = "display";
	private static final String LIST_TASK_IDENTIFIER_3 = "show";

	private static final String SEARCH_TASK_IDENTIFIER_1 = "search";
	private static final String SEARCH_TASK_IDENTIFIER_2 = "filter";
	private static final String SEARCH_TASK_IDENTIFIER_3 = "find";

	private static final String MARK_FINISHED_IDENTIFIER_1 = "finish";
	private static final String MARK_FINISHED_IDENTIFIER_2 = "check";
	private static final String MARK_FINISHED_IDENTIFIER_3 = "mark";

	private static final String MARK_UNFINISHED_IDENTIFIER_1 = "unfinish";
	private static final String MARK_UNFINISHED_IDENTIFIER_2 = "uncheck";
	private static final String MARK_UNFINISHED_IDENTIFIER_3 = "unmark";

	private static final String UNDO_ACTION_IDENTIFIER = "undo";

	private static final String DATE_IDENTIFIER = ".on";
	private static final String DUE_DATE_IDENTIFIER = ".by";
	private static final String TIME_IDENTIFIER = ".at";
	private static final String SEARCH_FINISHED_IDENTIFIER = ".done";

	private static final Map<String, Action> actionMap = createMap();

	private static Map<String, Action> createMap() {

		Map<String, Action> map = new HashMap<String, Action>();

		map.put(ADD_TASK_IDENTIFIER_1, Action.ADD);
		map.put(ADD_TASK_IDENTIFIER_2, Action.ADD);
		map.put(ADD_TASK_IDENTIFIER_3, Action.ADD);

		map.put(UPDATE_TASK_IDENTIFIER_1, Action.UPDATE);
		map.put(UPDATE_TASK_IDENTIFIER_2, Action.UPDATE);
		map.put(UPDATE_TASK_IDENTIFIER_3, Action.UPDATE);

		map.put(DELETE_TASK_IDENTIFIER_1, Action.DELETE);
		map.put(DELETE_TASK_IDENTIFIER_2, Action.DELETE);
		map.put(DELETE_TASK_IDENTIFIER_3, Action.DELETE);

		map.put(LIST_TASK_IDENTIFIER_1, Action.LIST);
		map.put(LIST_TASK_IDENTIFIER_2, Action.LIST);
		map.put(LIST_TASK_IDENTIFIER_3, Action.LIST);

		map.put(SEARCH_TASK_IDENTIFIER_1, Action.SEARCH);
		map.put(SEARCH_TASK_IDENTIFIER_2, Action.SEARCH);
		map.put(SEARCH_TASK_IDENTIFIER_3, Action.SEARCH);

		map.put(MARK_FINISHED_IDENTIFIER_1, Action.MARK_AS_FINISHED);
		map.put(MARK_FINISHED_IDENTIFIER_2, Action.MARK_AS_FINISHED);
		map.put(MARK_FINISHED_IDENTIFIER_3, Action.MARK_AS_FINISHED);

		map.put(MARK_UNFINISHED_IDENTIFIER_1, Action.MARK_AS_UNFINISHED);
		map.put(MARK_UNFINISHED_IDENTIFIER_2, Action.MARK_AS_UNFINISHED);
		map.put(MARK_UNFINISHED_IDENTIFIER_3, Action.MARK_AS_UNFINISHED);

		map.put(UNDO_ACTION_IDENTIFIER, Action.UNDO);

		return Collections.unmodifiableMap(map);
	}

	/**
	 * This is used when you need to resolve a string into an Action
	 * enumeration.
	 * 
	 * @param identifier - a String object
	 * @return an Action enumeration that corresponds with the specified
	 *         identifier string
	 */
	public static Action resolveActionIdentifier(String identifier) {

		identifier = identifier.toLowerCase();

		if (!actionMap.containsKey(identifier)) {
			return Action.INVALID;
		}

		return actionMap.get(identifier);
	}

	public static String getAddTaskIdentifier() {
		return ADD_TASK_IDENTIFIER_1;
	}

	public static String getUpdateTaskIdentifier() {
		return UPDATE_TASK_IDENTIFIER_1;
	}

	public static String getDeleteTaskIdentifier() {
		return DELETE_TASK_IDENTIFIER_1;
	}

	public static String getListTaskIdentifier() {
		return LIST_TASK_IDENTIFIER_1;
	}

	public static String getSearchTaskIdentifier() {
		return SEARCH_TASK_IDENTIFIER_1;
	}

	public static String getMarkAsFinishedIdentifier() {
		return MARK_FINISHED_IDENTIFIER_1;
	}

	public static String getMarkAsUnfinishedIdentifier() {
		return MARK_UNFINISHED_IDENTIFIER_1;
	}

	public static String getUndoActionIdentifier() {
		return UNDO_ACTION_IDENTIFIER;
	}
	
	public static String getDateIdentifier() {
		return DATE_IDENTIFIER;
	}

	public static String getDueDateIdentifier() {
		return DUE_DATE_IDENTIFIER;
	}

	public static String getTimeIdentifier() {
		return TIME_IDENTIFIER;
	}
	
	public static String getSearchForFinishedIdentifier() {
		return SEARCH_FINISHED_IDENTIFIER;
	}

}
