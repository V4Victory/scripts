package scripts.state.tools;

import java.util.Collection;
import java.util.Iterator;

public class Cons<T> {
	T head;
	Cons<T> tail;
	
	boolean empty = true;
	
	public Cons() {
		this(null,null);
		empty = true;
	}
	
	public Cons(T head_, Cons<T> tail_) {
		head = head_;
		tail = tail_;
		empty = false;
	}
	
	public Cons(Iterator<T> it) {
		if(it.hasNext()) {
			head = it.next();
			tail = new Cons<T>(it);
			empty = false;
		} else {
			head = null;
			tail = null;
			empty = true;
		}
	}
	
	public T getHead() {
		return head;
	}
	
	public Cons<T> getTail() {
		return tail;
	}
	
	public boolean isEmpty() {
		return empty;
	}
}
