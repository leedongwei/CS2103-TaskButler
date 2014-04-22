// @author A0097722X
package butler.logic.command;


public abstract class Command {
	
	private Action commandAction;
	
	public Command(Action commandAction) {
		this.commandAction = commandAction;
	}
	
	public abstract Result execute();
	
	public boolean isPersistable() {
		return commandAction.isPersistable();
	}
	
	public boolean equals(Command command){
		return false;
		
	}

}
