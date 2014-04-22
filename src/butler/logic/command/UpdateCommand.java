// @author A0097722X
package butler.logic.command;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import butler.common.Task;
import butler.common.TaskType;
import butler.common.TimeSpan;
import butler.logic.taskmgr.TaskManager;

public class UpdateCommand extends ReversibleCommand {
	
	private TaskManager manager;
	private String taskName;
	private TimeSpan timeSpan;
	private TaskType taskType;
	private Task task;
	private Task oldTaskCopy;
	
	public UpdateCommand(Action commandAction,
			Stack<ReversibleCommand> historyStack, TaskManager manager,
			String taskName, TimeSpan timeSpan, TaskType taskType, Task task) {
		super(commandAction, historyStack);
		this.manager = manager;
		this.taskName = taskName;
		this.timeSpan = timeSpan;
		this.taskType = taskType;
		this.task = task;
	}

	@Override
	public Result execute() {
		this.oldTaskCopy = new Task(this.task.getTaskType(),
				this.task.getName(), this.task.getPeriod(),
				this.task.isFinished());
		Task UpdatedTask = manager.updateTask(task, taskName, timeSpan,
				taskType);
		List<Task> listOfResults = new Vector<Task>();
		listOfResults.add(UpdatedTask);
		Result result = new Result(Action.UPDATE, listOfResults);
		pushToHistoryStack();
		return result;
	}

	@Override
	public Result undo() {
		Task UpdatedTask = manager.updateTask(this.task, oldTaskCopy.getName(),
				oldTaskCopy.getPeriod(), oldTaskCopy.getTaskType());
		List<Task> listOfResults = new Vector<Task>();
		listOfResults.add(UpdatedTask);
		Result result = new Result(Action.UNDO, listOfResults);
		return result;
	}
	
	@Override
	public boolean equals(Command command) {
		UpdateCommand updateCommand = (UpdateCommand) command;
		boolean equals = false;
		if (this.taskName.equals(updateCommand.taskName))
			if (this.taskType == updateCommand.taskType)
				if (this.timeSpan.getStartTime() != null && updateCommand.timeSpan.getStartTime() != null) {
					if (this.timeSpan.getStartTime().equals(updateCommand.timeSpan.getStartTime())	
							&& this.timeSpan.getEndTime().equals(updateCommand.timeSpan.getEndTime()))
						equals = true;
				} else	if (this.timeSpan.getStartTime() == null && updateCommand.timeSpan.getStartTime() == null) {
					if (this.timeSpan.getEndTime() != null){
						if (this.timeSpan.getEndTime().equals(updateCommand.timeSpan.getEndTime()))
							equals = true;
					}else if (this.timeSpan.getEndTime() == null && updateCommand.timeSpan.getEndTime() == null)
					equals = true;

				}
		return equals;
	}


}
