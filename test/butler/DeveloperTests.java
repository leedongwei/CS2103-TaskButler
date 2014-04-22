// @author A0097836L
package butler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import butler.io.SaveTasksTest;
import butler.logic.command.AddParserTest;
import butler.logic.command.CheckParserTest;
import butler.logic.command.DeleteParserTest;
import butler.logic.command.ListParserTest;
import butler.logic.command.SearchParserTest;
import butler.logic.command.UnCheckParserTest;
import butler.logic.command.UndoParserTest;
import butler.logic.command.UpdateParserTest;
import butler.logic.taskmgr.SearchLogicTest;

@RunWith(Suite.class)
@SuiteClasses({ SearchLogicTest.class, AddParserTest.class,
		CheckParserTest.class, DeleteParserTest.class, ListParserTest.class,
		SearchParserTest.class, UnCheckParserTest.class, UndoParserTest.class,
		UpdateParserTest.class, SaveTasksTest.class })
public class DeveloperTests {

}
