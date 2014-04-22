// @author A0097722X
package butler.logic.command;

import butler.common.LoggerPreset;
import butler.common.Task;
import butler.common.TaskType;
import butler.common.TimeSpan;
import butler.common.TimeSpanHelper;
import butler.logic.state.DisplayListType;
import butler.logic.state.DisplayState;
import butler.logic.taskmgr.TaskManager;

import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class CommandParserImpl implements CommandParser {
	private static final Logger log = LoggerPreset.getLogger();
	
	private static int NUMBER_OF_SUBCOMMANDS = 2;
	private TaskManager manager;
	private DisplayState displayState;
	private Stack<ReversibleCommand> historyStack;

	public CommandParserImpl(TaskManager manager, DisplayState displayState){
		this.manager = manager;
		this.displayState = displayState;
		this.historyStack = new Stack<ReversibleCommand>();
	}
	
	@Override
	public Command parse(String commandString) throws Exception{
		return parse(commandString, false);
	}
	
	/**
	 * Parses the user input into a command object which can be executed by the system.
	 * 
	 * @param commandString - User input
	 * @param allowHiddenFlags - true if special commands are required by system to perform special commands not allowed to be entered by user
	 * 
	 * @return Command object based on task to be executed 
	 * @throws Exception if any values that user entered is not currently supported by the system. 
	 * The error message will be created by the individual methods call from this
	 */	

	@Override
	public Command parse(String commandString, boolean allowHiddenFlags) throws Exception{

		Command command = null;
		TaskType type = null;
		Action primaryCommandInput = getPrimaryCommand(commandString.trim());
		String eventDescription = "";
		String subCommand[][];
		Task task = null;
		TimeSpan timeSpan = null;
		Map<Integer, String> keyMap = new TreeMap<Integer, String>();
		
		type = extractKeyWords(keyMap, commandString);
		
		switch(primaryCommandInput){
			case ADD:		
				eventDescription = extractDescription(commandString, keyMap, Action.ADD);
				subCommand = populateSubCommand(commandString, keyMap);
				timeSpan = extractInterval(commandString, keyMap.size() - 1, Action.ADD, type, subCommand);					
				command = new AddCommand(Action.ADD, historyStack, manager, eventDescription, timeSpan, type);			
				break;
			case UPDATE:
				task = getTask(commandString);		
				eventDescription = extractDescription(commandString, keyMap, Action.UPDATE);
				subCommand = populateSubCommand(commandString, keyMap);
				timeSpan = extractInterval(commandString, keyMap.size() - 1, Action.UPDATE, type, subCommand);	
				command = new UpdateCommand(Action.UPDATE, historyStack, manager, eventDescription, timeSpan, type, task);
				break;
			case DELETE:
				task = getTask(commandString);		
				command = new DeleteCommand(Action.DELETE, historyStack, task, manager);
				break;
			case LIST:
				String inputPeriod = "today";				
				if(commandString.trim().indexOf(" ") != -1){
					inputPeriod = commandString.substring(commandString.indexOf(" ") + 1, commandString.length()).toLowerCase();		
				}				
				timeSpan = DateTimeParser.extractListDate(inputPeriod);
				command = new ListCommand(Action.LIST, timeSpan, manager);
				break;
			case SEARCH:
				String query = extractDescription(commandString, keyMap, Action.SEARCH);
				if(commandString.indexOf(".done") == -1){
					command = new SearchCommand(Action.SEARCH, manager, query,false);
				}else{
					query = query.substring(0, query.indexOf(".done")).trim();
					command = new SearchCommand(Action.SEARCH, manager, query,true);
				}
				break;
			case UNDO:
				if(historyStack.isEmpty()){
					throw new Exception("There are no commands to undo.");
				}else{
					command = new UndoCommand(Action.UNDO, historyStack);
				}
				break;
			case MARK_AS_FINISHED:
				task = getTask(commandString);		
				command = new CheckCommand(Action.MARK_AS_FINISHED, historyStack, manager, task);
				break;
			case MARK_AS_UNFINISHED:
				task = getTask(commandString);		
				command = new UnCheckCommand(Action.MARK_AS_UNFINISHED, historyStack, manager, task);
				break;
			default:
				log.log(Level.INFO, "Invalid Primary Command, UserInput: [{0}]", commandString);
				throw new Exception("Invalid command entered. Please enter a command type of \"schedule, list, etc.\"");
		}
		log.log(Level.INFO, "Command Created Type:[{0}], UserInput: [{1}]", new Object[] {command.getClass().getSimpleName(), commandString});
		return command;
		//return null;
	}
	
	/**
	 * Calculates the interval for the date and time requested by the user
	 * 
	 * @param commandString - User input
	 * @param numOfCommands - the number of sub Commands the user can enter. Currently is 2, .on or .by and .at
	 * @param action - Current Action type
	 * @param type - Task type of NORMAL, DEADLINE or FLOAT
	 * @param subCommand - 2D array containing the sub Commands entered by the user, and the values for each sub command
	 * 
	 * @return TimeSpan of the interval specified by the user
	 * @throws Exception if Date and time that user entered cannot be understood based on current specified formats
	 */	
	private TimeSpan extractInterval(String commandString, int numOfCommands, Action action, TaskType type, String[][] subCommand) throws Exception{
		TimeSpan timeSpan = null;
		LocalDate startDate = null;
		LocalDate endDate = null;
		LocalTime startTime = null;
		LocalTime endTime = null;
		boolean hasAtCommand = false;
		String inputTime = "";
		
		if(type.equals(TaskType.FLOATING)){
			return TimeSpanHelper.createTimeSpan(null, null);
		}
		
		for(int i=0;i < numOfCommands; i++){							//Loops through all subCommands entered by user. 
			inputTime = subCommand[i][0].trim();						//this allows the user to enter subCommands without fixed sequenced
			if(subCommand[i][1].equals("on")){							//e.g. .on before .at or .at before .on
				startDate = DateTimeParser.extractDate(inputTime);
				endDate = DateTimeParser.extractDate(inputTime);
			}else if(subCommand[i][1].equals("by")){
				endDate = DateTimeParser.extractDate(inputTime);
			}
			if(subCommand[i][1].equals("at")){
				if(inputTime.indexOf("-") == -1){
					if(type.equals(TaskType.DEADLINE)){
						endTime = DateTimeParser.extractTime(inputTime);
					}else{
						startTime = DateTimeParser.extractTime(inputTime);
						endTime = startTime.plusHours(1);
					}
				}else{
					String start = inputTime.substring(0, inputTime.indexOf("-"));
					String end = inputTime.substring(inputTime.indexOf("-") + 1, inputTime.length());
					startTime = DateTimeParser.extractTime(start);
					endTime = DateTimeParser.extractTime(end);
				}
				hasAtCommand = true;
			}
		}
		
		if(!hasAtCommand){											//append time into date if At command is present
			startTime = DateTimeParser.extractTime("00.00");		//time cannot be appended inside for loop as we do not know if a .on command is present
			if(type.equals(TaskType.DEADLINE)){
				endTime = DateTimeParser.extractTime("23.59");
			}else{
				endTime = DateTimeParser.extractTime("00.00");
				endDate = startDate.plusDays(1);
			}
			if(startDate == null){
				startDate = endDate;
			}		
		}else{
			if(startDate == null & !type.equals(TaskType.DEADLINE)){
				startDate = new LocalDate();
				endDate = new LocalDate();
			}
		}
	
	
		if(type.equals(TaskType.DEADLINE)){
			timeSpan = new TimeSpan(null, endDate.toDateTime(endTime).toDate());
		}else if(type.equals(TaskType.NORMAL)){
			if(endTime.compareTo(startTime) < 0){
				endDate = endDate.plusDays(1);
			}
			timeSpan = new TimeSpan(startDate.toDateTime(startTime).toDate(), endDate.toDateTime(endTime).toDate());
		}
		
		
		return timeSpan;
	}
	
	/**
	 * Returns a event description extracted from the user input
	 * 
	 * @param commandString - User input
	 * @param keyMap - Map containing the index of each sub command
	 * @param action - Current Action type
	 * 
	 * @return the event description
	 * @throws Exception if <code>commandString</code> is empty
	 */
	private String extractDescription(String commandString, Map<Integer, String> keyMap, Action action) throws Exception{
		Entry<Integer, String> entry = keyMap.entrySet().iterator().next();
		int firstStringPosition = entry.getKey();
		String eventDescription = "";

		if(commandString.indexOf(" ") != -1){
			if(action.equals(Action.ADD) || action.equals(Action.SEARCH)){
				eventDescription = commandString.substring(commandString.indexOf(" "), firstStringPosition).trim();
			}else if(action.equals(Action.UPDATE)){
				eventDescription = commandString.substring(commandString.indexOf(" ", commandString.indexOf(" ") + 1), firstStringPosition).trim();
			}
		}
		if(eventDescription.length() == 0){					
			log.log(Level.INFO, "Invalid Event Description(name), UserInput: [{0}]", commandString);
			throw new Exception("Event Description cannot be empty.");
		}

		return eventDescription;
	}
	
	/**
	 * creates a 2D array consisting of the different sub commands entered by user
	 * 
	 * @param commandString - User input
	 * @param keyMap - Map containing the index of each sub command
	 * 
	 * @return array of sub commands
	 */	
	private String[][] populateSubCommand(String commandString, Map<Integer, String> keyMap){
		Entry<Integer, String> entry = keyMap.entrySet().iterator().next();

		int firstStringPosition = entry.getKey();
		String firstCommand = entry.getValue();

		String subCommand[][] = new String[NUMBER_OF_SUBCOMMANDS][NUMBER_OF_SUBCOMMANDS] ;
		String currentCommand = firstCommand;
		int count = 0, tempValue = 0, storedPreviousValue = 0;

		for(Entry<Integer, String> entryIn : keyMap.entrySet()) {
			if(count != 0){
				tempValue = entryIn.getKey();

				if (count == 1){
					subCommand[count - 1][0] = commandString.substring(firstStringPosition + currentCommand.length() + 2, tempValue);
				}else{
					subCommand[count - 1][0] = commandString.substring(storedPreviousValue + currentCommand.length() + 2,tempValue);
				}
				subCommand[count - 1][1] = currentCommand;
				currentCommand = entryIn.getValue();
				storedPreviousValue = tempValue;
			}
			count++;
		}
		
		return subCommand;
	}
	
	/**
	 * Requests the actual ID of the task from DisplayState.
	 * ID entered by User is what is shown on display but not the actual ID
	 * 
	 * @param commandString - User input
	 * 
	 * @return Task object which is entered by user
	 * @throws Exception if Task ID is invalid, or if Task ID is in the correct format.
	 */	
	private Task getTask(String commandString) throws Exception{
		String prefix = "";
		int taskId;
		try{
			int firstSpacePos = commandString.indexOf(" ") + 1;
			String inputId = "";
			if(commandString.indexOf(" ", firstSpacePos) == -1){
				inputId= commandString.substring(firstSpacePos, commandString.length()).toLowerCase();
			}else {
				inputId = commandString.substring(firstSpacePos, commandString.indexOf(" ", firstSpacePos)).toLowerCase();
			}
			taskId = Integer.parseInt(inputId.substring(1, inputId.length())) - 1;
			prefix = String.valueOf(inputId.charAt(0));
		}catch(Exception ex){
			log.log(Level.INFO, "Invalid Task ID Entered, UserInput: [{0}]", commandString);
			throw new Exception("Invalid Task ID value entered. Please use T[num],D[num], or F[num]");
		}

		Task task = displayState.getTaskInDisplayList(DisplayListType.fromPrefix(prefix), taskId);
		if(task == null){
			log.log(Level.INFO, "Task ID cannot be found, UserInput: [{0}]", commandString);
			throw new Exception("Task ID entered cannot be found. Please try again.");
		}
		return task;
	}	
	
	private Action getPrimaryCommand(String commandString){

		String identifier = "";
		if(commandString.indexOf(" ") == -1){
			identifier = commandString;
		}else {
			identifier= commandString.substring(0, commandString.indexOf(" "));
		}

		return Keywords.resolveActionIdentifier(identifier);
		
	}
	
	private TaskType extractKeyWords(Map<Integer, String> keyMap, String commandString){
		TaskType type = null;
		keyMap.put(commandString.length(), "end");
		type = TaskType.FLOATING;
		
		if(commandString.indexOf(".on") != -1){
			keyMap.put(commandString.indexOf(".on"), "on");
			type = TaskType.NORMAL;
			
		}else if(commandString.indexOf(".by") != -1){
			keyMap.put(commandString.indexOf(".by"), "by");
			type = TaskType.DEADLINE;
		}
		if(commandString.indexOf(".at") != -1 && commandString.indexOf(".by") == -1 ){
			keyMap.put(commandString.indexOf(".at"), "at");
			type = TaskType.NORMAL;
		}else if(commandString.indexOf(".at") != -1){
			keyMap.put(commandString.indexOf(".at"), "at");
			type = TaskType.DEADLINE;
		}
		return type;
	}
}
