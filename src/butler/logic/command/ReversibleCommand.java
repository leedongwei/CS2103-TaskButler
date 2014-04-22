// @author A0097722X
package butler.logic.command;

import java.util.Stack;

public abstract class ReversibleCommand extends Command {

	private Stack<ReversibleCommand> historyStack;

	public ReversibleCommand(Action commandAction,
			Stack<ReversibleCommand> historyStack) {
		super(commandAction);
		this.historyStack = historyStack;
	}
	
	protected void pushToHistoryStack() {
		historyStack.push(this);
	}

	public abstract Result undo();
	
}
