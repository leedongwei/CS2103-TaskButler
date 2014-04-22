// @author A0097722X
package butler.logic.command;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import butler.common.Task;
import butler.common.TaskType;
import butler.common.TimeSpan;
import butler.logic.taskmgr.TaskManager;

public class AddCommand extends ReversibleCommand {

	private TaskManager manager;
	private String taskName;
	private TimeSpan timeSpan;
	private TaskType taskType;
	private Task task;

	public AddCommand(Action commandAction,
			Stack<ReversibleCommand> historyStack, TaskManager manager,
			String taskName, TimeSpan timeSpan, TaskType taskType) {
		super(commandAction, historyStack);
		this.manager = manager;
		this.taskName = taskName;
		this.timeSpan = timeSpan;
		this.taskType = taskType;
	}

	@Override
	public Result execute() {
		Task addtask = manager.addTask(taskName, timeSpan, taskType);
		List<Task> listOfResults = new Vector<Task>();
		listOfResults.add(addtask);
		Result result = new Result(Action.ADD, listOfResults);
		this.task = addtask;
		pushToHistoryStack();
		return result;
	}

	@Override
	public Result undo() {
		Task temptask = manager.deleteTask(this.task);
		List<Task> listOfResults = new Vector<Task>();
		listOfResults.add(temptask);
		Result result = new Result(Action.UNDO, listOfResults);
		return result;
	}

	@Override
	public boolean equals(Command command) {
		AddCommand addCommand = (AddCommand) command;
		boolean equals = false;
		// System.out.println(this.taskName + " " + addCommand.taskName);
		if (this.taskName.equals(addCommand.taskName))
			if (this.taskType == addCommand.taskType)
				if (this.timeSpan.getStartTime() != null && addCommand.timeSpan.getStartTime() != null) {
					if (this.timeSpan.getStartTime().equals(addCommand.timeSpan.getStartTime())	
							&& this.timeSpan.getEndTime().equals(addCommand.timeSpan.getEndTime()))
						equals = true;
				} else	if (this.timeSpan.getStartTime() == null && addCommand.timeSpan.getStartTime() == null) {
					if (this.timeSpan.getEndTime() != null){
						if (this.timeSpan.getEndTime().equals(addCommand.timeSpan.getEndTime()))
							equals = true;
					}else if (this.timeSpan.getEndTime() == null && addCommand.timeSpan.getEndTime() == null)
					equals = true;

				}
		return equals;
	}

}
