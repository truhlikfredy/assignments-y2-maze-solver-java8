package utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * My implementation of Stack datastructure (implements Agenda so it can be
 * changed between others)
 * 
 * @author Anton Krug
 * @date 2015/03/12
 * @version 0.8
 *
 * @param <T>
 */

public class AgendaStack<T> implements Agenda<T> {
	protected AbstractList<T>	data;
	protected int							size;

	public AgendaStack() {
		size = 0;
		data = new ArrayList<>();
	}

	/**
	 * Delete the junk we left behind before accesing directly the internal
	 * datastructure. Problem is that size and data.size can be different and
	 * accesing data directly at that moment would give unexpected results. So
	 * triming the data is necesary.
	 */
	private void purge() {
		data.subList(size, data.size()).clear();
	}

	@Override
	public boolean isEmpty() {
		return (size == 0) ? true : false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void add(T item) {
		// data.add(size, item);
		// data.subList(size, data.size()).clear();
		// data.add(item);

		if (item != null) {
			if (size == data.size()) {
				data.add(item);
			} else {
				data.set(size, item);
			}
			size++;
		}
	}

	@Override
	public void push(T item) {
		add(item);
	}

	@Override
	public T pop() {
		if (size > 0) {
			size--;
			return data.get(size);
		} else return null;
	}

	@Override
	public T peek() {
		if (size > 0) {
			return data.get(size - 1);
		} else return null;
	}

	@Override
	public void remove(int index) {
		if (index >= 0 && index < size) {
			for (int i = index + 1; i < size; i++) {
				data.set(i - 1, data.get(i));
			}
			size--;
		}
	}

	@Override
	public void remove(T item) {
		remove(indexOf(item));
	}

	protected int indexOf(T item) {
		for (int index = 0; index < size; index++) {
			if (data.get(index).equals(item)) return index;
		}
		return -1;
	}

	@Override
	public boolean contains(T item) {
		purge();
		for (T entry : data) {
			if (entry.equals(item)) return true;
		}
		return false;
	}

	@Override
	public Stream<T> stream() {
		purge();
		return data.stream();
	}

	@Override
	public String toString() {
		String tmp = "[";
		for (int index = 0; index < size; index++) {
			if (index > 0) tmp = tmp + ", ";
			tmp = tmp + data.get(index).toString();
		}
		tmp = tmp + "]";
		return tmp;
	}

	@Override
	public AbstractList<T> getList() {
		purge();
		return data;
	}

}
