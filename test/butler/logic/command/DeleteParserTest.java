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

public class DeleteParserTest {
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
	//test for checking a command object
	public void DeleteCommand() throws Exception {
		TimeSpan NormalTimeSpan  = new TimeSpan(new DateTime().toDate(), new DateTime().toDate());		
		String commandString = "delete t1";
		
		Task OriginalTask = new Task(TaskType.NORMAL, "NORMAL", NormalTimeSpan);
		manager.addTask("NORMAL", NormalTimeSpan, TaskType.NORMAL);
		
		DeleteCommand actualCommand = (DeleteCommand) commandParser.parse(commandString);
		DeleteCommand expectedcommand = new DeleteCommand(Action.MARK_AS_FINISHED, historyStack, OriginalTask, manager);
		
		assertTrue(expectedcommand.equals(actualCommand));
	}
	
	@Test(expected = Exception.class)
	//test for checking a command object
	public void DeleteCommand_Exception_WrongTaskInput() throws Exception {
		TimeSpan NormalTimeSpan  = new TimeSpan(new DateTime().toDate(), new DateTime().toDate());		
		String commandString = "delete t2";
		
		Task OriginalTask = new Task(TaskType.NORMAL, "NORMAL", NormalTimeSpan);
		manager.addTask("NORMAL", NormalTimeSpan, TaskType.NORMAL);
		
		DeleteCommand actualCommand = (DeleteCommand) commandParser.parse(commandString);
		DeleteCommand expectedcommand = new DeleteCommand(Action.MARK_AS_FINISHED, historyStack, OriginalTask, manager);
		
		assertTrue(expectedcommand.equals(actualCommand));
	}

}
