// @author A0097722X
package butler.logic.command;

import java.util.List;
import butler.common.Task;
import butler.logic.taskmgr.TaskManager;

public class SearchCommand extends Command {
	private TaskManager manager;
	private String query;
	private boolean isFinishedTasks;
	
	public SearchCommand(Action commandAction, TaskManager manager, String query, boolean isFinishedTasks) {
		super(Action.SEARCH);
		this.manager = manager;
		this.query = query;
		this.isFinishedTasks = isFinishedTasks;
	}

	@Override
	public Result execute() {
		List<Task> searchTasks = null;
		if(isFinishedTasks){
			 searchTasks = manager.searchFinishedTasks(query);
		}
		else{
			 searchTasks = manager.searchUnfinishedTasks(query);
		}
		Result result = new Result(Action.SEARCH, searchTasks);
		
		return result;
	}
	
	@Override
	public boolean equals(Command command) {
		SearchCommand searchCommand = (SearchCommand) command;
		boolean equals = false;
		
		if(this.query.equals(searchCommand.query))
			if(this.isFinishedTasks == searchCommand.isFinishedTasks)
				equals = true;
		
		return equals;
	}

}
