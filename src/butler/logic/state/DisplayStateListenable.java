package butler.logic.state;

import java.util.LinkedList;
import java.util.List;

public class DisplayStateListenable {
	
	private static final String EXCEPTION_ADD_NULL_LISTENER = "Added null listener";

	private List<DisplayStateListener> listeners;

	public DisplayStateListenable() {
		listeners = new LinkedList<DisplayStateListener>();
	}

	public void addListener(DisplayStateListener listener)
			throws NullPointerException {

		if (listener == null) {
			throw new NullPointerException(EXCEPTION_ADD_NULL_LISTENER);
		}

		listeners.add(listener);
	}

	public void removeListener(DisplayStateListener listener) {
		listeners.remove(listener);
	}

	protected void notifyModeChanged(DisplayModeChangedEvent evt) {
		for (DisplayStateListener listener : listeners) {
			listener.modeChanged(evt);
		}
	}

	protected void notifyListChanged(DisplayListChangedEvent evt) {
		for (DisplayStateListener listener : listeners) {
			listener.listChanged(evt);
		}
	}

}
