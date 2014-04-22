// @author A0097722X
package butler.logic.command;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import butler.common.Task;
import butler.logic.taskmgr.TaskManager;

public class DeleteCommand extends ReversibleCommand {

	private Task task;
	private TaskManager manager;

	public DeleteCommand(Action commandAction,
			Stack<ReversibleCommand> historyStack, Task task,
			TaskManager manager) {
		super(commandAction, historyStack);
		this.task = task;
		this.manager = manager;
	}

	@Override
	public Result execute() {
		Task temptask = manager.deleteTask(task);
		List<Task> listOfResults = new Vector<Task>();
		listOfResults.add(temptask);
		Result result = new Result(Action.DELETE, listOfResults);
		this.task = temptask;
		pushToHistoryStack();
		return result;
	}

	@Override
	public Result undo() {

		Task temptask = manager.undeleteTask(task); 
		List<Task> listOfResults = new Vector<Task>(); 
		listOfResults.add(temptask); 
		Result result = new Result(Action.UNDO, listOfResults); 
		this.task = temptask; 
		return result;
	}

	@Override
	public boolean equals(Command command) {
		DeleteCommand deleteCommand = (DeleteCommand) command;
		boolean equals = false;
		if (this.task.getName().equals(deleteCommand.task.getName()))
			if (this.task.getTaskType() == deleteCommand.task.getTaskType())
				if (this.task.getStartTime() != null && deleteCommand.task.getPeriod().getStartTime() != null) {
					if (this.task.getStartTime().equals(deleteCommand.task.getPeriod().getStartTime())	
							&& this.task.getEndTime().equals(deleteCommand.task.getPeriod().getEndTime()))
						equals = true;
				} else	if (this.task.getPeriod().getStartTime() == null && deleteCommand.task.getPeriod().getStartTime() == null) {
					if (this.task.getPeriod().getEndTime() != null){
						if (this.task.getPeriod().getEndTime().equals(deleteCommand.task.getPeriod().getEndTime()))
							equals = true;
					}else if (this.task.getPeriod().getEndTime() == null && deleteCommand.task.getPeriod().getEndTime() == null)
					equals = true;

				}
		return equals;
	}

}
