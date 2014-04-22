// @author A0085419x
package butler.io;

import butler.common.Task;

import java.util.List;

public class StorageFacade implements Storage {
	
	private static final String FILE_NAME = "cs2103aug2013f10-2j.txt";
	
	private LoadTasks load;
	private SaveTasks save;
	
	public StorageFacade() {
		load = new LoadTasks(FILE_NAME);
		save = new SaveTasks(FILE_NAME);
	}

	@Override
	public List<Task> loadTasks(){
		return load.execute(null);		
	}

	@Override
	public void saveTasks(List<Task> taskList) {
		save.execute(taskList);
	}

}
