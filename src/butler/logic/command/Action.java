// @author A0097836L
package butler.logic.command;

/**
 * The Action enumeration is used to specify the command action that the user
 * entered.
 */
public enum Action {
	
	ADD                 (true, true),
	UPDATE              (true, true),
	DELETE              (true, true),
	LIST                (false, false),
	SEARCH              (false, false),
	UNDO                (false, true),
	MARK_AS_FINISHED    (true, true),
	MARK_AS_UNFINISHED  (true, true),
	INVALID             (false, false);
	
	private final boolean isReversible;
	private final boolean isPersistable;
	
	private Action(boolean isReversible, boolean isPersistable) {
		this.isReversible = isReversible;
		this.isPersistable = isPersistable;
	}
	
	/**
	 * Returns true if the effect of this action can be reversed.
	 * 
	 * @return	true if the effect of this action can be reversed
	 */
	public boolean isReversible() {
		return isReversible;
	}
	
	/**
	 * Returns true if the effect of this action should be persisted.
	 * 
	 * @return	true if the effect of this action should be persisted
	 */
	public boolean isPersistable() {
		return isPersistable;
	}

}
