package utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.stream.Stream;

public class AgendaStack<T> implements Agenda<T> {
	protected AbstractList<T>	data;
	protected int							size;

	public AgendaStack() {
		size = 0;
		data = new ArrayList<>();
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
		data.add(size, item);
		size++;
	}

	@Override
	public void push(T item) {
		add(item);
	}

	@Override
	public T pop() {
		size--;
		return data.get(size);
	}

	@Override
	public T peek() {
		return data.get(size - 1);
	}

	@Override
	public void remove(int index) {
		for (int i = index + 1; i < size; i++) {
			data.set(i - 1, data.get(i));
		}
		size--;
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
		for (T entry : data) {
			if (entry.equals(item)) return true;
		}
		return false;
	}

	@Override
	public Stream<T> stream() {
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
		// deleting the junk we left behind before giving acces to internat
		// datastructure
		data.subList(size, data.size()).clear();
		return data;
	}

}
