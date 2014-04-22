// @author A0097836L
package butler.logic;

import java.util.List;

import butler.common.Task;
import butler.common.TimeSpan;
import butler.logic.command.Result;
import butler.logic.state.DisplayStateListener;

public interface Logic {

	public void initialize();

	public Result executeCommand(String commandString) throws Exception;

	public String getTipMessage(String commandString);

	public String getGuideMessage(String commandString);

	public String autofill(String commandString) throws Exception;

	public List<Task> listTasks(TimeSpan timeSpan);

	public List<Task> searchUnfinishedTasks(String searchTerms);

	public List<Task> searchFinishedTasks(String searchTerms);

	public void addDisplayStateListener(DisplayStateListener listener);

	public void removeDisplayStateListener(DisplayStateListener listener);

}
