// @author A0097722X
package butler.logic.command;

import static org.junit.Assert.assertTrue;

import java.util.Stack;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import butler.common.Task;
import butler.common.TaskType;
import butler.common.TimeSpan;
import butler.logic.state.DisplayState;
import butler.logic.taskmgr.TaskManager;
import butler.logic.taskmgr.TaskManagerImpl;

public class UpdateParserTest {
	private CommandParserImpl commandParser;
	private TaskManager manager;
	private Stack<ReversibleCommand> historyStack;
	private DisplayState state;

	@Before
	public void setUp(){
		this.state = new DisplayState();
		this.manager = new TaskManagerImpl(state);
		this.commandParser = new CommandParserImpl(manager,state);
		this.historyStack = new  Stack<ReversibleCommand>();
	}
	
	@Test 
	//test for updating a floating task to a normal task with specified date
	public void UpdateFloatingTask() throws Exception {
		TimeSpan floatTimeSpan = new TimeSpan(null, null);
		TimeSpan UpdatedTimeSpan = new TimeSpan(new DateTime(2013,10,23,0,0).toDate(), new DateTime(2013,10,24,0,0).toDate());		
		String commandString = "update f1 UpdatedFloatToNormal .on 23/10/13";
		
		Task OriginalTask = new Task(TaskType.FLOATING, "Float", floatTimeSpan);
		manager.addTask("Float", floatTimeSpan, TaskType.FLOATING);
		
		UpdateCommand updateCommand = (UpdateCommand) commandParser.parse(commandString);
		UpdateCommand actualCommand = new UpdateCommand(Action.UPDATE, historyStack, manager, "UpdatedFloatToNormal", UpdatedTimeSpan, TaskType.NORMAL, OriginalTask);
		assertTrue(updateCommand.equals(actualCommand));
	}
	
	@Test 
	//test for updating a Deadline task to normal tasks with specified date and time
	public void UpdateDeadlineTask() throws Exception {
		TimeSpan DeadlineTimeSpan = new TimeSpan(null, new DateTime().toDate());
		TimeSpan UpdatedTimeSpan = new TimeSpan(new DateTime(2013,10,23,14,00).toDate(), new DateTime(2013,10,23,15,00).toDate());		
		String commandString = "update d1 UpdatedDeadlineToNormal .on 23/10/13 .at 14.00";
		
		Task OriginalTask = new Task(TaskType.DEADLINE, "Deadline", DeadlineTimeSpan);
		manager.addTask("Deadline", DeadlineTimeSpan, TaskType.DEADLINE);
		
		UpdateCommand updateCommand = (UpdateCommand) commandParser.parse(commandString);
		UpdateCommand actualCommand = new UpdateCommand(Action.UPDATE, historyStack, manager, "UpdatedDeadlineToNormal", UpdatedTimeSpan, TaskType.NORMAL, OriginalTask);
		assertTrue(updateCommand.equals(actualCommand));
	}
	
	@Test 
	//test for updating a Normal task to a Deadline task with specified date
	public void UpdateNormalTask() throws Exception {
		TimeSpan NormalTimeSpan  = new TimeSpan(new DateTime().toDate(), new DateTime().toDate());		
		TimeSpan UpdatedTimeSpan = new TimeSpan(null, new DateTime(2013,10,23,23,59).toDate());
		String commandString = "update T1 UpdatedNormalToDeadline .by 23/10/13";
		
		Task OriginalTask = new Task(TaskType.NORMAL, "NORMAL", NormalTimeSpan);
		manager.addTask("NORMAL", NormalTimeSpan, TaskType.NORMAL);
		
		UpdateCommand updateCommand = (UpdateCommand) commandParser.parse(commandString);
		UpdateCommand actualCommand = new UpdateCommand(Action.UPDATE, historyStack, manager, "UpdatedNormalToDeadline", UpdatedTimeSpan, TaskType.DEADLINE, OriginalTask);
		assertTrue(updateCommand.equals(actualCommand));
	}
	

	@Test(expected = Exception.class)
	//test for exception when updating a normal task but with invalid task ID input
	public void UpdateNormalTask_Exception_WrongTaskInput() throws Exception {
		TimeSpan NormalTimeSpan  = new TimeSpan(new DateTime().toDate(), new DateTime().toDate());		
		TimeSpan UpdatedTimeSpan = new TimeSpan(null, new DateTime(2013,10,23,23,59).toDate());
		String commandString = "update T2 UpdatedNormalToDeadline .by 23/10/13";
		
		Task OriginalTask = new Task(TaskType.NORMAL, "NORMAL", NormalTimeSpan);
		manager.addTask("NORMAL", NormalTimeSpan, TaskType.NORMAL);
		
		UpdateCommand updateCommand = (UpdateCommand) commandParser.parse(commandString);
		UpdateCommand actualCommand = new UpdateCommand(Action.UPDATE, historyStack, manager, "UpdatedNormalToDeadline", UpdatedTimeSpan, TaskType.DEADLINE, OriginalTask);
		assertTrue(updateCommand.equals(actualCommand));
	}
}
