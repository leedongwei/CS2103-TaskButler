// @author A0097722X
package butler.logic.command;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import butler.common.Task;
import butler.logic.taskmgr.TaskManager;

public class CheckCommand extends ReversibleCommand {
	
	private TaskManager manager;
	private Task task;

	public CheckCommand(Action commandAction,
			Stack<ReversibleCommand> historyStack, TaskManager manager,
			Task task) {
		super(commandAction, historyStack);
		this.manager = manager;
		this.task = task;
	}

	@Override
	public Result execute() {
		Task markTask = manager.markTaskAsFinished(task);
		List<Task> listOfResults = new Vector<Task>();
		listOfResults.add(markTask);
		Result result = new Result(Action.MARK_AS_FINISHED, listOfResults);
		pushToHistoryStack();
		return result;
	}

	@Override
	public Result undo() {
		Task tempTask = manager.markTaskAsUnfinished(task);
		List<Task> listOfResults = new Vector<Task>();
		listOfResults.add(tempTask);
		Result result = new Result(Action.UNDO, listOfResults);
		return result;
	}
	
	@Override
	public boolean equals(Command command) {
		CheckCommand checkCommand = (CheckCommand) command;
		boolean equals = false;
		if (this.task.getName().equals(checkCommand.task.getName()))
			if (this.task.getTaskType() == checkCommand.task.getTaskType())
				if (this.task.getStartTime() != null && checkCommand.task.getPeriod().getStartTime() != null) {
					if (this.task.getStartTime().equals(checkCommand.task.getPeriod().getStartTime())	
							&& this.task.getEndTime().equals(checkCommand.task.getPeriod().getEndTime()))
						equals = true;
				} else	if (this.task.getPeriod().getStartTime() == null && checkCommand.task.getPeriod().getStartTime() == null) {
					if (this.task.getPeriod().getEndTime() != null){
						if (this.task.getPeriod().getEndTime().equals(checkCommand.task.getPeriod().getEndTime()))
							equals = true;
					}else if (this.task.getPeriod().getEndTime() == null && checkCommand.task.getPeriod().getEndTime() == null)
					equals = true;

				}
		return equals;
	}

}
