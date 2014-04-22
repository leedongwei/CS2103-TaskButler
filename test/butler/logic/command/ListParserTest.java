// @author A0097722X
package butler.logic.command;

import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import butler.common.TimeSpan;
import butler.logic.taskmgr.TaskManager;
import butler.logic.taskmgr.TaskManagerImpl;

public class ListParserTest {
	private CommandParserImpl commandParser;
	private TaskManager manager;
	
	@Before
	public void setUp(){
		this.manager = new TaskManagerImpl(null);
		this.commandParser = new CommandParserImpl(manager,null);
	}
	
	@Test 
	//test for list today command
	public void ListToday() throws Exception {
		TimeSpan timeSpan = new TimeSpan(new LocalDate().toDate(), new LocalDate().plusDays(1).toDate());		
		String commandString = "list today";
		
		ListCommand actualCommand = (ListCommand) commandParser.parse(commandString);
		ListCommand expectedCommand = new ListCommand(Action.LIST, timeSpan, manager);
		
		assertTrue(expectedCommand.equals(actualCommand));
	}
	
	@Test 
	//test for listing a particular date
	public void ListSpecifiedDate() throws Exception {
		TimeSpan timeSpan = new TimeSpan(new DateTime(2013,10,10, 0, 0).toDate(), new DateTime(2013,10,11, 0, 0).toDate());		
		String commandString = "list 10/10/13";
		
		ListCommand actualCommand = (ListCommand) commandParser.parse(commandString);
		ListCommand expectedCommand = new ListCommand(Action.LIST, timeSpan, manager);
		
		assertTrue(expectedCommand.equals(actualCommand));
	}
	
	@Test 
	//test for listing a range of dates
	public void ListRangeOfSpecifiedDate() throws Exception {
		TimeSpan timeSpan = new TimeSpan(new DateTime(2013,10,10, 0, 0).toDate(), new DateTime(2013,11,13, 0, 0).toDate());		
		String commandString = "list 10/10/13-12/11/13";
		
		ListCommand actualCommand = (ListCommand) commandParser.parse(commandString);
		ListCommand expectedCommand = new ListCommand(Action.LIST, timeSpan, manager);
		
		assertTrue(expectedCommand.equals(actualCommand));
	}
	
}
