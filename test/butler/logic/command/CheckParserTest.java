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

public class CheckParserTest {
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
	public void CheckCommand() throws Exception {
		TimeSpan NormalTimeSpan  = new TimeSpan(new DateTime().toDate(), new DateTime().toDate());		
		String commandString = "check t1";
		
		Task OriginalTask = new Task(TaskType.NORMAL, "NORMAL", NormalTimeSpan);
		manager.addTask("NORMAL", NormalTimeSpan, TaskType.NORMAL);
		
		CheckCommand actualCommand = (CheckCommand) commandParser.parse(commandString);
		CheckCommand checkcommand = new CheckCommand(Action.MARK_AS_FINISHED, historyStack, manager, OriginalTask);
		
		assertTrue(checkcommand.equals(actualCommand));
	}
	
	@Test(expected = Exception.class)
	//Test for when wrong user input task exception
	public void CheckCommand_Exception_WrongTaskInput() throws Exception {
		TimeSpan NormalTimeSpan  = new TimeSpan(new DateTime().toDate(), new DateTime().toDate());		
		
		String commandString = "check t2";	//invalid task ID
		
		Task OriginalTask = new Task(TaskType.NORMAL, "NORMAL", NormalTimeSpan);
		manager.addTask("NORMAL", NormalTimeSpan, TaskType.NORMAL);
		
		CheckCommand actualCommand = (CheckCommand) commandParser.parse(commandString);
		CheckCommand expectedcommand = new CheckCommand(Action.MARK_AS_FINISHED, historyStack, manager, OriginalTask);
		
		assertTrue(expectedcommand.equals(actualCommand));
	}
}
