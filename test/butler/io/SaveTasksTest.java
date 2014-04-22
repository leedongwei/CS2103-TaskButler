// @author A0085419X
package butler.io;

import butler.common.Task;
import butler.common.TaskType;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



public class SaveTasksTest {

	List<Task> taskList;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {		
		taskList = new ArrayList<Task>();
		
		Task floatTask = new Task(TaskType.FLOATING, "Floating Task");
		taskList.add(floatTask);
		
		Date start = new Date(1380556800000L);
		Date end = new Date(1380560400000L);
		Task normTask = new Task(TaskType.NORMAL, "Normal Task", start, end);
		taskList.add(normTask);
		
		Task deadTask = new Task(TaskType.NORMAL, "Deadline Task", start, end);
		taskList.add(deadTask);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		StorageFacade testStore = new StorageFacade();
		testStore.saveTasks(taskList);
		testStore.loadTasks();		
	}

}
