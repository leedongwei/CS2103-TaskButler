// @author A0097722X
package butler.logic.command;

import java.util.List;
import butler.common.Task;
import butler.common.TimeSpan;
import butler.logic.taskmgr.TaskManager;

public class ListCommand extends Command{
	private TimeSpan timeSpan;
	private TaskManager manager;
	
	public ListCommand (Action commandAction, TimeSpan timeSpan, TaskManager manager){
		super(commandAction);
		this.timeSpan = timeSpan;
		this.manager = manager;
	}

	@Override
	public Result execute() {
		List<Task> listOfResults = manager.listTasks(timeSpan);
		Result result = new Result(Action.LIST, listOfResults);
		
		return result;
	}

	@Override
	public boolean equals(Command command) {
		ListCommand listCommand = (ListCommand) command;
		boolean equals = false;
		if (this.timeSpan.getStartTime() != null && listCommand.timeSpan.getStartTime() != null) {
			if (this.timeSpan.getStartTime().equals(listCommand.timeSpan.getStartTime())	
					&& this.timeSpan.getEndTime().equals(listCommand.timeSpan.getEndTime()))
				equals = true;
		} else	if (this.timeSpan.getStartTime() == null && listCommand.timeSpan.getStartTime() == null) {
			if (this.timeSpan.getEndTime() != null){
				if (this.timeSpan.getEndTime().equals(listCommand.timeSpan.getEndTime()))
					equals = true;
			}else if (this.timeSpan.getEndTime() == null && listCommand.timeSpan.getEndTime() == null)
				equals = true;

		}
		return equals;
	}

}
