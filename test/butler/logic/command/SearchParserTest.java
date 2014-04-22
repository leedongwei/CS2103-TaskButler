// @author A0097722X
package butler.logic.command;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import butler.logic.taskmgr.TaskManager;
import butler.logic.taskmgr.TaskManagerImpl;

public class SearchParserTest {
	private CommandParserImpl commandParser;
	private TaskManager manager;


	@Before
	public void setUp(){	
		this.manager = new TaskManagerImpl(null);
		this.commandParser = new CommandParserImpl(manager,null);

	}
	
	@Test 
	//test for search command object
	public void SearchCommand_UndoneTasks() throws Exception {
		
		String commandString = "search meeting with boss";
		
		SearchCommand actualCommand = (SearchCommand) commandParser.parse(commandString);
		SearchCommand expectedcommand = new SearchCommand(Action.SEARCH, manager, "meeting with boss",false);
		
		assertTrue(expectedcommand.equals(actualCommand));
	}
	
	@Test 
	//test for search command object with .done flag
	public void SearchCommand_doneTasks() throws Exception {
		
		String commandString = "search meeting with boss .done";
		
		SearchCommand actualCommand = (SearchCommand) commandParser.parse(commandString);
		SearchCommand expectedcommand = new SearchCommand(Action.SEARCH, manager, "meeting with boss",true);
		
		assertTrue(expectedcommand.equals(actualCommand));
	}
	
}
