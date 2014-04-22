// @author A0097836L
package butler.logic.state;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The DisplayList class extends java.util.ArrayList and provides the ability
 * to track if the list has been changed. (i.e. add, remove, etc.)
 */
public class DisplayList<E> extends ArrayList<E> {
	
	// auto-generated default value
	private static final long serialVersionUID = 1L;
	
	private boolean hasChanged;
	
	public DisplayList() {
		resetChanged();
	}
	
	public boolean hasChanged() {
		return hasChanged;
	}
	
	public void setAsChanged() {
		hasChanged = true;
	}
	
	public void resetChanged() {
		hasChanged = false;
	}
	
	@Override
	public boolean add(E obj) {
		setAsChanged();
		return super.add(obj);
	}
	
	@Override
	public void add(int index, E element) {
		setAsChanged();
		super.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		setAsChanged();
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		setAsChanged();
		return super.addAll(index, c);
	}

	@Override
	public void clear() {
		setAsChanged();
		super.clear();
	}
	
	@Override
	public E remove(int index) {
		setAsChanged();
		return super.remove(index);
	}

	@Override
	public boolean remove(Object obj) {
		setAsChanged();
		return super.remove(obj);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		setAsChanged();
		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		setAsChanged();
		return super.retainAll(c);
	}

	@Override
	public E set(int index, E element) {
		setAsChanged();
		return super.set(index, element);
	}

}
