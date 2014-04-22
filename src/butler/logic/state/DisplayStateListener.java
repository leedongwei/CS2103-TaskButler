// @author A0097836L
package butler.logic.state;

import java.util.EventListener;

public interface DisplayStateListener extends EventListener {
	
	public void modeChanged(DisplayModeChangedEvent evt);
	
	public void listChanged(DisplayListChangedEvent evt);

}
