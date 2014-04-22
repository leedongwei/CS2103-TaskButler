package butler.logic.state;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import butler.common.LoggerPreset;
import butler.common.Task;
import butler.common.TaskType;
import butler.common.TimeSpan;
import butler.common.TimeSpanHelper;
import butler.logic.taskmgr.SearchLogic;

/**
 * The DisplayState class
 */
public class DisplayState extends DisplayStateListenable {
	
	private static final Logger log = LoggerPreset.getLogger();
	
	private static final String MESSAGE_INVALID_TASK_CODE =
			"You selected an invalid task code.";

	private DisplayMode displayMode;
	private boolean hasDisplayModeChanged;
	private TimeSpan currTimeSpan;
	private String currSearchTerms;
	private boolean searchesFinishedTasks;

	private DisplayList<Task> normalList;
	private DisplayList<Task> deadlineList;
	private DisplayList<Task> floatingList;

	private SearchLogic search;

	public DisplayState() {

		displayMode = DisplayMode.LIST;
		hasDisplayModeChanged = false;
		currTimeSpan = TimeSpanHelper.createDaySpan(new Date());
		currSearchTerms = null;
		searchesFinishedTasks = false;

		normalList = new DisplayList<Task>();
		deadlineList = new DisplayList<Task>();
		floatingList = new DisplayList<Task>();

		search = new SearchLogic();
	}

	/**
	 * Retrieves the task that is displayed in the display list by specifying
	 * which display list to look from and the array list.
	 * 
	 * @param type - which display list to look from
	 * @param index - index of where the task is in the display list
	 * @return the task retrieved from display list
	 * @throws Exception if the index is out of bounds
	 */
	public Task getTaskInDisplayList(DisplayListType type, int index)
			throws Exception {

		assert index >= 0;

		try {
			return getDisplayList(type).get(index);

		} catch (IndexOutOfBoundsException ex) {
			log.info("User entered invalid task code");
			throw new Exception(MESSAGE_INVALID_TASK_CODE);
		}
	}

	/**
	 * Called by TaskManager to signal that the task is added.
	 * 
	 * @param task - the task added
	 */
	public void onAddTask(Task task) {
		if (isListResult(task) || isSearchResult(task)) {
			addTaskToDisplayList(task);
			sortDisplayLists();
			notifyListeners();
		}
	}
	
	/**
	 * Called by TaskManager to signal that the task is deleted.
	 * 
	 * @param task - the task deleted
	 */
	public void onDeleteTask(Task task) {
		removeTaskFromDisplayList(task);
		sortDisplayLists();
		notifyListeners();
	}

	/**
	 * Called by TaskManager to signal that the task has changes to its
	 * 'marked as finished' status.
	 * 
	 * @param task - the task changed
	 */
	public void onMarkTask(Task task) {

		if (!isListResult(task) && !isSearchResult(task)) {
			removeTaskFromDisplayList(task);
		}

		setDisplayListAsChanged(task);
		sortDisplayLists();
		notifyListeners();
	}

	/**
	 * Determines if the task should be included in the display list.
	 * 
	 * @param task - a Task object
	 * @return true if the task is a list result
	 */
	private boolean isListResult(Task task) {

		boolean isListResult = displayMode == DisplayMode.LIST
				&& search.isListResult(currTimeSpan, task);

		return isListResult;
	}

	/**
	 * Determines if the task should be included in the display list.
	 * 
	 * @param task - a Task object
	 * @return true if the task is a search result
	 */
	private boolean isSearchResult(Task task) {

		boolean isSearchFinishedResult = displayMode == DisplayMode.SEARCH
				&& searchesFinishedTasks
				&& search.isSearchFinishedResult(currSearchTerms, task);

		boolean isSearchUnfinishedResult = displayMode == DisplayMode.SEARCH
				&& !searchesFinishedTasks
				&& search.isSearchUnfinishedResult(currSearchTerms, task);

		boolean isSearchResult = isSearchFinishedResult
				|| isSearchUnfinishedResult;

		return isSearchResult;
	}

	/**
	 * Sets the display mode to LIST and re-populates the display list
	 * with the task list provided.
	 * 
	 * @param taskList - the list of tasks to be populated
	 * @param timeSpan - time range criteria
	 */
	public void onListTasks(List<Task> taskList, TimeSpan timeSpan) {

		assert timeSpan != null;

		displayMode = DisplayMode.LIST;
		hasDisplayModeChanged = true;
		currTimeSpan = timeSpan;
		currSearchTerms = null;
		populateDisplayLists(taskList);

		notifyListeners();
	}

	/**
	 * Sets the display mode to SEARCH and re-populates the display list
	 * with the task list provided.
	 * 
	 * @param taskList - the list of tasks to be populated
	 * @param searchTerms - search criteria
	 * @param isFinishedTasks - true if searching for finished tasks only
	 */
	public void onSearchTasks(List<Task> taskList, String searchTerms,
			boolean isFinishedTasks) {

		assert searchTerms != null;
		assert !searchTerms.trim().isEmpty();

		displayMode = DisplayMode.SEARCH;
		hasDisplayModeChanged = true;
		currTimeSpan = null;
		currSearchTerms = searchTerms;
		searchesFinishedTasks = isFinishedTasks;
		populateDisplayLists(taskList);

		notifyListeners();
	}

	private void populateDisplayLists(List<Task> taskList) {

		assert taskList != null;
		clearDisplayLists();

		for (Task task : taskList) {
			addTaskToDisplayList(task);
		}

		sortDisplayLists();
	}

	private void addTaskToDisplayList(Task task) {
		assert task != null;
		getDisplayList(task.getTaskType()).add(task);
	}

	private void setDisplayListAsChanged(Task task) {
		assert task != null;
		getDisplayList(task.getTaskType()).setAsChanged();
	}

	private void removeTaskFromDisplayList(Task task) {
		assert task != null;
		getDisplayList(task.getTaskType()).remove(task);
	}

	private void clearDisplayLists() {
		normalList.clear();
		deadlineList.clear();
		floatingList.clear();
	}

	private void sortDisplayLists() {
		Collections.sort(normalList);
		Collections.sort(deadlineList);
		Collections.sort(floatingList);
	}

	private DisplayList<Task> getDisplayList(DisplayListType type) {

		assert type != null;

		switch (type) {
			case NORMAL:
				return normalList;

			case DEADLINE:
				return deadlineList;

			case FLOATING:
			default:
				return floatingList;
		}
	}

	/**
	 * Determines the associated display list to be used with the specified
	 * task type.
	 */
	private DisplayList<Task> getDisplayList(TaskType type) {

		assert type != null;

		switch (type) {
			case NORMAL:
				return normalList;

			case DEADLINE:
				return deadlineList;

			case FLOATING:
			default:
				return floatingList;
		}
	}

	private void notifyListeners() {
		notifyDisplayModeChanged();
		notifyNormalListChanged();
		notifyDeadlineListChanged();
		notifyFloatingListChanged();
	}

	private void notifyDisplayModeChanged() {

		if (!hasDisplayModeChanged) {
			return;
		}
		
		log.log(Level.INFO, "Display mode changed to {0}", displayMode);

		hasDisplayModeChanged = false;
		notifyModeChanged(new DisplayModeChangedEvent(this, displayMode,
				currTimeSpan, currSearchTerms, searchesFinishedTasks));
	}

	private void notifyNormalListChanged() {

		if (!normalList.hasChanged()) {
			return;
		}
		
		log.fine("Normal list changed");

		normalList.resetChanged();
		notifyListChanged(new DisplayListChangedEvent(this,
				DisplayListType.NORMAL, normalList));
	}

	private void notifyDeadlineListChanged() {

		if (!deadlineList.hasChanged()) {
			return;
		}
		
		log.fine("Deadline list changed");

		deadlineList.resetChanged();
		notifyListChanged(new DisplayListChangedEvent(this,
				DisplayListType.DEADLINE, deadlineList));
	}

	private void notifyFloatingListChanged() {

		if (!floatingList.hasChanged()) {
			return;
		}
		
		log.fine("Floating list changed");

		floatingList.resetChanged();
		notifyListChanged(new DisplayListChangedEvent(this,
				DisplayListType.FLOATING, floatingList));
	}

}
