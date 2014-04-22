// @author A0097836L
package butler.logic.taskmgr;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import butler.common.LoggerPreset;
import butler.common.Task;
import butler.common.TaskType;
import butler.common.TimeSpan;
import butler.logic.state.DisplayState;

/**
 * The TaskManagerImpl class encapsulates the functions of managing the global
 * task list.
 */
public class TaskManagerImpl implements TaskManager {
	
	private static final Logger log = LoggerPreset.getLogger();

	private DisplayState state;
	private SearchLogic search;
	private List<Task> taskList;

	public TaskManagerImpl(DisplayState state) {
		this.state = state;
		this.search = new SearchLogic();
		this.taskList = new ArrayList<Task>();
	}

	@Override
	public Task addTask(String name, TimeSpan timeSpan, TaskType type) {
		return addTask(name, timeSpan, type, false);
	}

	@Override
	public Task addTask(String name, TimeSpan timeSpan, TaskType type,
			boolean isFinished) {
		
		log.log(Level.INFO, "Added task [{0}]", name);

		Task task = new Task(type, name, timeSpan, isFinished);
		taskList.add(task);
		
		state.onAddTask(task);
		return task;
	}

	/**
	 * Updates the task with the new parameters specified. If null parameters
	 * is specified, then the fields associated will not be updated.
	 */
	@Override
	public Task updateTask(Task task, String newName, TimeSpan newTimeSpan,
			TaskType newType) {
		
		log.log(Level.INFO, "Updated task [{0}]", newName);
		
		state.onDeleteTask(task);
		
		updateTaskName(task, newName);
		updateTaskTimeSpan(task, newTimeSpan);
		updateTaskType(task, newType);
		
		state.onAddTask(task);
		return task;
	}
	
	private void updateTaskName(Task task, String newName) {
		
		if (newName == null) {
			return;
		}
		
		assert task != null;
		task.setName(newName);
	}
	
	private void updateTaskTimeSpan(Task task, TimeSpan newTimeSpan) {
		
		if (newTimeSpan == null) {
			return;
		}

		assert task != null;
		task.setPeriod(newTimeSpan);
	}
	
	private void updateTaskType(Task task, TaskType newType) {
		
		if (newType == null) {
			return;
		}

		assert task != null;
		task.setTaskType(newType);
	}


	/**
	 * Deletes the specified task from the task list. However, the actual task
	 * is not physically removed but only marked as deleted. This is for undo
	 * operation to be able to undelete the task.
	 */
	@Override
	public Task deleteTask(Task task) {
		
		// under correct operation, the task must be in the task list
		assert task != null;
		assert taskList.contains(task);
		log.log(Level.INFO, "Set task [{0}] as deleted", task.getName());

		task.setDeleted(true);
		
		state.onDeleteTask(task);
		return task;
	}
	
	/**
	 * Undeletes the specified task from the task list. This method should only
	 * be called by the undo function.
	 */
	@Override
	public Task undeleteTask(Task task) {
		
		// under correct operation, the task must be in the task list
		assert task != null;
		assert taskList.contains(task);
		log.log(Level.INFO, "Set task [{0}] as undeleted", task.getName());

		task.setDeleted(false);
		
		state.onAddTask(task);
		return task;
	}

	@Override
	public List<Task> listTasks(TimeSpan timeSpan) {
		
		log.log(Level.INFO, "List tasks: {0}", timeSpan);
		
		List<Task> resultList = new ArrayList<Task>();
		
		for (Task task : taskList) {
			if (search.isListResult(timeSpan, task)) {
				resultList.add(task);
			}
		}

		state.onListTasks(resultList, timeSpan);
		return resultList;
	}

	@Override
	public List<Task> searchUnfinishedTasks(String searchTerms) {
		
		log.log(Level.INFO, "Searched unfinished tasks: {0}", searchTerms);
		
		List<Task> resultList = new ArrayList<Task>();
		
		for (Task task : taskList) {
			if (search.isSearchUnfinishedResult(searchTerms, task)) {
				resultList.add(task);
			}
		}
		
		state.onSearchTasks(resultList, searchTerms, false);
		return resultList;
	}

	@Override
	public List<Task> searchFinishedTasks(String searchTerms) {
		
		log.log(Level.INFO, "Searched finished tasks: {0}", searchTerms);
		
		List<Task> resultList = new ArrayList<Task>();
		
		for (Task task : taskList) {
			if (search.isSearchFinishedResult(searchTerms, task)) {
				resultList.add(task);
			}
		}
		
		state.onSearchTasks(resultList, searchTerms, true);
		return resultList;
	}

	@Override
	public Task markTaskAsFinished(Task task) {

		assert task != null;
		log.log(Level.INFO, "Set task [{0}] as finished", task.getName());
		
		task.setFinished(true);
		
		state.onMarkTask(task);
		return task;
	}

	@Override
	public Task markTaskAsUnfinished(Task task) {
		
		assert task != null;
		log.log(Level.INFO, "Set task [{0}] as unfinished", task.getName());
		
		task.setFinished(false);
		
		state.onMarkTask(task);
		return task;
	}

	@Override
	public List<Task> getAllTasks() {
		return taskList;
	}
	
	@Override
	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}

}
