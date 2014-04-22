// @author A0097722X
package butler.logic.command;

import static org.junit.Assert.*;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import butler.common.TaskType;
import butler.common.TimeSpan;
import butler.logic.command.Action;
import butler.logic.command.AddCommand;
import butler.logic.command.CommandParserImpl;
import butler.logic.command.ReversibleCommand;
import butler.logic.taskmgr.TaskManager;
import butler.logic.taskmgr.TaskManagerImpl;

import org.joda.time.DateTime;

public class AddParserTest {
	private CommandParserImpl commandParser;
	private TaskManager manager;
	private Stack<ReversibleCommand> historyStack;
	
	@Before
	public void setUp(){
		manager = new TaskManagerImpl(null);
		commandParser = new CommandParserImpl(manager,null);
		historyStack = new  Stack<ReversibleCommand>();
	}
	
	@Test 
	//test for adding of floating tasks
	public void AddFloatingTask() throws Exception {
		TaskType type = TaskType.FLOATING;
		TimeSpan timeSpan = new TimeSpan(null, null);		
		String commandString = "schedule FloatingTask";
		
		AddCommand expectedCommand = new AddCommand(Action.ADD, historyStack, manager,"FloatingTask", timeSpan, type);
		AddCommand actualCommand = null;

		actualCommand = (AddCommand) commandParser.parse(commandString);

		assertTrue(expectedCommand.equals(actualCommand));
	}

	@Test
	//test add task with specific date
	public void AddNormalTask_WithOnCommand() throws Exception {
		TaskType type = TaskType.NORMAL;
		TimeSpan timeSpan = new TimeSpan(new DateTime(2013,10,23,0,0).toDate(), new DateTime(2013,10,24,0,0).toDate());		
		String commandString = "schedule TaskWithOn .on 23/10/13";
		
		AddCommand expectedCommand = new AddCommand(Action.ADD, historyStack, manager,"TaskWithOn", timeSpan, type);
		AddCommand actualCommand = null;
		
		actualCommand = (AddCommand) commandParser.parse(commandString);

		assertTrue(expectedCommand.equals(actualCommand));
	}
	
	@Test
	//test add task with specific date and time
	public void AddNormalTask_WithOnAndThenAtCommand() throws Exception {
		TaskType type = TaskType.NORMAL;
		TimeSpan timeSpan = new TimeSpan(new DateTime(2013,10,23,15,0).toDate(), new DateTime(2013,10,23,23,0).toDate());		
		String commandString = "schedule TaskWithOnAndAt .on 23/10/13 .at 15.00-23.00";
		
		AddCommand expectedCommand = new AddCommand(Action.ADD, historyStack, manager,"TaskWithOnAndAt", timeSpan, type);
		AddCommand actualCommand = null;
		actualCommand = (AddCommand) commandParser.parse(commandString);
	
		assertTrue(expectedCommand.equals(actualCommand));
	}

	@Test(expected = Exception.class)
	//test add task with specific date and time but by specifying time first before date
	public void AddNormalTask_Exception_IncorrectPrimaryCommand() throws Exception {
		TaskType type = TaskType.NORMAL;
		TimeSpan timeSpan = new TimeSpan(new DateTime(2013,10,23,15,0).toDate(), new DateTime(2013,10,23,23,0).toDate());		
		String commandString = "WrongCommand TaskA .at 15.00-23.00 .on 23/10/13";		//WrongCommand is not expected primary command type
		
		AddCommand expectedCommand = new AddCommand(Action.ADD, historyStack, manager,"TaskA", timeSpan, type);
		AddCommand actualCommand = null;
		actualCommand = (AddCommand) commandParser.parse(commandString);
	
		assertTrue(expectedCommand.equals(actualCommand));
	}

	@Test
	//test add task with specific date and time but by specifying time first before date
	public void AddNormalTask_WithAtandThenOnCommand() throws Exception {
		TaskType type = TaskType.NORMAL;
		TimeSpan timeSpan = new TimeSpan(new DateTime(2013,10,23,15,0).toDate(), new DateTime(2013,10,23,23,0).toDate());		
		String commandString = "schedule TaskWithAtAndOn .at 15.00-23.00 .on 23/10/13";
		
		AddCommand expectedCommand = new AddCommand(Action.ADD, historyStack, manager,"TaskWithAtAndOn", timeSpan, type);
		AddCommand actualCommand = null;
		actualCommand = (AddCommand) commandParser.parse(commandString);
	
		assertTrue(expectedCommand.equals(actualCommand));
	}
	
	@Test
	//test add task with specific date and time but by specifying time first before date
	public void AddDeadlineTask_WithByCommand() throws Exception {
		TaskType type = TaskType.DEADLINE;
		TimeSpan timeSpan = new TimeSpan(null, new DateTime(2013,11,11,23,59).toDate());		
		String commandString = "schedule DeadlineTask .by 11/11/13";
		
		AddCommand expectedCommand = new AddCommand(Action.ADD, historyStack, manager,"DeadlineTask", timeSpan, type);
		AddCommand actualCommand = null;
		actualCommand = (AddCommand) commandParser.parse(commandString);
	
		assertTrue(expectedCommand.equals(actualCommand));
	}
	

	@Test(expected = Exception.class)
	//test add task with specific date and time but by specifying time first before date
	public void AddNormalTask_Exception_EmptyDescription() throws Exception {
		String commandString = "schedule .at 15.00-23.00 .on 23/10/13";

		@SuppressWarnings("unused")
		AddCommand actualCommand  = (AddCommand) commandParser.parse(commandString);

	}
}
