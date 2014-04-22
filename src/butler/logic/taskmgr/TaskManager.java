// @author A0097836L
package butler.logic.taskmgr;

import java.util.List;

import butler.common.Task;
import butler.common.TaskType;
import butler.common.TimeSpan;

public interface TaskManager {
	
	public void setTaskList(List<Task> taskList);
	
	public Task addTask(String name, TimeSpan timeSpan, TaskType type);

	public Task addTask(String name, TimeSpan timeSpan, TaskType type,
			boolean isFinished);

	public Task updateTask(Task task, String newName, TimeSpan newTimeSpan,
			TaskType newType);

	public Task deleteTask(Task task);

	public Task undeleteTask(Task task);

	public List<Task> listTasks(TimeSpan timeSpan);

	public List<Task> searchUnfinishedTasks(String searchTerms);

	public List<Task> searchFinishedTasks(String searchTerms);

	public Task markTaskAsFinished(Task task);

	public Task markTaskAsUnfinished(Task task);

	public List<Task> getAllTasks();

}
