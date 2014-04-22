// @author A0097836L
package butler.logic.taskmgr;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import butler.common.Task;
import butler.common.TimeSpan;
import butler.logic.common.DateStub;
import butler.logic.common.TaskStub;
import butler.logic.common.TimeSpanStub;
import butler.logic.taskmgr.SearchLogic;

public class SearchLogicTest {

	private static TimeSpan today;
	private static TimeSpan todayTomorrow;
	private static SearchLogic search;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		setUpTodayTimeSpan();
		setUpTodayTomorrowTimeSpan();
		search = new SearchLogic();
	}

	private static void setUpTodayTimeSpan() {
		Date startTime = DateStub.getTodayAtMidnight().toDate();
		Date endTime = DateStub.getTodayAtMidnight().plusDays(1).toDate();
		today = new TimeSpanStub(startTime, endTime);
	}

	private static void setUpTodayTomorrowTimeSpan() {
		Date startTime = DateStub.getTodayAtMidnight().toDate();
		Date endTime = DateStub.getTodayAtMidnight().plusDays(2).toDate();
		todayTomorrow = new TimeSpanStub(startTime, endTime);
	}
	
	
	
	/**======================================================================
	 * isListResult() test cases for exceptional case. 
	 * ======================================================================*/
	
	/**
	 * Invalid case: Null time span.
	 * Assuming that -ea switch is off.
	 */
	@Test(expected = NullPointerException.class)
	public void isListResult_NullTimeSpan_ThrowsNullPointerException() {

		Task task = TaskStub.createNormalTaskForToday();
		task.setFinished(false);

		search.isListResult(null, task);
	}

	/**
	 * Invalid case: Null task.
	 * Assuming that -ea switch is off.
	 */
	@Test(expected = NullPointerException.class)
	public void isListResult_NullTask_ThrowsNullPointerException() {

		Task task = TaskStub.createNormalTaskForToday();
		task.setFinished(false);

		search.isListResult(today, null);
	}
	
	
	
	/**======================================================================
	 * isListResult() test cases for normal task. 
	 * ======================================================================*/

	/**
	 * Invalid case: Deleted Task.
	 * This is a case for 'deleted' partition.
	 */
	@Test
	public void isListResult_DeletedTask_ReturnFalse() {

		Task task = TaskStub.createNormalTaskForToday();
		task.setFinished(false);
		task.setDeleted(true);

		boolean result = search.isListResult(today, task);
		assertFalse(result);
	}

	/**
	 * Valid case: List today; Unfinished normal task on today.
	 * This is a boundary case for 'present time' partition
	 */
	@Test
	public void isListResult_TodayUnfinishedNormalTaskAndListToday_ReturnTrue() {

		Task task = TaskStub.createNormalTaskForToday();
		task.setFinished(false);

		boolean result = search.isListResult(today, task);
		assertTrue(result);
	}

	/**
	 * Valid case: List today to tomorrow; Finished normal task on tomorrow
	 * This is case for 'present time spanning multiple days' partition.
	 */
	@Test
	public void isListResult_TomorrowFinishedNormalTaskAndListTodayTomorrow_ReturnTrue() {

		Task task = TaskStub.createNormalTaskForTomorrow();
		task.setFinished(true);

		boolean result = search.isListResult(todayTomorrow, task);
		assertTrue(result);
	}
	
	/**
	 * Invalid case: List today; Task on tomorrow.
	 * This is a boundary case for 'future time' partition.
	 */
	@Test
	public void isListResult_TomorrowTaskAndListToday_ReturnFalse() {

		Task task = TaskStub.createNormalTaskForTomorrow();
		task.setFinished(false);

		boolean result = search.isListResult(today, task);
		assertFalse(result);
	}
	
	/**
	 * Invalid case: List today; Task on yesterday 23:59 but ends today.
	 * This is a boundary case for 'past time' partition.
	 */
	@Test
	public void isListResult_YesterdayTaskAndListToday_ReturnFalse() {

		Task task = TaskStub.createNormalTaskForYesterdayOverflowToday();
		task.setFinished(false);

		boolean result = search.isListResult(today, task);
		assertFalse(result);
	}
	
	
	
	/**======================================================================
	 * isListResult() test cases for deadline task. 
	 * ======================================================================*/

	/**
	 * Valid case: List today; Unfinished deadline task due yesterday.
	 * This is a boundary case for unfinished deadline task in 'past time'
	 * partition.
	 */
	@Test
	public void isListResult_OverdueUnfinishedDeadlineTaskAndListToday_ReturnTrue() {

		Task task = TaskStub.createDeadlineTaskDueYesterday();
		task.setFinished(false);

		boolean result = search.isListResult(today, task);
		assertTrue(result);
	}

	/**
	 * Invalid case: List today to tomorrow; Unfinished deadline task due
	 * yesterday.
	 * This is a boundary case for unfinished deadline task in 'past time
	 * with listing multiple days' partition.
	 */
	@Test
	public void isListResult_OverdueUnfinishedDeadlineTaskAndListTodayTomorrow_ReturnFalse() {

		Task task = TaskStub.createDeadlineTaskDueYesterday();
		task.setFinished(false);

		boolean result = search.isListResult(todayTomorrow, task);
		assertFalse(result);
	}

	/**
	 * Invalid case: List today; Finished deadline task due yesterday.
	 * This is a boundary case for finished deadline task in 'past time' partition.
	 */
	@Test
	public void isListResult_OverdueFinishedDeadlineTaskAndListToday_ReturnFalse() {

		Task task = TaskStub.createDeadlineTaskDueYesterday();
		task.setFinished(true);

		boolean result = search.isListResult(today, task);
		assertFalse(result);
	}

	/**
	 * Valid case: List today; Finished deadline task due today.
	 * This is a boundary case for deadline task in 'present time' partition.
	 */
	@Test
	public void isListResult_DueTodayFinishedDeadlineTaskAndListToday_ReturnTrue() {

		Task task = TaskStub.createDeadlineTaskDueToday();
		task.setFinished(true);

		boolean result = search.isListResult(today, task);
		assertTrue(result);
	}

	/**
	 * Valid case: List today to tomorrow; Finished deadline task due tomorrow.
	 * This is a boundary case for deadline task in 'present time spanning
	 * multiple days' partition.
	 */
	@Test
	public void isListResult_DueTomorrowFinishedDeadlineTaskAndListTodayTomorrow_ReturnTrue() {

		Task task = TaskStub.createDeadlineTaskDueTomorrow();
		task.setFinished(true);

		boolean result = search.isListResult(todayTomorrow, task);
		assertTrue(result);
	}


	/**
	 * Invalid case: List today to tomorrow; Unfinished deadline task due the
	 * day after tomorrow.
	 * This is a boundary case for deadline task in 'future time with listing
	 * multiple days' partition.
	 */
	@Test
	public void isListResult_DueDayAfterTomorrowUnfinishedDeadlineTaskAndListTodayTomorrow_ReturnFalse() {

		Task task = TaskStub.createDeadlineTaskDueDayAfterTomorrow();
		task.setFinished(true);

		boolean result = search.isListResult(todayTomorrow, task);
		assertFalse(result);
	}

	/**
	 * Valid case: List today; Deadline task due just before 2 weeks.
	 * This is a boundary case for deadline task in 'present time' partition.
	 */
	@Test
	public void isListResult_DueBeforeTwoWeeksDeadlineTaskAndListToday_ReturnTrue() {

		Task task = TaskStub.createDeadlineTaskDueBeforeTwoWeeks();
		task.setFinished(false);

		boolean result = search.isListResult(today, task);
		assertTrue(result);
	}

	/**
	 * Valid case: List today; Deadline task due after 2 weeks.
	 * This is a boundary case for deadline task in 'future time' partition.
	 */
	@Test
	public void isListResult_DueAfterTwoWeeksDeadlineTaskAndListToday_ReturnFalse() {

		Task task = TaskStub.createDeadlineTaskDueAfterTwoWeeks();
		task.setFinished(false);

		boolean result = search.isListResult(today, task);
		assertFalse(result);
	}
	
	
	
	/**======================================================================
	 * isListResult() test cases for floating task. 
	 * ======================================================================*/
	
	/**
	 * Valid case: List today; Unfinished floating task.
	 */
	@Test
	public void isListResult_UnfinishedFloatingTaskAndListToday_ReturnTrue() {

		Task task = TaskStub.createFloatingTask();
		task.setFinished(false);

		boolean result = search.isListResult(today, task);
		assertTrue(result);
	}
	
	/**
	 * Valid case: List today to tomorrow; Unfinished floating task.
	 */
	@Test
	public void isListResult_UnfinishedFloatingTaskAndListTodayTomorrow_ReturnTrue() {

		Task task = TaskStub.createFloatingTask();
		task.setFinished(false);

		boolean result = search.isListResult(todayTomorrow, task);
		assertTrue(result);
	}
	
	/**
	 * Invalid case: List today; Finished floating task.
	 */
	@Test
	public void isListResult_FinishedFloatingTaskAndListToday_ReturnFalse() {

		Task task = TaskStub.createFloatingTask();
		task.setFinished(true);

		boolean result = search.isListResult(today, task);
		assertFalse(result);
	}
	
	/**
	 * Invalid case: List today to tomorrow; Finished floating task.
	 */
	@Test
	public void isListResult_FinishedFloatingTaskAndListTodayTomorrow_ReturnFalse() {

		Task task = TaskStub.createFloatingTask();
		task.setFinished(true);

		boolean result = search.isListResult(todayTomorrow, task);
		assertFalse(result);
	}

	
	
	/**======================================================================
	 * isSearchResult() test cases for exceptional cases.
	 * ======================================================================*/
	
	/**
	 * Invalid case: Null search terms.
	 * Assuming that -ea switch is off.
	 */
	@Test(expected = NullPointerException.class)
	public void isSearchResult_NullSearchTerms_ThrowsNullPointerException() {

		String searchTerms = null;
		Task task = TaskStub.createFloatingTask();
		task.setFinished(false);

		search.isSearchResult(searchTerms, task);
	}

	/**
	 * Invalid case: Empty search terms.
	 */
	@Test
	public void isSearchResult_EmptySearchTerms_ReturnFalse() {

		String searchTerms = "";
		Task task = TaskStub.createFloatingTask();
		task.setFinished(false);

		boolean result = search.isSearchResult(searchTerms, task);
		assertFalse(result);
	}

	/**
	 * Invalid case: Null task
	 * Assuming that -ea switch is off.
	 */
	@Test(expected = NullPointerException.class)
	public void isSearchResult_NullTask_ThrowsNullPointerException() {

		String searchTerms = "lunch at downtown";
		Task task = null;

		boolean result = search.isSearchResult(searchTerms, task);
		assertFalse(result);
	}
	
	/**
	 * Invalid case: Deleted task
	 */
	@Test
	public void isSearchResult_DeletedTask_ReturnsFalse() {
		
		String searchTerms = "downtown lunch";
		Task task = TaskStub.createNormalTaskForToday();
		task.setName("lunch at downtown");
		task.setFinished(false);
		task.setDeleted(true);

		boolean result = search.isSearchResult(searchTerms, task);
		assertFalse(result);
	}
	
	
	
	/**======================================================================
	 * isSearchUnfinishedResult() test cases.
	 * ======================================================================*/

	/**
	 * Valid case: Unfinished task; Contains all search terms.
	 */
	@Test
	public void isSearchUnfinishedResult_UnfinishedTaskContainingAllSearchTerms_ReturnTrue() {

		String searchTerms = "downtown lunch";
		Task task = TaskStub.createNormalTaskForToday();
		task.setName("lunch at downtown");
		task.setFinished(false);

		boolean result = search.isSearchUnfinishedResult(searchTerms, task);
		assertTrue(result);
	}

	/**
	 * Invalid case: Unfinished task; Does not contains all search terms.
	 */
	@Test
	public void isSearchUnfinishedResult_UnfinishedTaskNotContainingAllSearchTerms_ReturnFalse() {

		String searchTerms = "city lunch";
		Task task = TaskStub.createDeadlineTaskDueToday();
		task.setName("lunch at downtown");
		task.setFinished(false);

		boolean result = search.isSearchUnfinishedResult(searchTerms, task);
		assertFalse(result);
	}

	/**
	 * Invalid case: Finished task while searching under unfinished tasks.
	 */
	@Test
	public void isSearchUnfinishedResult_FinishedTask_ReturnFalse() {

		String searchTerms = "downtown lunch";
		Task task = TaskStub.createFloatingTask();
		task.setName("lunch at downtown");
		task.setFinished(true);

		boolean result = search.isSearchUnfinishedResult(searchTerms, task);
		assertFalse(result);
	}
	
	
	
	/**======================================================================
	 * isSearchFinishedResult() test cases.
	 * ======================================================================*/
	
	/**
	 * Valid case: Finished task; Contains all search terms.
	 */
	@Test
	public void isSearchFinishedResult_FinishedTaskContainsAllSearchTerms_ReturnTrue() {

		String searchTerms = "downtown lunch";
		Task task = TaskStub.createNormalTaskForToday();
		task.setName("lunch at downtown");
		task.setFinished(true);

		boolean result = search.isSearchFinishedResult(searchTerms, task);
		assertTrue(result);
	}

	/**
	 * Invalid case: Finished task; Does not contains all search terms.
	 */
	@Test
	public void isSearchFinishedResult_FinishedTaskNotContainsAllSearchTerms_ReturnFalse() {

		String searchTerms = "city lunch";
		Task task = TaskStub.createDeadlineTaskDueToday();
		task.setName("lunch at downtown");
		task.setFinished(true);

		boolean result = search.isSearchFinishedResult(searchTerms, task);
		assertFalse(result);
	}

	/**
	 * Invalid case: Unfinished task while searching under finished tasks.
	 */
	@Test
	public void isSearchFinishedResult_UnfinishedTask_ReturnFalse() {

		String searchTerms = "downtown lunch";
		Task task = TaskStub.createFloatingTask();
		task.setName("lunch at downtown");
		task.setFinished(false);

		boolean result = search.isSearchFinishedResult(searchTerms, task);
		assertFalse(result);
	}

}
