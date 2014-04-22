// @author A0097722X
package butler.logic.command;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import butler.logic.taskmgr.TaskManager;
import butler.logic.taskmgr.TaskManagerImpl;

public class UndoParserTest {
	private CommandParserImpl commandParser;
	private TaskManager manager;
	@Before
	public void setUp(){	
		this.manager = new TaskManagerImpl(null);
		this.commandParser = new CommandParserImpl(manager,null);
		new  Stack<ReversibleCommand>();
	}
	

	@Test (expected = Exception.class)
	//test undo exception when there is no history of command to undo
	public void SearchCommand_doneTasks() throws Exception {
		
		String commandString = "undo";
		
		commandParser.parse(commandString);
	
	}
}
