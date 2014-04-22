// @author A0097722X
package butler.logic.command;

public interface CommandParser {
	
	public Command parse(String commandString) throws Exception;
	
	public Command parse(String commandString, boolean allowHiddenFlags) throws Exception;

}
