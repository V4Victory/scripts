package scripts.farming.modules;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.powerbot.qd;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.concurrent.strategy.StrategyDaemon;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.Context;

import state.Condition;
import state.Module;
import state.State;
import state.edge.ExceptionSafeTask;
import state.edge.Option;
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

		scripts = new HashSet<Class<?>>(getClassesForPackage(""));

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

	/**
	 * Attempts to list all the classes in the specified package as determined
	 * by the context class loader
	 * 
	 * @param pckgname
	 *            the package name to search
	 * @return a list of classes that exist within that package
	 * @throws ClassNotFoundException
	 *             if something went wrong
	 */
	private static List<Class<?>> getClassesForPackage(String pckgname) {
		// This will hold a list of directories matching the pckgname. There may
		// be more than one if a package is split over multiple jars/paths
		ArrayList<File> directories = new ArrayList<File>();
		try {
			try {
				ClassLoader cld = Thread.currentThread()
						.getContextClassLoader();
				if (cld == null) {
					throw new ClassNotFoundException("Can't get class loader.");
				}
				String path = pckgname.replace('.', '/');
				// Ask for all resources for the path
				Enumeration<URL> resources = cld.getResources(path);
				while (resources.hasMoreElements()) {
					directories.add(new File(URLDecoder.decode(resources
							.nextElement().getPath(), "UTF-8")));
				}
			} catch (NullPointerException x) {
				throw new ClassNotFoundException(
						pckgname
								+ " does not appear to be a valid package (Null pointer exception)");
			} catch (UnsupportedEncodingException encex) {
				throw new ClassNotFoundException(
						pckgname
								+ " does not appear to be a valid package (Unsupported encoding)");
			} catch (IOException ioex) {
				throw new ClassNotFoundException(
						"IOException was thrown when trying to get all resources for "
								+ pckgname);
			}

			ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
			// For every directory identified capture all the .class files
			for (File directory : directories) {
				if (directory.exists()) {
					// Get the list of the files contained in the package
					String[] files = directory.list();
					for (String file : files) {
						// we are only interested in .class files
						if (file.endsWith(".class")) {
							// removes the .class extension
							try {
								classes.add(Class.forName(pckgname + '.'
										+ file.substring(0, file.length() - 6)));
							} catch (NoClassDefFoundError e) {
								// do nothing. this class hasn't been found by
								// the
								// loader, and we don't care.
							}
						}
					}
				} else {
					throw new ClassNotFoundException(pckgname + " ("
							+ directory.getPath()
							+ ") does not appear to be a valid package");
				}
			}
			return classes;
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());// reflection.getTypesAnnotatedWith(annotation);
			return new ArrayList<Class<?>>();
		}
	}
}
