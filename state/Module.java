package scripts.state;

import scripts.farming.modules.Requirement;

public class Module {
	State initial, success, critical;
	
	String description;
	Requirement[] requirements;
	
	public Module(String description_, State initial_, State success_, State critical_) {
		this(description_, initial_, success_, critical_, new Requirement[]{});
	}
	
	public Module(String description_, State initial_, State success_, State critical_, Requirement[] requirements_) {
		description = description_;
		initial =  initial_;
		success = success_;
		critical = critical_;
		requirements = requirements_;
	}
	
	public State getInitialState() { return initial; }
	public State getSuccessState() { return success; }
	public State getCriticalState() { return critical; }
	public String toString() { return description; }
	public Requirement[] getRequirements() { return requirements; }
}
