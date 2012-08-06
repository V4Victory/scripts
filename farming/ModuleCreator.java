package scripts.farming;

import state.Module;
import state.State;

public abstract class ModuleCreator {
	public abstract Module[] createModules(State initial, State success, State critical);
}
