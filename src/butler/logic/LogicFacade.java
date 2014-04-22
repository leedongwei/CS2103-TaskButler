// @author A0097836L
package butler.logic;

import butler.logic.command.Autofill;
import butler.logic.command.Command;
import butler.logic.command.CommandGuide;
import butler.logic.command.CommandParser;
import butler.logic.command.CommandParserImpl;
import butler.logic.command.Result;
import butler.logic.state.DisplayState;
import butler.logic.state.DisplayStateListener;
import butler.logic.taskmgr.TaskManager;
import butler.logic.taskmgr.TaskManagerImpl;
import butler.common.Task;
import butler.common.TimeSpan;
import butler.io.Storage;

import java.util.List;

/**
 * The LogicFacade class is a thin facade for UserInterface to interact with the
 * subcomponents of Logic.
 */
public class LogicFacade implements Logic {

	private Storage storage;
	private DisplayState state;
	private TaskManager manager;
	private CommandParser parser;
	private CommandGuide guide;
	private Autofill autofill;

	public LogicFacade(Storage storage) {

		assert storage != null;

		this.storage = storage;
		this.state = new DisplayState();
		this.manager = new TaskManagerImpl(state);
		this.parser = new CommandParserImpl(manager, state);
		this.guide = new CommandGuide();
		this.autofill = new Autofill(state);
	}

	@Override
	public void initialize() {
		List<Task> taskList = storage.loadTasks();
		manager.setTaskList(taskList);
	}

	@Override
	public Result executeCommand(String commandString) throws Exception {

		Command command = parser.parse(commandString);
		Result result = command.execute();

		if (command.isPersistable()) {
			List<Task> taskList = manager.getAllTasks();
			storage.saveTasks(taskList);
		}

		return result;
	}

	@Override
	public String getTipMessage(String commandString) {
		return guide.getTipMessage(commandString);
	}

	@Override
	public String getGuideMessage(String commandString) {
		return guide.getGuideMessage(commandString);
	}

	@Override
	public String autofill(String commandString) throws Exception {
		return autofill.complete(commandString);
	}

	@Override
	public List<Task> listTasks(TimeSpan timeSpan) {
		return manager.listTasks(timeSpan);
	}

	@Override
	public List<Task> searchUnfinishedTasks(String searchTerms) {
		return manager.searchUnfinishedTasks(searchTerms);
	}

	@Override
	public List<Task> searchFinishedTasks(String searchTerms) {
		return manager.searchFinishedTasks(searchTerms);
	}

	@Override
	public void addDisplayStateListener(DisplayStateListener listener) {
		state.addListener(listener);
	}

	@Override
	public void removeDisplayStateListener(DisplayStateListener listener) {
		state.removeListener(listener);
	}

}
