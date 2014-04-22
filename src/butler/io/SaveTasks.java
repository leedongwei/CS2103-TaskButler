// @author A0085419x
package butler.io;

import butler.common.Task;
import butler.common.TaskType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class SaveTasks extends StorageOperation {

	/**
	 * Constructor
	 */
	protected SaveTasks(String FILE_NAME) {		
		fileName_ = FILE_NAME;
		commandStrings_ = new ArrayList<String>();
	}

	/**
	 * Executes process to save file
	 * 
	 * @param taskList		List of Tasks
	 * @return				Returns null as logic does not require
	 * 						confirmation of successful save
	 */
	protected List<Task> execute(List<Task> taskList) {
		taskList_ = taskList;
		this.recreateCommands();
		this.writeToFile();
		
		return null;
	}

	/**
	 * Parse through taskList one-by-one and recreate String command
	 * for each of them. Checks if task is marked for deletion.
	 * Adds undeleted tasks to commandList to prepare for writing. 
	 * 
	 * @param taskList
	 */
	private void recreateCommands() {
		int numberOfTasks = taskList_.size();
		String command;

		commandStrings_.clear();
		for (int i = 0; i < numberOfTasks; i++) {
			if (!taskList_.get(i).isDeleted()) {
				command = generateCommand(taskList_.get(i));
				commandStrings_.add(command);
			}
		}
	}

	/**
	 * Recreate the String command used to create a task.
	 * 
	 * @param task		1 task to be converted to String
	 * @return			String command used to create task
	 */
	private String generateCommand(Task task) {
		StringBuilder sb = new StringBuilder();
		
		// <taskType> <name> <isFinished> <startTime> <endTime>
		
		// task.taskType
		TaskType type = task.getTaskType();
		if (type.equals(TaskType.NORMAL)) {
			sb.append("normal");
		} else if (type.equals(TaskType.DEADLINE)) {
			sb.append("deadline");
		} else if (type.equals(TaskType.FLOATING)) {
			sb.append("floating");
		} else {
			sb.append("TaskType Error");
		}
		sb.append(CMD_END + WHITESPACE);
		
		// task.name
		sb.append(CMD_START);
		sb.append(task.getName());
		sb.append(CMD_END + WHITESPACE);
		
		// task.isFinished
		sb.append(CMD_START);
		if (task.isFinished()) {
			sb.append("1");
		} else {
			sb.append("0");
		}
		sb.append(CMD_END + WHITESPACE);
		
		// task.period (start time)
		sb.append(CMD_START);
		if (type.equals(TaskType.FLOATING) || type.equals(TaskType.DEADLINE)) {
			sb.append("null");
		} else {
			Long date = task.getStartTime().getTime();
			sb.append(String.valueOf(date));
		}
		sb.append(CMD_END + WHITESPACE);
		
		// task.period (end time)
		sb.append(CMD_START);
		if (type.equals(TaskType.FLOATING)) {
			sb.append("null");
		} else {
			sb.append(task.getEndTime().getTime());
		}
		
		log.log(Level.INFO, "Recreated commandString for: [{0}]", task.getName());
		
		return sb.toString();
	}

	/**
	 * Writes commandList to file.
	 * 1 task's command will occupy 1 line in file.
	 */
	private void writeToFile() {
		try {
			FileWriter fw = new FileWriter(fileName_);
			BufferedWriter bw = new BufferedWriter(fw);

			while (commandStrings_.size() != 0) {
				bw.write(commandStrings_.remove(0) + NEW_LINE);
			}

			bw.close();
			fw.close();
			
			log.log(Level.INFO, "Saved tasks to file: [{0}]", fileName_);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}