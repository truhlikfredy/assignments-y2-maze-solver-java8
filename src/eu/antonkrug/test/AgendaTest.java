package eu.antonkrug.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import utils.Agenda;
import utils.AgendaJdk;
import utils.AgendaQueue;
import utils.AgendaStack;
import utils.AgendaJdk.Function;


/**
 * All agenda datastructure related tests
 * 
 * @author Anton Krug
 * @date 2015/03/12
 * @version 1.0
 */
public class AgendaTest {

	private AgendaJdk<Integer>		jdkStack;
	private AgendaJdk<Integer>		jdkQueue;
	private AgendaStack<Integer>	stack;
	private AgendaStack<Integer>	queue;

	@Before
	public void setUp() throws Exception {
		jdkStack = new AgendaJdk<>(Function.STACK);
		jdkQueue = new AgendaJdk<>(Function.QUEUE);
		stack = new AgendaStack<>();
		queue = new AgendaQueue<>();

		jdkStack.add(1);
		jdkStack.add(2);
		jdkStack.add(3);
		jdkStack.add(4);
		jdkStack.add(4);
		jdkStack.add(5);

		jdkQueue.add(1);
		jdkQueue.add(2);
		jdkQueue.add(3);
		jdkQueue.add(4);
		jdkQueue.add(4);
		jdkQueue.add(5);

		stack.add(1);
		stack.add(2);
		stack.add(3);
		stack.add(4);
		stack.add(4);
		stack.add(5);

		queue.add(1);
		queue.add(2);
		queue.add(3);
		queue.add(4);
		queue.add(4);
		queue.add(5);
	}

	private void popAgenda(Integer result, Agenda<Integer> data) {
		assertEquals(6, data.size());
		assertEquals(result, data.pop());
		assertEquals(5, data.size());
	}

	@Test
	public void popTest() {
		popAgenda(5, jdkStack);
		popAgenda(5, stack);
		
		popAgenda(1, jdkQueue);
		popAgenda(1, queue);
	}

	private void popPushAgenda(String result, Integer resultA, Integer resultB , Agenda<Integer> data) {
		data.add(90);
		data.push(22);
		assertEquals(8, data.size());
		assertEquals(resultA, data.pop());
		data.push(55);
		assertEquals(resultB, data.pop());
		data.pop();
		data.push(89);		
		assertEquals(result, data.toString());
		assertEquals(7, data.size());
		assertEquals(7, data.getList().size());
	}
	
	@Test
	public void popPushTest() {
		popPushAgenda("[1, 2, 3, 4, 4, 5, 89]",22,55, jdkStack);
		popPushAgenda("[1, 2, 3, 4, 4, 5, 89]",22,55, stack);
		
		popPushAgenda("[4, 4, 5, 90, 22, 55, 89]",1,2, jdkQueue);
		popPushAgenda("[4, 4, 5, 90, 22, 55, 89]",1,2, queue);
	}
	
	private void peekAgenda(Integer result, Agenda<Integer> data) {
		assertEquals(6, data.size());
		assertEquals(result, data.peek());
		assertEquals(6, data.size());
	}

	@Test
	public void peekTest() {
		peekAgenda(5, jdkStack);
		peekAgenda(5, stack);
		
		peekAgenda(1, jdkQueue);
		peekAgenda(1, queue);
	}

	private void sizeAgenda(Agenda<Integer> data) {
		assertEquals(6, data.size());
		data.pop();
		data.pop();
		data.pop();
		data.pop();
		data.pop();
		data.pop();
		assertEquals(0, data.size());
		assertEquals(false, data.contains((Integer)(2)));
		assertEquals(true, data.isEmpty());
	}

	@Test
	public void sizeTest() {
		sizeAgenda(jdkStack);
		sizeAgenda(stack);
		
		sizeAgenda(jdkQueue);
		sizeAgenda(queue);
	}

	private void containsAgenda(Agenda<Integer> data) {
		assertEquals(true, data.contains(3));
		assertEquals(true, data.contains(4));
		assertEquals(false, data.contains(89));
		assertEquals(true, data.contains(1));
	}

	@Test
	public void containsTest() {
		containsAgenda(jdkStack);
		containsAgenda(stack);
		
		containsAgenda(jdkQueue);
		containsAgenda(queue);
	}

	private void removeAgenda(Agenda<Integer> data) {
		data.remove(2);
		assertEquals(5, data.size());
		assertEquals(5, data.getList().size());
		assertEquals("[1, 2, 4, 4, 5]", data.toString());
	}

	private void removeObjectAgenda(Agenda<Integer> data) {
		data.remove((Integer) 5);
		assertEquals(4, data.size());
		assertEquals(4, data.getList().size());
		assertEquals("[1, 2, 4, 4]", data.toString());

		//non existing one
		data.remove((Integer) 15);
		assertEquals(4, data.size());
		assertEquals(4, data.getList().size());
		assertEquals("[1, 2, 4, 4]", data.toString());
	}


	@Test
	public void removeTest() {
		// System.out.println(jdkStack);
		// System.out.println(stack);

		removeAgenda(jdkStack);
		removeAgenda(stack);
		
		removeAgenda(jdkQueue);
		removeAgenda(queue);

		removeObjectAgenda(jdkStack);
		removeObjectAgenda(stack);
		
		removeObjectAgenda(jdkQueue);
		removeObjectAgenda(queue);
	}

	private void addRemoveAgenda(Agenda<Integer> data) {
		data.remove(2);
		data.add(9);
		assertEquals(6, data.size());
		assertEquals(6, data.getList().size());
		assertEquals("[1, 2, 4, 4, 5, 9]", data.toString());
	}

	private void addRemoveObjectAgenda(Agenda<Integer> data) {
		data.remove((Integer) 5);
		data.add(81);
		assertEquals(6, data.size());
		assertEquals(6, data.getList().size());
		assertEquals("[1, 2, 4, 4, 9, 81]", data.toString());
		
		//non existing one
		data.remove((Integer) 15);
		assertEquals(6, data.size());
		assertEquals(6, data.getList().size());
		assertEquals("[1, 2, 4, 4, 9, 81]", data.toString());
	}

	@Test
	public void addRemoveTest() {
		// System.out.println(jdkStack);
		// System.out.println(stack);
		
		addRemoveAgenda(jdkStack);
		addRemoveAgenda(stack);
		
		addRemoveAgenda(jdkQueue);
		addRemoveAgenda(queue);
		
		addRemoveObjectAgenda(jdkStack);
		addRemoveObjectAgenda(stack);
		
		addRemoveObjectAgenda(jdkQueue);
		addRemoveObjectAgenda(queue);
	}

}
