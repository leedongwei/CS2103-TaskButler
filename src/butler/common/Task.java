// @author A0097836L
package butler.common;

import java.util.Date;

public class Task implements Comparable<Task> {

	private TaskType taskType;
	private String name;
	private TimeSpan period;
	private boolean isFinished;
	private boolean isDeleted;

	public Task(TaskType taskType, String name) {
		this(taskType, name, null);
	}

	public Task(TaskType taskType, String name, Date startTime, Date endTime) {
		this(taskType, name, new TimeSpan(startTime, endTime));
	}

	public Task(TaskType taskType, String name, TimeSpan period) {
		this(taskType, name, period, false);
	}

	public Task(TaskType taskType, String name, TimeSpan period,
			boolean isFinished) {
		
		assert taskType != null;
		assert name != null;
		assert period != null;
		
		this.taskType = taskType;
		this.name = name;
		this.period = period;
		this.isFinished = isFinished;
		this.isDeleted = false;
	}

	/**
	 * Returns true if this task is overdue. An overdue task is a deadline task
	 * that is not finished and its end time is earlier than the time when this
	 * method is called.
	 * 
	 * @return true if this task is overdue
	 */
	public boolean isOverdue() {

		if (taskType != TaskType.DEADLINE) {
			return false;
		}

		Date now = getNowTime();
		boolean isOverdue = !isFinished && getEndTime().compareTo(now) < 0;

		return isOverdue;
	}

	/**
	 * Returns true if this task is a full day task. A full day task is a normal
	 * task that have a start time of 00:00 and an end time of 00:00 the
	 * following day.
	 * 
	 * @return true if this task is a full day task
	 */
	public boolean isFullDay() {
		return period.isFullDayInterval();
	}

	protected Date getNowTime() {
		return new Date();
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TimeSpan getPeriod() {
		return period;
	}

	public void setPeriod(TimeSpan period) {
		this.period = period;
	}

	public Date getStartTime() {

		if (period == null) {
			return null;
		}

		return period.getStartTime();
	}

	public Date getEndTime() {

		if (period == null) {
			return null;
		}

		return period.getEndTime();
	}

	public void setStartTime(Date startTime) {
		period.setStartTime(startTime);
	}

	public void setEndTime(Date endTime) {
		period.setEndTime(endTime);
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public int compareTo(Task task) {
		
		int result = this.period.compareTo(task.period);
		
		if (result == TimeSpan.COMPARE_TO_EQUAL) {
			result = this.name.compareTo(task.name);
		}
		
		return result;
	}

}
