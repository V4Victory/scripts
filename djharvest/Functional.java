package djharvest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Functional {
	public static interface Function<S,T> {
		public S operate(T val);
	}
	
	public static <S,T> List<S> map(Iterable<T> vals, Function<S,T> func) {
		List<S> results = new ArrayList<S>();
		for(T t : vals) {
			results.add(func.operate(t));
		}
		return results;
	}


	public static <S> S max(Iterable<S> vals, Function<Integer,S> func, Integer maxv) {
		S sv = null;
		for(S s : vals) {
			Integer v = func.operate(s);
			if(v>maxv) {
				maxv = v;
				sv = s;
			}
		}
		return sv;
	}
}
