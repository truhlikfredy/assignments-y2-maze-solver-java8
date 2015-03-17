package utils;

import java.util.stream.Stream;

/**
 * Interface for LIFO stack and FIFO queue and regular JDK equivalents.
 * 
 * @author Anton Krug
 * @date 2015/03/12
 * @version 0.2
 *
 * @param <T>
 */

public interface Agenda<T> {
	
	public boolean isEmpty();

	public int size();

	public void add(T item);
	
	public void push(T item);

	public T pop();

	public T peek();

	public void remove(int index);

	public void remove(T item);

	public boolean contains(T item);

	public Stream<T> stream();

}
