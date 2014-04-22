// @author A0085419x
package butler.io;

import butler.common.LoggerPreset;
import butler.common.Task;

import java.util.List;
import java.util.logging.Logger;


public abstract class StorageOperation {
	// special characters
	protected static final String NEW_LINE = "\r\n";
	protected static final String WHITESPACE = " ";
	protected static final String CMD_START = "ŒŒ";
	protected static final String CMD_END = "þþ";
	protected static final String CMD_SPLIT = CMD_END + WHITESPACE + CMD_START;
	
	protected static final Logger log = LoggerPreset.getLogger();
	
	protected String fileName_;
	protected List<Task> taskList_;
	protected List<String> commandStrings_;
	
	abstract List<Task> execute(List<Task> taskList);
}
