// @author A0097722X
package butler.logic.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import butler.common.LoggerPreset;
import butler.common.TimeSpan;

public class DateTimeParser {
	private static final Logger log = LoggerPreset.getLogger();
	private enum RelativeDatePhrase {
		TODAY, TOMORROW, YESTERDAY, WEEK,
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY,
		NEXT_MONDAY, NEXT_TUESDAY, NEXT_WEDNESDAY, NEXT_THURSDAY,
		NEXT_FRIDAY, NEXT_SATURDAY, NEXT_SUNDAY, NEXT_WEEK,
		INVALID;
	}

	private static final Map<String, RelativeDatePhrase> phraseMap = createMap();
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH.mm");
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("dd/MM/yy");

	public static LocalDate extractDate(String inputTime) throws Exception {
		return extractDate(null, inputTime);
	}

	public static LocalDate extractDate(Action action, String inputTime) throws Exception{

		LocalDate returnDate = new LocalDate();


		switch (parsePhrase(inputTime)) {
		case TODAY:
			return returnDate;		
		case TOMORROW:
			return returnDate.plusDays(1);
		case YESTERDAY:
			return returnDate.minusDays(1);
		case MONDAY:
			return returnDate.withDayOfWeek(DateTimeConstants.MONDAY);
		case TUESDAY:
			return returnDate.withDayOfWeek(DateTimeConstants.TUESDAY);
		case WEDNESDAY:
			return returnDate.withDayOfWeek(DateTimeConstants.WEDNESDAY);
		case THURSDAY:
			return returnDate.withDayOfWeek(DateTimeConstants.THURSDAY);
		case FRIDAY:
			return returnDate.withDayOfWeek(DateTimeConstants.FRIDAY);
		case SATURDAY:
			return returnDate.withDayOfWeek(DateTimeConstants.SATURDAY);
		case SUNDAY:
			return returnDate.withDayOfWeek(DateTimeConstants.SUNDAY);		
		case NEXT_MONDAY:
			return returnDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.MONDAY);
		case NEXT_TUESDAY:
			return returnDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.TUESDAY);
		case NEXT_WEDNESDAY:
			return returnDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.WEDNESDAY);
		case NEXT_THURSDAY:
			return returnDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.THURSDAY);
		case NEXT_FRIDAY:
			return returnDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.FRIDAY);
		case NEXT_SATURDAY:
			return returnDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.SATURDAY);
		case NEXT_SUNDAY:
			return returnDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.SUNDAY);
		case INVALID:
			try{
				returnDate = LocalDate.parse(inputTime, DATE_FORMAT);
			}catch(Exception ex){
				log.log(Level.INFO, "Invalid date format entered, UserInput: [{0}]", inputTime);
				throw new Exception("Invalid date format entered. Please use dd/MM/yy or \"today, etc.\"");
			}
			break;
		case NEXT_WEEK:
			log.log(Level.INFO, "Next Week not valid Date for scheduling, UserInput: [{0}]", inputTime);
			throw new Exception("Next Week is not a valid date for scheduling tasks. Please use dd/MM/yy or \"today, etc.\"");
		case WEEK:
			log.log(Level.INFO, "Week not valid Date for scheduling, UserInput: [{0}]", inputTime);
			throw new Exception("Week is not a valid date for scheduling tasks. Please use dd/MM/yy or \"today, etc.\"");
		}

		assert (returnDate != null);
		return returnDate;
	}

	public static TimeSpan extractListDate(String inputPeriod) throws Exception {
		LocalDate startDate = new LocalDate();
		LocalDate endDate = null;
		TimeSpan timeSpan = null;
		try{
			switch (parsePhrase(inputPeriod)) {
			case TODAY:
				break;
			case TOMORROW:
				startDate = startDate.plusDays(1);
				break;
			case YESTERDAY:
				startDate = startDate.minusDays(1);
				break;
			case MONDAY:
				startDate = startDate.withDayOfWeek(DateTimeConstants.MONDAY);
				break;
			case TUESDAY:
				startDate = startDate.withDayOfWeek(DateTimeConstants.TUESDAY);
				break;
			case WEDNESDAY:
				startDate = startDate.withDayOfWeek(DateTimeConstants.WEDNESDAY);
				break;
			case THURSDAY:
				startDate = startDate.withDayOfWeek(DateTimeConstants.THURSDAY);
				break;
			case FRIDAY:
				startDate = startDate.withDayOfWeek(DateTimeConstants.FRIDAY);
				break;
			case SATURDAY:
				startDate = startDate.withDayOfWeek(DateTimeConstants.SATURDAY);
				break;
			case SUNDAY:
				startDate = startDate.withDayOfWeek(DateTimeConstants.SUNDAY);	
				break;
			case WEEK:
				endDate = startDate.plusDays(7);
				break;
			case NEXT_MONDAY:
				startDate = startDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.MONDAY);
				break;
			case NEXT_TUESDAY:
				startDate = startDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.TUESDAY);
				break;
			case NEXT_WEDNESDAY:
				startDate = startDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.WEDNESDAY);
				break;
			case NEXT_THURSDAY:
				startDate = startDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.THURSDAY);
				break;
			case NEXT_FRIDAY:
				startDate = startDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.FRIDAY);
				break;
			case NEXT_SATURDAY:
				startDate = startDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.SATURDAY);
				break;
			case NEXT_SUNDAY:
				startDate = startDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.SUNDAY);
				break;
			case NEXT_WEEK:
				startDate = startDate.plusWeeks(1);
				endDate = startDate.plusDays(7);
				break;
			case INVALID:
				if(inputPeriod.indexOf("-") == -1){
					startDate = LocalDate.parse(inputPeriod, DATE_FORMAT);
				}else{
					startDate = LocalDate.parse(inputPeriod.substring(0, inputPeriod.indexOf("-")), DATE_FORMAT);
					endDate = LocalDate.parse(inputPeriod.substring(inputPeriod.indexOf("-") + 1,inputPeriod.length()), DATE_FORMAT);
					endDate = endDate.plusDays(1);
				}
				break;
			}
			if(endDate == null){
				endDate = startDate.plusDays(1);
			}
			timeSpan = new TimeSpan(startDate.toDate(),endDate.toDate()); 
		}catch(Exception ex){
			log.log(Level.INFO, "Invalid date format entered, UserInput: [{0}]", inputPeriod);
			throw new Exception("Invalid date format entered. Please use dd/MM/yy or \"today, etc.\"");
		}
		return timeSpan;
	}


	public static LocalTime extractTime(String inputTime) throws Exception{
		LocalTime returnTime = new LocalTime();
		try{
			returnTime = LocalTime.parse(inputTime, TIME_FORMAT);
		}catch (Exception ex){
			log.log(Level.INFO, "Invalid time format entered, UserInput: [{0}]", inputTime);
			throw new Exception("Invalid time format entered. Please use HH.mm");
		}
		return returnTime;
	}

	private static RelativeDatePhrase parsePhrase(String phrase) {

		phrase = phrase.toLowerCase();

		if (!phraseMap.containsKey(phrase)) {
			return RelativeDatePhrase.INVALID;
		}

		return phraseMap.get(phrase);
	}

	private static Map<String, RelativeDatePhrase> createMap() {

		Map<String, RelativeDatePhrase> map = new HashMap<String, RelativeDatePhrase>();

		map.put("today", RelativeDatePhrase.TODAY);
		map.put("tomorrow", RelativeDatePhrase.TOMORROW);
		map.put("yesterday", RelativeDatePhrase.YESTERDAY);
		map.put("week", RelativeDatePhrase.WEEK);

		map.put("mon", RelativeDatePhrase.MONDAY);
		map.put("tue", RelativeDatePhrase.TUESDAY);
		map.put("wed", RelativeDatePhrase.WEDNESDAY);
		map.put("thu", RelativeDatePhrase.THURSDAY);
		map.put("fri", RelativeDatePhrase.FRIDAY);
		map.put("sat", RelativeDatePhrase.SATURDAY);
		map.put("sun", RelativeDatePhrase.SUNDAY);

		map.put("monday", RelativeDatePhrase.MONDAY);
		map.put("tuesday", RelativeDatePhrase.TUESDAY);
		map.put("wednesday", RelativeDatePhrase.WEDNESDAY);
		map.put("thursday", RelativeDatePhrase.THURSDAY);
		map.put("friday", RelativeDatePhrase.FRIDAY);
		map.put("saturday", RelativeDatePhrase.SATURDAY);
		map.put("sunday", RelativeDatePhrase.SUNDAY);

		map.put("next mon", RelativeDatePhrase.NEXT_MONDAY);
		map.put("next tue", RelativeDatePhrase.NEXT_TUESDAY);
		map.put("next wed", RelativeDatePhrase.NEXT_WEDNESDAY);
		map.put("next thu", RelativeDatePhrase.NEXT_THURSDAY);
		map.put("next fri", RelativeDatePhrase.NEXT_FRIDAY);
		map.put("next sat", RelativeDatePhrase.NEXT_SATURDAY);
		map.put("next sun", RelativeDatePhrase.NEXT_SUNDAY);

		map.put("next monday", RelativeDatePhrase.NEXT_MONDAY);
		map.put("next tuesday", RelativeDatePhrase.NEXT_TUESDAY);
		map.put("next wednesday", RelativeDatePhrase.NEXT_WEDNESDAY);
		map.put("next thursday", RelativeDatePhrase.NEXT_THURSDAY);
		map.put("next friday", RelativeDatePhrase.NEXT_FRIDAY);
		map.put("next saturday", RelativeDatePhrase.NEXT_SATURDAY);
		map.put("next sunday", RelativeDatePhrase.NEXT_SUNDAY);
		map.put("next week", RelativeDatePhrase.NEXT_WEEK);

		return Collections.unmodifiableMap(map);
	}

}
