// @author A0085419x
package butler.io;

import butler.common.Task;

import java.util.List;

public interface Storage {
	
	public List<Task> loadTasks();
	
	public void saveTasks(List<Task> taskList);

}
