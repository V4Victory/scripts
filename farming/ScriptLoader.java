package scripts.farming;

import java.util.Set;

public class ScriptLoader {
	

	Set<Class<?>> scripts;
	
	public Set<Class<?>> getScripts() {
		System.out.println("GET SCRIPTS");
		if(scripts == null) {
			scripts = ClassHelper.getAnnotatedClasses("scripts.wrapper", ScriptWrapper.class);
		}
		return scripts;
	}
}
