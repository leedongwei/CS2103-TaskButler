// @author A0097722X
package butler.logic.command;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import butler.common.Task;
import butler.logic.taskmgr.TaskManager;

public class UnCheckCommand extends ReversibleCommand {
	
	private TaskManager manager;
	private Task task;

	public UnCheckCommand(Action commandAction,
			Stack<ReversibleCommand> historyStack, TaskManager manager,
			Task task) {
		super(commandAction, historyStack);
		this.manager = manager;
		this.task = task;
	}

	@Override
	public Result execute() {
		Task markTask = manager.markTaskAsUnfinished(task);
		List<Task> listOfResults = new Vector<Task>();
		listOfResults.add(markTask);
		Result result = new Result(Action.MARK_AS_UNFINISHED, listOfResults);
		pushToHistoryStack();
		return result;
	}

	@Override
	public Result undo() {
		Task tempTask = manager.markTaskAsFinished(task);
		List<Task> listOfResults = new Vector<Task>();
		listOfResults.add(tempTask);
		Result result = new Result(Action.UNDO, listOfResults);
		return result;
	}
	

	@Override
	public boolean equals(Command command) {
		UnCheckCommand uncheckCommand = (UnCheckCommand) command;
		boolean equals = false;
		if (this.task.getName().equals(uncheckCommand.task.getName()))
			if (this.task.getTaskType() == uncheckCommand.task.getTaskType())
				if (this.task.getStartTime() != null && uncheckCommand.task.getPeriod().getStartTime() != null) {
					if (this.task.getStartTime().equals(uncheckCommand.task.getPeriod().getStartTime())	
							&& this.task.getEndTime().equals(uncheckCommand.task.getPeriod().getEndTime()))
						equals = true;
				} else	if (this.task.getPeriod().getStartTime() == null && uncheckCommand.task.getPeriod().getStartTime() == null) {
					if (this.task.getPeriod().getEndTime() != null){
						if (this.task.getPeriod().getEndTime().equals(uncheckCommand.task.getPeriod().getEndTime()))
							equals = true;
					}else if (this.task.getPeriod().getEndTime() == null && uncheckCommand.task.getPeriod().getEndTime() == null)
					equals = true;

				}
		return equals;
	}

}
