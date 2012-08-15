package scripts.state.tools;

public class Cons<T> {
	T head;
	Cons<T> tail;
	
	boolean empty = true;
	
	public Cons() {
		this(null,null);
	}
	
	public Cons(T head_, Cons<T> tail_) {
		head = head_;
		tail = tail_;
		empty = false;
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
