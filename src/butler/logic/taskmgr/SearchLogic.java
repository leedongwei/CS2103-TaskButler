// @author A0097836L
package butler.logic.taskmgr;

import java.util.Date;

import butler.common.Task;
import butler.common.TaskType;
import butler.common.TimeSpan;
import butler.common.TimeSpanHelper;

public class SearchLogic {

	private static final int UPCOMING_DEADLINES_DAY_SPAN = 14;
	private static final String WHITESPACE_PATTERN = "\\s+";
	private static final String EMPTY_STRING = "";

	public boolean isListResult(TimeSpan timeSpan, Task task) {

		assert task != null;
		assert task.getTaskType() != null;

		if (task.isDeleted()) {
			return false;
		}

		switch (task.getTaskType()) {
			case NORMAL:
				return isNormalListResult(timeSpan, task);

			case DEADLINE:
				return isDeadlineListResult(timeSpan, task);

			case FLOATING:
			default:
				return isFloatingListResult(task);
		}
	}

	private boolean isNormalListResult(TimeSpan timeSpan, Task task) {
		assert task.getTaskType() == TaskType.NORMAL;
		return task.getPeriod().isStartTimeWithin(timeSpan);
	}

	private boolean isDeadlineListResult(TimeSpan timeSpan, Task task) {

		assert task.getTaskType() == TaskType.DEADLINE;

		TimeSpan taskPeriod = task.getPeriod();

		if (timeSpan.equalsToday()) {
			return (task.isOverdue() || isTaskDueSoon(timeSpan, taskPeriod));

		} else {
			return taskPeriod.isEndTimeWithin(timeSpan);
		}
	}

	private boolean isTaskDueSoon(TimeSpan todayTimeSpan, TimeSpan taskPeriod) {

		Date today = todayTimeSpan.getStartTime();
		TimeSpan deadlineTimeSpan = TimeSpanHelper.createDaySpan(today,
				UPCOMING_DEADLINES_DAY_SPAN);
		boolean isDueSoon = taskPeriod.isEndTimeWithin(deadlineTimeSpan);
		
		return isDueSoon;
	}

	private boolean isFloatingListResult(Task task) {
		assert task.getTaskType() == TaskType.FLOATING;
		return !task.isFinished();
	}

	public boolean isSearchUnfinishedResult(String searchTerms, Task task) {

		assert task != null;

		if (task.isFinished()) {
			return false;
		}

		return isSearchResult(searchTerms, task);
	}

	public boolean isSearchFinishedResult(String searchTerms, Task task) {

		assert task != null;

		if (!task.isFinished()) {
			return false;
		}

		return isSearchResult(searchTerms, task);
	}

	public boolean isSearchResult(String searchTerms, Task task) {

		assert searchTerms != null;
		assert task != null;

		if (searchTerms.equals(EMPTY_STRING)) {
			return false;
		}

		if (task.isDeleted()) {
			return false;
		}

		String[] tokens = searchTerms.toLowerCase().split(WHITESPACE_PATTERN);
		boolean isResult = containsAllSearchTerms(task.getName(),tokens);

		return isResult;
	}

	/**
	 * Returns true if <code>text</code> contains all search terms in
	 * <code>tokens</code> array.
	 * 
	 * @param text - string to be searched
	 * @param tokens - an array of words to be searched against the text
	 * @return true if <code>text</code> contains all search terms in
	 *         <code>tokens</code> array
	 */
	private boolean containsAllSearchTerms(String text, String[] tokens) {

		assert text != null;
		assert tokens != null;

		boolean containsThisTerm = false;
		boolean containsAllTerms = true;
		text = text.toLowerCase();

		for (String term : tokens) {
			// if one word is not matched, then the text is not a result
			containsThisTerm = (text.indexOf(term) != -1);
			containsAllTerms = containsAllTerms && containsThisTerm;
		}

		return containsAllTerms;
	}

}
