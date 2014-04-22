// @author A0097836L
package butler.common;

import java.util.Date;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalDate;

/**
 * The TimeSpanHelper class aims to ease the creation of TimeSpan object with
 * special interval.
 */
public class TimeSpanHelper {

	private static final int ONE_DAY = 1;
	private static final int ONE_HOUR = 1;

	public static TimeSpan createTimeSpan(Date startTime) {
		return createTimeSpan(startTime, getAutoEndTime(startTime));
	}

	public static TimeSpan createTimeSpan(Date startTime, Date endTime) {
		return new TimeSpan(startTime, endTime);
	}

	/**
	 * Creates a TimeSpan object that starts from midnight of <code>date</code>
	 * till next day midnight.
	 * 
	 * @param date - the date
	 * @return a TimeSpan object
	 */
	public static TimeSpan createDaySpan(Date date) {
		return createDaySpan(date, date);
	}

	/**
	 * Creates a TimeSpan object that starts from midnight of
	 * <code>startDate</code> and ends the day after <code>endDate</code> at
	 * midnight.
	 * 
	 * @param startDate - start date
	 * @param daysSpanned - number of days to span
	 * @return a TimeSpan object
	 */
	public static TimeSpan createDaySpan(Date startDate, Date endDate) {

		LocalDate start = LocalDate.fromDateFields(startDate);
		LocalDate end = LocalDate.fromDateFields(startDate).plusDays(ONE_DAY);

		return createTimeSpan(start.toDate(), end.toDate());
	}

	/**
	 * Creates a TimeSpan object that starts from midnight of
	 * <code>startDate</code> and ends the number of days specified in
	 * <code>daysSpanned</code> later at midnight.
	 * 
	 * @param startDate - start date
	 * @param daysSpanned - number of days to span
	 * @return a TimeSpan object
	 */
	public static TimeSpan createDaySpan(Date startDate, int daysSpanned) {

		LocalDate start = LocalDate.fromDateFields(startDate);
		LocalDate end = start.plusDays(daysSpanned);

		return createTimeSpan(start.toDate(), end.toDate());
	}

	/**
	 * Returns an end time for the specified start time. The end time will be
	 * one hour after the start time.
	 * 
	 * @param startTime - start time
	 * @return an end time for the specified start time
	 */
	private static Date getAutoEndTime(Date startTime) {

		LocalDateTime start = LocalDateTime.fromDateFields(startTime);
		LocalDateTime end = start.plusHours(ONE_HOUR);

		return end.toDate();
	}

}
