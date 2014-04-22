// @author A0097722X
package butler.logic.command;

import java.util.Stack;
import java.util.Vector;

import butler.common.Task;

public class UndoCommand extends Command {
	private Stack<ReversibleCommand> historyStack;
	
	public UndoCommand(Action commandAction, Stack<ReversibleCommand> historyStack) {
		super(commandAction);
		this.historyStack = historyStack;
		
	}

	@Override
	public Result execute() {
		if(!historyStack.isEmpty()){
			ReversibleCommand command = historyStack.pop();
			Result result = command.undo();
			return result;	
			
		}
		return new Result(Action.UNDO, new Vector<Task>());
	}
	
	
}
