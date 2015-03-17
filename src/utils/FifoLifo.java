package utils;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

/**
 * Implementation for LIFO stack and FIFO queue implementing List. Which should
 * be replacement for LinkedList (queue) or Stack.
 * 
 * @author Anton Krug
 * @date 2015/03/12
 * @version 0.2
 *
 * @param <CONTENT_TYPE>
 */
public class FifoLifo<Point> extends Stack<Point> {
//	public class FifoLifo<Point>  {
	private List<Point>	content;
	private ListImplementation	implementation;
	private int size;
	private Point[] values;

	public enum ListImplementation {
		QUEUE, STACK;
	}

	public FifoLifo(ListImplementation implementation) {
		super();
//		Point one = new Point(10,10);
		
//		this.content = (Point []) Array.newInstance(one.getClass().getComponentType(), 250000);
		
		this.implementation = implementation;
		this.clear();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return (size > 0) ? false : true;
	}

	@Override
	public boolean contains(Object o) {
		for (Point item : content) {
			if (item.equals(o)) return true;
		}
		return false;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}

	@Override
	public boolean remove(Object o) {
		size--;
		return content.remove(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return content.containsAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
		switch (implementation) {
			case QUEUE:
				content = new LinkedList<Point>();
				break;

			case STACK:
				content = new ArrayList<Point>();
				break;
		}
		size=0;
	}


}
