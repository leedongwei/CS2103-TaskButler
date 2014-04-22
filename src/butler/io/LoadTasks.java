// @author A0085419X
package butler.io;

import butler.common.Task;
import butler.common.TaskType;
import butler.common.TimeSpan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;


public class LoadTasks extends StorageOperation {

	/**
	 * Constructor
	 * 
	 * @param FILE_NAME		Saved file of stored tasks
	 */
	protected LoadTasks(String FILE_NAME) {
		fileName_ = FILE_NAME;
		taskList_ = new ArrayList<Task>();
		commandStrings_ = new ArrayList<String>();
	}

	/**
	 * Execute process to load tasks from text file
	 * 
	 * @return			List of tasks
	 */
	protected List<Task> execute(List<Task> nullList) {
		log.log(Level.INFO, "Started loading of tasks from text file");
		
		boolean isSuccessfulRead = this.readFromFile();

		if (isSuccessfulRead) {
			this.generateTaskList();
			log.log(Level.INFO, "Successful loading of tasks");
			return taskList_;
		} else {
			log.log(Level.INFO, "Unsuccessful reading of commandStrings");
			return taskList_;
		}
	}

	/**
	 * Reads from file and saves it line-by-line into commandList.
	 * 
	 * @return			True for successful operation
	 */
	private boolean readFromFile() {
		try {
			log.log(Level.INFO, "Read commandStrings from: [{0}]", fileName_);
			
			String command;
			File file = new File(fileName_);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			commandStrings_.clear();
			while ((command = br.readLine()) != null) {
				commandStrings_.add(command);
			}

			br.close();
			fr.close();
			return true;
		} catch (Exception e) {
			return false;
		} 
	}

	/**
	 * Takes Strings read from file and convert them into a list of
	 * Tasks objects.
	 */
	private void generateTaskList() {
		int numberOfTasks = commandStrings_.size();
		Task tempTask;

		for (int i = 0; i < numberOfTasks; i++) {
			tempTask = this.createTask(commandStrings_.get(i));
			taskList_.add(tempTask);
		}
	}

	/**
	 * Takes 1 single command string and use it to create a Task object. Breaks
	 * command into individual parameters required for task creation.
	 * 
	 * @param command		String read from file, contains parameters needed
	 * 						to create Task
	 * @return				The task created
	 */
	private Task createTask(String command) {
		String taskType, taskName, taskStartTime, taskEndTime, taskIsFinished;
		TaskType type;
		TimeSpan period;
		boolean isFinished;

		String[] commandComponents = command.split(CMD_SPLIT);
		taskType = commandComponents[0];
		taskName = commandComponents[1];
		taskIsFinished = commandComponents[2];
		taskStartTime = commandComponents[3];
		taskEndTime = commandComponents[4];

		type = getType(taskType);
		period = getPeriod(taskStartTime, taskEndTime);
		isFinished = getIsFinished(taskIsFinished);
		
		log.log(Level.INFO, "Convert commandString to Task: [{0}]", taskName);

		return new Task(type, taskName, period, isFinished);
	}

	/**
	 * Convert String into TaskType
	 * 
	 * @param taskType		String parameter from command 
	 * @return				A TaskType recognized by logic
	 */
	private TaskType getType(String taskType) {
		TaskType type;

		switch (taskType) {
		case "normal":
			type = TaskType.NORMAL;
			break;
		case "deadline":
			type = TaskType.DEADLINE;
			break;
		case "floating":
			type = TaskType.FLOATING;
			break;
		default:
			return null;			
		}
		
		return type;
	}

	/**
	 * Convert time parameters into TimeSpan
	 * 
	 * @param taskStartTime		String parameter from command 
	 * @param taskEndTime		String parameter from command 
	 * @return					TimeSpan of Task
	 */
	private TimeSpan getPeriod(String taskStartTime, String taskEndTime) {
		Date startTime = null, endTime = null;

		if (!taskStartTime.equals("null")) {
			startTime = new Date(Long.parseLong(taskStartTime));
		}
		if (!taskEndTime.equals("null")) {
			endTime = new Date(Long.parseLong(taskEndTime));
		}
		
		return new TimeSpan(startTime, endTime);
	}

	/**
	 * Converts isFinished parameters into boolean
	 * 
	 * @param taskIsFinished	0 or 1
	 * @return					false or true respectively
	 */
	private boolean getIsFinished(String taskIsFinished) {
		if (taskIsFinished.equals("0")) {
			return false;
		} else {
			return true;
		}
	}

}
