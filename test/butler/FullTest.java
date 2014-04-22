// @author A0097802Y

package butler;

import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import butler.io.Storage;
import butler.io.StorageFacade;
import butler.logic.Logic;
import butler.logic.LogicFacade;
import butler.ui.UserInterface;

public class FullTest {
	
	private static String TODAY_DATE;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		TODAY_DATE = sdf.format(new Date());
	}

	@Test
	public void addFloatingTasks() throws Exception {

		boolean isCorrectResult = true;
		Storage storage = initializeStorage();
		Logic logic = initializeLogic(storage);
		UserInterface window = initializeUserInterface(logic);
		logic.executeCommand("add hiking");
		logic.executeCommand("add do CS21AA Project");
		logic.executeCommand("add do CS21YY Project");
		logic.executeCommand("add do CS21XX Project");
			
		if (window.getFloatingModel().getSize() != 4) {
			isCorrectResult = false;
		}
		
		window.show(true);

		assertTrue("Incorrect floating task size result", isCorrectResult);
	}

	@Test
	public void addNormalTasks() throws Exception {

		boolean isCorrectResult = true;
		Storage storage = initializeStorage();
		Logic logic = initializeLogic(storage);
		UserInterface window = initializeUserInterface(logic);
		
		logic.executeCommand("add watch movie .on " + TODAY_DATE + " .at 21.00-23.00");
		logic.executeCommand("add eat breakfast .on " + TODAY_DATE + " .at 09.00-10.00");
	
		if (window.getNormalModel().getSize() != 2) {
			isCorrectResult = false;
		}
		
		window.show(true);

		assertTrue("Incorrect normal task size result", isCorrectResult);
	}
	
	@Test
	public void addDeadlineTasks() throws Exception {

		boolean isCorrectResult = true;
		Storage storage = initializeStorage();
		Logic logic = initializeLogic(storage);
		UserInterface window = initializeUserInterface(logic);
		
		logic.executeCommand("add watch movie .by 13/11/13");
		logic.executeCommand("add buy clothes .by 13/11/13");
	
		if (window.getDeadlineModel().getSize() != 2) {
			isCorrectResult = false;
		}
		
		window.show(true);

		assertTrue("Incorrect deadline task size result", isCorrectResult);
	}
	
	@Test
	public void addTasksOfAllTypes() throws Exception {

		boolean isCorrectResult = true;
		Storage storage = initializeStorage();
		Logic logic = initializeLogic(storage);
		UserInterface window = initializeUserInterface(logic);
		
		logic.executeCommand("add watch movie");
		logic.executeCommand("add hiking");
		logic.executeCommand("add do CS21AA Project");
		logic.executeCommand("add do CS21YY Project");
		assertTrue("Incorrect floating task size result", isCorrectResult);
		
		logic.executeCommand("add watch movie .on " + TODAY_DATE + " .at 21.00-23.00");
		logic.executeCommand("add eat breakfast .on " + TODAY_DATE + " .at 09.00-10.00");
		logic.executeCommand("add buy car .on " + TODAY_DATE);
		assertTrue("Incorrect normal task size result", isCorrectResult);
		
		logic.executeCommand("add watch movie .by 13/11/13");
		logic.executeCommand("add buy clothes .by 13/11/13");
		assertTrue("Incorrect deadline task size result", isCorrectResult);
		
		if (window.getDeadlineModel().getSize() != 2) {
			isCorrectResult = false;
		}
		
		if (window.getNormalModel().getSize() != 3) {
			isCorrectResult = false;
		}
		
		if (window.getFloatingModel().getSize() != 4) {
			isCorrectResult = false;
		}
		
		window.show(true);

		assertTrue("Incorrect final result", isCorrectResult);
	}
	
	
	@Test
	public void deleteDeadlineTasks() throws Exception {

		boolean isCorrectResult = true;
		Storage storage = initializeStorage();
		Logic logic = initializeLogic(storage);
		UserInterface window = initializeUserInterface(logic);
		
		logic.executeCommand("add watch movie .by 13/11/13");
		logic.executeCommand("add buy clothes .by 13/11/13");
		logic.executeCommand("delete d2");
		
		if (window.getDeadlineModel().getSize() != 1) {
			isCorrectResult = false;
		}
		
		window.show(true);
		assertTrue("Incorrect deadline task size result", isCorrectResult);
	}
	
	@Test
	public void deleteNormalTasks() throws Exception {

		boolean isCorrectResult = true;
		Storage storage = initializeStorage();
		Logic logic = initializeLogic(storage);
		UserInterface window = initializeUserInterface(logic);
		
		logic.executeCommand("add watch movie .on " + TODAY_DATE + " .at 21.00-23.00");
		logic.executeCommand("add eat breakfast .on " + TODAY_DATE + " .at 09.00-10.00");
		logic.executeCommand("add buy car .on " + TODAY_DATE);
		logic.executeCommand("delete t2");
		
		if (window.getNormalModel().getSize() != 2) {
			isCorrectResult = false;
		}
		
		window.show(true);
		assertTrue("Incorrect normal task size result", isCorrectResult);
	}
	
	@Test
	public void deleteFloatingTasks() throws Exception {

		boolean isCorrectResult = true;
		Storage storage = initializeStorage();
		Logic logic = initializeLogic(storage);
		UserInterface window = initializeUserInterface(logic);
		
		logic.executeCommand("add watch movie");
		logic.executeCommand("add hiking");
		logic.executeCommand("add do CS21AA Project");
		logic.executeCommand("add do CS21YY Project");
		logic.executeCommand("delete f2");
		
		if (window.getFloatingModel().getSize() != 3) {
			isCorrectResult = false;
		}
		
		window.show(true);
		assertTrue("Incorrect floating task size result", isCorrectResult);
	}
	
	private static Storage initializeStorage() {
		Storage storage = new StorageFacade();
		return storage;
	}

	private static Logic initializeLogic(Storage storage) {
		Logic logic = new LogicFacade(storage);
		return logic;
	}

	private static UserInterface initializeUserInterface(Logic logic) {
		UserInterface window = new UserInterface(logic);
		return window;
	}

}
