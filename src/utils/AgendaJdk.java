package utils;

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * Just wrapper using JDK Stack and Queue (linked List). Implementing agenda so
 * the BFS and DFS solvers can use JDK and mine implementations without any
 * change in their code
 * 
 * @author Anton Krug
 * @date 2015/03/12
 * @version 0.2
 *
 * @param <T>
 */

public class AgendaJdk<T> implements Agenda<T> {

	private AbstractList<T>	data;

	public enum Function {
		STACK, QUEUE;
	}

	public AgendaJdk(Function function) {
		switch (function) {
			case STACK:
				data = new Stack<>();
				break;

			case QUEUE:
				data = new LinkedList<>();
				break;
		}
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public void add(T item) {
		data.add(item);
	}

	@Override
	public void push(T item) {
		data.add(item);
	}

	@Override
	public T pop() {
		if (data instanceof Stack<?>) {
			return ((Stack<T>) data).pop();
		} else {
			return ((LinkedList<T>) data).pop();
		}
	}

	@Override
	public T peek() {
		if (data instanceof Stack<?>) {
			return ((Stack<T>) data).peek();
		} else {
			return ((LinkedList<T>) data).peek();
		}
	}

	@Override
	public void remove(int index) {
		data.remove(index);
	}

	@Override
	public void remove(T item) {
		data.remove(item);
	}

	@Override
	public boolean contains(T item) {
		return data.contains(item);
	}

	@Override
	public Stream<T> stream() {
		return data.stream();
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
	
	@Override
	public AbstractList<T> getList() {
		return data;
	}

}
