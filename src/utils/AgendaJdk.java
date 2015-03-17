package utils;

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.Stream;

public class AgendaJdk<T> implements Agenda<T> {

	private AbstractList<T>	data;
	private Function				usedFunction;

	public enum Function {
		STACK, QUEUE;
	}

	public AgendaJdk(Function function) {
		usedFunction=function;
		
		switch (function) {
			case STACK:
				data= new Stack<>();
				break;
				
			case QUEUE:
				data= new LinkedList<>();
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
		if (usedFunction==Function.STACK) {
			return ((Stack<T>)data).pop();
		} else {
			return ((LinkedList<T>)data).pop();
		}
	}

	@Override
	public T peek() {
		if (usedFunction==Function.STACK) {
			return ((Stack<T>)data).peek();
		} else {
			return ((LinkedList<T>)data).peek();
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
		return contains(item);
	}

	@Override
	public Stream<T> stream() {
		return data.stream();
	}

}
