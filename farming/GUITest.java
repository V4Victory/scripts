package scripts.farming;

import java.io.FileDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.security.Permission;

import org.powerbot.ef;
import org.powerbot.qe;
import org.powerbot.se;
import org.powerbot.game.api.ActiveScript;

public class GUITest {

	public static class MyScript extends ActiveScript {

		@Override
		protected void setup() {
			System.out.println("Setup!");
		}
	}

	public static class ScriptWrapper<D extends ActiveScript> {
		Class<D> scriptClass;

		public ScriptWrapper(Class<D> sc) {
			scriptClass = sc;
		}

		public ActiveScript newInstance() {
			Constructor<D> c;
			try {
				c = scriptClass.getConstructor();
				return c.newInstance();
			} catch (NoSuchMethodException | SecurityException
					| InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		}

		public ActiveScript newInstance2() {
			Constructor<D> c;
			try {
				return scriptClass.newInstance();
			} catch (SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		}
	}

	public static class MyScriptWrapper extends ScriptWrapper<MyScript> {

		public MyScriptWrapper() {
			super(MyScript.class);
		}

	}

	public static class NullSecurityManager extends SecurityManager {
		public void checkCreateClassLoader() {
		}

		public void checkAccess(Thread g) {
		}

		public void checkAccess(ThreadGroup g) {
		}

		public void checkExit(int status) {
		}

		public void checkExec(String cmd) {
		}

		public void checkLink(String lib) {
		}

		public void checkRead(FileDescriptor fd) {
		}

		public void checkRead(String file) {
		}

		public void checkRead(String file, Object context) {
		}

		public void checkWrite(FileDescriptor fd) {
		}

		public void checkWrite(String file) {
		}

		public void checkDelete(String file) {
		}

		public void checkConnect(String host, int port) {
		}

		public void checkConnect(String host, int port, Object context) {
		}

		public void checkListen(int port) {
		}

		public void checkAccept(String host, int port) {
		}

		public void checkMulticast(InetAddress maddr) {
		}

		public void checkMulticast(InetAddress maddr, byte ttl) {
		}

		public void checkPropertiesAccess() {
		}

		public void checkPropertyAccess(String key) {
		}

		public void checkPropertyAccess(String key, String def) {
		}

		public boolean checkTopLevelWindow(Object window) {
			return true;
		}

		public void checkPrintJobAccess() {
		}

		public void checkSystemClipboardAccess() {
		}

		public void checkAwtEventQueueAccess() {
		}

		public void checkPackageAccess(String pkg) {
		}

		public void checkPackageDefinition(String pkg) {
		}

		public void checkSetFactory() {
		}

		public void checkMemberAccess(Class clazz, int which) {
		}

		public void checkSecurityAccess(String provider) {
		}

		public void checkPermission(Permission p) {
		}

		public void checkPermission(Permission p, Object o) {
		}
	}

	public static class CustomSecurityManager extends NullSecurityManager {
		public void checkCreateClassLoader() {
			throw new SecurityException();
		}
	}
	
	public static class MyClassLoader extends ClassLoader {
		public MyClassLoader() {
			super(ClassLoader.getSystemClassLoader());
		}
	}

	public static void main(String[] args) {
		try {
		Field f = org.powerbot.dc.class.getDeclaredField("z");
		f.setAccessible(true);
		String[] z = (String[])f.get(null);
		for(String s : z) {
			System.out.println(s);
		}

		
		se sei = se.a();
		ef efi = new ef(sei);
		qe qei = new qe(efi);
		//qei.setVisible(false);
		System.out.println(qei.getComponentCount());
		} catch(Exception e) {
			e.printStackTrace();
		}

		System.setSecurityManager(new CustomSecurityManager());
		try {
			Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("scripts.farming.GUITest");
			clazz.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//ClassLoader cl = new MyClassLoader();
		MyScriptWrapper mysw = new MyScriptWrapper();
		try {
			ActiveScript as = mysw.newInstance();
			Method setup = ActiveScript.class.getDeclaredMethod("setup");
			setup.setAccessible(true);
			setup.invoke(as);
			
			Class<?> clazz = MyScript.class;
			as = (ActiveScript)clazz.newInstance();
			setup = ActiveScript.class.getDeclaredMethod("setup");
			setup.setAccessible(true);
			setup.invoke(as);
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
