// @author A0097836L
package butler.logic.state;

import java.util.EventObject;

import butler.common.TimeSpan;

/**
 * The DisplayModeChangedEvent class contains the information that describes
 * the new DisplayMode. It is used by UI to detect changes to DisplayMode so
 * that it can refresh accordingly.
 */
public class DisplayModeChangedEvent extends EventObject {
	
	// auto-generated default
	private static final long serialVersionUID = 1L;

	private DisplayMode modeChanged;
	private TimeSpan timeSpan;
	private String searchTerms;
	private boolean searchesFinishedTasks;

	public DisplayModeChangedEvent(Object source, DisplayMode modeChanged,
			TimeSpan timeSpan, String searchTerms, boolean searchesFinishedTasks) {
		super(source);
		this.modeChanged = modeChanged;
		this.timeSpan = timeSpan;
		this.searchTerms = searchTerms;
		this.searchesFinishedTasks = searchesFinishedTasks;
	}

	public DisplayMode getModeChanged() {
		return modeChanged;
	}

	public TimeSpan getTimeSpan() {
		return timeSpan;
	}

	public String getSearchTerms() {
		return searchTerms;
	}
	
	public boolean searchesFinishedTasks() {
		return searchesFinishedTasks;
	}

}
