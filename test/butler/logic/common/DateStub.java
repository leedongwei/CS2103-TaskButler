// @author A0097836L
package butler.logic.common;

import java.util.Date;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class DateStub {

	// We set the following as the current time
	public static final int YEAR = 2013;
	public static final int MONTH = DateTimeConstants.OCTOBER;
	public static final int DAY = 12;
	public static final int HOUR = 16;
	public static final int MINUTE = 35;

	// We set the task stub start time to start at midnight
	private static final int START_HOUR = 0;
	private static final int START_MINUTE = 0;
	
	private DateStub() {
		
	}

	public static Date getNowTime() {
		return new LocalDateTime(YEAR, MONTH, DAY, HOUR, MINUTE).toDate();
	}
	
	public static LocalDate getTodayAtMidnight() {
		return new LocalDate(YEAR, MONTH, DAY);
	}
	
	/**
	 * The task will start at midnight.
	 */
	public static LocalDateTime getTodayTaskStartTime() {
		return new LocalDateTime(YEAR, MONTH, DAY, START_HOUR, START_MINUTE);
	}
	
	/**
	 * The task will end at midnight a day after its start time.
	 */
	public static LocalDateTime getTodayTaskEndTime() {
		return getTodayTaskStartTime().plusDays(1);
	}
	
}
