// @author A0097836L
package butler.logic.state;

import java.util.EventObject;
import java.util.List;

import butler.common.Task;

/**
 * The DisplayListChangedEvent class contains the information that describes
 * the changed DisplayList. It is used by UI to detect changes to DisplayList
 * so that it can refresh accordingly.
 */
public class DisplayListChangedEvent extends EventObject {

	// auto-generated default
	private static final long serialVersionUID = 1L;

	private DisplayListType listChanged;
	private List<Task> displayList;

	public DisplayListChangedEvent(Object source, DisplayListType listChanged,
			List<Task> displayList) {
		super(source);
		this.listChanged = listChanged;
		this.displayList = displayList;
	}

	public DisplayListType getListChanged() {
		return listChanged;
	}

	public List<Task> getDisplayList() {
		return displayList;
	}

}
