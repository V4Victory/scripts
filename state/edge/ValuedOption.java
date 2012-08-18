package scripts.state.edge;

import java.util.Map.Entry;

import scripts.state.Condition;
import scripts.state.State;
import scripts.state.tools.OptionSelector;

public class ValuedOption<T> extends Option<T> {
	//Map<T,Edge> edges = new HashSet<T,Edge>();
	OptionCreator<T> creator;
	
	public ValuedOption(Condition c, OptionSelector<T> selector_, OptionCreator<T> creator_) {
		super(null, selector_);
		setCondition(c.and(new Condition() {
			public boolean validate() {
				state.removeAllEdges();
				for(Entry<T,State> entry : creator.create().entrySet()) {
					add(entry.getKey(),entry.getValue());
				}
				return true;
			}
		}));
		creator = creator_;
	}
}
