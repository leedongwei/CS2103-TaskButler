// @author A0097836L
package butler.logic.common;

import java.util.Date;

import butler.common.Task;
import butler.common.TaskType;
import butler.common.TimeSpan;

public class TaskStub extends Task {

	private static final String TASK_NAME = "TaskStub";
	
	private Date nowStub;

	/**
	 * Returns a normal task that starts from today 00:00 to tomorrow 00:00.
	 */
	public static Task createNormalTaskForToday() {

		Date startTime = DateStub.getTodayTaskStartTime().toDate();
		Date endTime = DateStub.getTodayTaskEndTime().toDate();

		return new TaskStub(TaskType.NORMAL, TASK_NAME, startTime, endTime);
	}

	/**
	 * Returns a normal task that starts from the tomorrow 00:00 to the day
	 * after tomorrow 00:00.
	 */
	public static Task createNormalTaskForTomorrow() {

		Date startTime = DateStub.getTodayTaskStartTime().plusDays(1).toDate();
		Date endTime = DateStub.getTodayTaskEndTime().plusDays(1).toDate();

		return new TaskStub(TaskType.NORMAL, TASK_NAME, startTime, endTime);
	}

	/**
	 * Returns a normal task that starts from the day after tomorrow 00:00 to
	 * 2 days after tomorrow 00:00.
	 */
	public static Task createNormalTaskForDayAfterTomorrow() {

		Date startTime = DateStub.getTodayTaskStartTime().plusDays(2).toDate();
		Date endTime = DateStub.getTodayTaskEndTime().plusDays(2).toDate();

		return new TaskStub(TaskType.NORMAL, TASK_NAME, startTime, endTime);
	}
	
	/**
	 * Returns a normal task that started from yesterday 00:00 to today 00:00.
	 */
	public static Task createNormalTaskForYesterday() {

		Date startTime = DateStub.getTodayTaskStartTime().minusDays(1).toDate();
		Date endTime = DateStub.getTodayTaskEndTime().minusDays(1).toDate();

		return new TaskStub(TaskType.NORMAL, TASK_NAME, startTime, endTime);
	}

	/**
	 * Returns a normal task that started from yesterday 23:59 to today 1:00.
	 */
	public static Task createNormalTaskForYesterdayOverflowToday() {

		Date startTime = DateStub.getTodayTaskStartTime().minusMinutes(1).toDate();
		Date endTime = DateStub.getTodayTaskStartTime().plusHours(1).toDate();

		return new TaskStub(TaskType.NORMAL, TASK_NAME, startTime, endTime);
	}
	
	/**
	 * Returns a deadline task that was due yesterday 23:59.
	 */
	public static Task createDeadlineTaskDueYesterday() {
		
		Date dueTime = DateStub.getTodayTaskStartTime().minusMinutes(1).toDate();

		return new TaskStub(TaskType.DEADLINE, TASK_NAME, null, dueTime);
	}
	
	/**
	 * Returns a deadline task that is due today 23:59.
	 */
	public static Task createDeadlineTaskDueToday() {

		Date dueTime = DateStub.getTodayTaskEndTime().minusMinutes(1).toDate();
		
		return new TaskStub(TaskType.DEADLINE, TASK_NAME, null, dueTime);
	}

	
	/**
	 * Returns a deadline task that is due tomorrow 23:59.
	 */
	public static Task createDeadlineTaskDueTomorrow() {

		Date dueTime = DateStub.getTodayTaskEndTime().plusDays(1).minusMinutes(1).toDate();
		
		return new TaskStub(TaskType.DEADLINE, TASK_NAME, null, dueTime);
	}

	
	/**
	 * Returns a deadline task that is due the day after tomorrow 23:59.
	 */
	public static Task createDeadlineTaskDueDayAfterTomorrow() {

		Date dueTime = DateStub.getTodayTaskEndTime().plusDays(2).minusMinutes(1).toDate();
		
		return new TaskStub(TaskType.DEADLINE, TASK_NAME, null, dueTime);
	}
	
	/**
	 * Returns a deadline task that is due a day before 2 weeks later 23:59.
	 */
	public static Task createDeadlineTaskDueBeforeTwoWeeks() {

		Date dueTime = DateStub.getTodayTaskStartTime().plusWeeks(2).minusMinutes(1).toDate();
		
		return new TaskStub(TaskType.DEADLINE, TASK_NAME, null, dueTime);
	}
	
	/**
	 * Returns a deadline task that is due 2 weeks later 00:00.
	 */
	public static Task createDeadlineTaskDueAfterTwoWeeks() {

		Date dueTime = DateStub.getTodayTaskStartTime().plusWeeks(2).toDate();
		
		return new TaskStub(TaskType.DEADLINE, TASK_NAME, null, dueTime);
	}
	
	/**
	 * Returns a floating task.
	 */
	public static Task createFloatingTask() {
		return new TaskStub(TaskType.FLOATING, TASK_NAME, null, null);
	}

	public TaskStub(TaskType taskType, String name) {
		this(taskType, name, null);
	}

	public TaskStub(TaskType taskType, String name, Date startTime, Date endTime) {
		this(taskType, name, new TimeSpan(startTime, endTime));
	}

	public TaskStub(TaskType taskType, String name, TimeSpan period) {
		this(taskType, name, period, false);
	}

	public TaskStub(TaskType taskType, String name, TimeSpan period,
			boolean isFinished) {
		super(taskType, name, period, isFinished);
		this.nowStub = DateStub.getNowTime();
	}

	@Override
	protected Date getNowTime() {
		assert nowStub != null;
		return nowStub;
	}

}
