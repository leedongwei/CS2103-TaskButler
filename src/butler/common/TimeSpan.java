// @author A0097836L
package butler.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDateTime;

public class TimeSpan implements Comparable<TimeSpan> {

	public static final int COMPARE_TO_EQUAL = 0;

	private static final int MIDNIGHT_HOUR = 0;
	private static final int MIDNIGHT_MINUTE = 0;
	private static final int ONE_DAY = 1;

	private static final String DATE_FORMAT = "dd/MM/yy";
	private static final String TIME_FORMAT = "HH.mm";
	private static final String EMPTY_STRING = "";
	private static final int START_POSITION = 0;
	private static final String SPAN_CHAR = "-";

	private Date startTime;
	private Date endTime;

	public TimeSpan() {
		this(null, null);
	}

	public TimeSpan(Date startTime, Date endTime) {

		if (startTime != null && endTime != null) {
			assert startTime.before(endTime);
		}

		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * Returns true if both the start time of this TimeSpan object is later than
	 * or the same as the start time of the specified TimeSpan object, and the
	 * start time of this TimeSpan object is earlier than the end time of the
	 * specified TimeSpan object.
	 * 
	 * @param timeSpan - a time period
	 * @return true if the start time is this TimeSpan object is within the
	 *         period spanned by the specified TimeSpan object
	 */
	public boolean isStartTimeWithin(TimeSpan timeSpan) {
		
		assert timeSpan != null;

		if (startTime == null) {
			return false;
		}

		return isTimeWithin(startTime, timeSpan);
	}

	/**
	 * Returns true if both the end time of this TimeSpan object is later than
	 * or the same as the start time of the specified TimeSpan object, and the
	 * end time of this TimeSpan object is earlier than the end time of the
	 * specified TimeSpan object.
	 * 
	 * @param timeSpan - a time period
	 * @return true if the start time is this TimeSpan object is within the
	 *         period spanned by the specified TimeSpan object
	 */
	public boolean isEndTimeWithin(TimeSpan timeSpan) {
		return isTimeWithin(endTime, timeSpan);
	}

	/**
	 * Returns true if <code>time</code> is later than or the same as the start
	 * time of the specified TimeSpan object, and <code>time</code> is earlier
	 * than the end time of the specified TimeSpan object.
	 * 
	 * @param time - a Date object to be compared
	 * @param timeSpan - a time period
	 * @return true if <code>time</code> is within the period spanned by the
	 *         specified TimeSpan object
	 */
	private boolean isTimeWithin(Date time, TimeSpan timeSpan) {
		
		assert timeSpan != null;

		boolean isWithin = isTimeLaterOrEquals(time, timeSpan.startTime)
				&& isTimeEarlier(time, timeSpan.endTime);

		if (!isWithin) {
			return false;
		}

		return true;
	}

	/**
	 * Returns true if <code>thisTime</code> is later than or the same as
	 * <code>thatTime</code>.
	 * 
	 * @param thisTime - the first Date object to be compared
	 * @param thatTime - the second Date object to be compared
	 * @return true if <code>thisTime</code> is later than or the same as
	 *         <code>thatTime</code>
	 */
	private boolean isTimeLaterOrEquals(Date thisTime, Date thatTime) {
		return (thisTime.after(thatTime) || thisTime.equals(thatTime));
	}

	/**
	 * Returns true if <code>thisTime</code> is earlier than
	 * <code>thatTime</code>.
	 * 
	 * @param thisTime - the first Date object to be compared
	 * @param thatTime - the second Date object to be compared
	 * @return true if <code>thisTime</code> is earlier than
	 *         <code>thatTime</code>
	 */
	private boolean isTimeEarlier(Date thisTime, Date thatTime) {
		return thisTime.before(thatTime);
	}

	/**
	 * Returns true if the start time and the end time of this TimeSpan object
	 * is the same as the start time and the end time of the TimeSpan object
	 * created using:
	 * 
	 * <pre>
	 * TimeSpanFactory.createDaySpan(new Date());
	 * </pre>
	 * 
	 * @return true if this TimeSpan object is equal to the TimeSpan object
	 *         representing today
	 */
	public boolean equalsToday() {

		if (startTime == null || endTime == null) {
			return false;
		}

		TimeSpan today = TimeSpanHelper.createDaySpan(getNowTime());
		boolean isEqualToday = startTime.equals(today.startTime)
				&& endTime.equals(today.endTime);

		return isEqualToday;
	}

	/**
	 * Returns true if this TimeSpan object represents a full day interval. A
	 * full day interval have a start time of 00:00 and an end time of 00:00 the
	 * following day.
	 * 
	 * @return true if this TimeSpan object represents a full day interval
	 */
	public boolean isFullDayInterval() {

		if (startTime == null || endTime == null) {
			return false;
		}

		return isStartingAtMidnight() && isEndingOneDayLater();
	}

	private boolean isStartingAtMidnight() {

		LocalDateTime time = LocalDateTime.fromDateFields(startTime);

		return (time.getHourOfDay() == MIDNIGHT_HOUR)
				&& (time.getMinuteOfHour() == MIDNIGHT_MINUTE);
	}

	private boolean isEndingOneDayLater() {

		LocalDateTime time = LocalDateTime.fromDateFields(startTime);
		time = time.plusDays(ONE_DAY);

		return time.toDate().equals(endTime);
	}

	public boolean isSpanningMultipleDays() {

		if (startTime == null || endTime == null) {
			return false;
		}

		LocalDateTime time = LocalDateTime.fromDateFields(startTime);
		time = time.plusDays(ONE_DAY);

		return time.toDate().before(endTime);
	}

	protected Date getNowTime() {
		return new Date();
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * Returns the earliest date time string that is contained in this time
	 * span.
	 */
	public String getEarliestDateString() {

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

		if (startTime != null) {
			return sdf.format(startTime);
		}

		if (endTime != null) {
			return sdf.format(endTime);
		}

		return EMPTY_STRING;
	}

	public String toTimeString() {

		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
		StringBuilder timeStr = new StringBuilder();
		String startTimeStr = EMPTY_STRING;
		String endTimeStr = EMPTY_STRING;

		if (getEndTime() != null) {
			endTimeStr = sdf.format(getEndTime());
			timeStr.append(endTimeStr);
		}

		if (getStartTime() != null) {
			startTimeStr = sdf.format(getStartTime());
			timeStr.insert(START_POSITION, startTimeStr + SPAN_CHAR);
		}

		return timeStr.toString();
	}

	@Override
	public String toString() {

		StringBuilder str = new StringBuilder();

		if (startTime != null) {
			str.append(startTime.toString() + " ");
		}

		if (endTime != null) {
			str.append("to " + endTime.toString());
		}

		return str.toString();
	}

	@Override
	public int compareTo(TimeSpan timeSpan) {

		if (this.startTime != null && timeSpan.startTime != null) {
			return this.startTime.compareTo(timeSpan.startTime);

		} else if (this.endTime != null && timeSpan.endTime != null) {
			return this.endTime.compareTo(timeSpan.endTime);

		} else {
			return COMPARE_TO_EQUAL;
		}
	}

}
