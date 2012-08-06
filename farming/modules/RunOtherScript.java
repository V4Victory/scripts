package scripts.farming.modules;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.concurrent.strategy.StrategyDaemon;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.Context;
import org.reflections.Reflections;

import state.Condition;
import state.Module;
import state.State;
import state.edge.ExceptionSafeTask;
import state.edge.Option;
import state.edge.Task;
import state.tools.OptionSelector;

public class RunOtherScript extends Module {
	
	public String toString() {
		return "Run Other Script";
	}

	Set<Class<?>> scripts;
	List<Strategy> newStrategies;
	Class<?> runningScript;
	ActiveScript activeScript;

	public RunOtherScript(State initial, State success, State critical,
			OptionSelector<Class<?>> selector, final Condition run,
			final Condition interrupt, Class<? extends Annotation> annotation) {
		super(initial, success, critical);
		Reflections reflection = new Reflections();
		scripts = reflection.getTypesAnnotatedWith(annotation);
		Option<Class<?>> option = new Option<Class<?>>(run, selector);
		initial.add(option);
		for (final Class<?> script : scripts) {
			final State prep = new State();
			final State state = new State();

			option.add(script, new State().add(new ExceptionSafeTask(
					Condition.TRUE, prep, critical) {
				public void run() throws Exception {
					script.getDeclaredMethod("prepare").invoke(null);
					runningScript = script;
					activeScript = (ActiveScript) script.getDeclaredMethod(
							"newInstance").invoke(null);

					Field botField = Context.class.getDeclaredField("bot");
					botField.setAccessible(true);
					Bot bot = (Bot) botField.get(Context.get());
					Context newContext = new Context(bot);
					activeScript.init(newContext);

					Method setupMethod = activeScript.getClass()
							.getDeclaredMethod("setup");
					setupMethod.setAccessible(true);
					setupMethod.invoke(activeScript);
					Method provideMethod = activeScript.getClass()
							.getDeclaredMethod("provide");
					provideMethod.setAccessible(true);

					Field executorField = ActiveScript.class
							.getDeclaredField("executor");
					executorField.setAccessible(true);
					StrategyDaemon sd = (StrategyDaemon) executorField
							.get(activeScript);
					Field strategiesField = StrategyDaemon.class
							.getDeclaredField("strategies");
					strategiesField.setAccessible(true);

					List<Strategy> strategies = (List<Strategy>) strategiesField
							.get(sd);

					newStrategies = new ArrayList<Strategy>();

					for (Strategy strategy : strategies) {

						Field policyField = Strategy.class
								.getDeclaredField("policy");
						policyField.setAccessible(true);
						final org.powerbot.concurrent.strategy.Condition condition = (org.powerbot.concurrent.strategy.Condition) policyField
								.get(strategy);
						Field tasksField = Strategy.class
								.getDeclaredField("tasks");
						tasksField.setAccessible(true);
						final org.powerbot.concurrent.Task[] tasks = (org.powerbot.concurrent.Task[]) tasksField
								.get(strategy);
						Strategy newStrategy;

						provideMethod.invoke(newStrategy = new Strategy(
								(Condition) run.and(condition), tasks));
						newStrategies.add(newStrategy);
					}

				}
			}));
			state.add(new ExceptionSafeTask(interrupt, success, critical) {
				public void run() throws Exception {
					Method revokeMethod = activeScript.getClass()
							.getDeclaredMethod("revoke");

					revokeMethod.setAccessible(true);
					for (Strategy strategy : newStrategies) {
						revokeMethod.invoke(strategy);
					}
					runningScript.getDeclaredMethod("cleanup").invoke(null);
				}
			});
		}
	}

	public Set<Class<?>> getScripts() {
		return scripts;
	}
}
