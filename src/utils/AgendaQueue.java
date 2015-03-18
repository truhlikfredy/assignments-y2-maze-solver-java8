package utils;

import java.util.LinkedList;

/**
 * Extension from Stack to Queue (just overiding methods which differ between
 * FIFO and LIFO).
 * 
 * (implements by inheritance Agenda so it can be changed between others)
 * 
 * @author Anton Krug
 * @date 2015/03/12
 * @version 0.2
 *
 * @param <T>
 */

public class AgendaQueue<T> extends AgendaStack<T> {

	public AgendaQueue() {
		data = new LinkedList<>();
		size = 0;
	}

	@Override
	public T pop() {
		T ret = data.get(0);
		data.remove(0);
		size--;
		return ret;
	}

	@Override
	public T peek() {
		return data.get(0);
	}

}
