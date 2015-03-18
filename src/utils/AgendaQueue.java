package utils;

import java.util.LinkedList;

public class AgendaQueue<T> extends AgendaStack<T> {
	
	public AgendaQueue() {
		data = new LinkedList<>();
		size = 0;
	}
	
	public T pop() {
		T ret = data.get(0);
		data.remove(0);
		size--;
		return ret;
	}
	
	public T peek() {
		return data.get(0);
	}

}
