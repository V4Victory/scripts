package scripts.farming;

import java.io.File;
import java.io.FileDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.security.Permission;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Environment;

import scripts.farming.modules.RunOtherScript;
import state.Condition;
import state.Module;
import state.State;
import state.tools.OptionSelector;

public class GUITest {

	public static class MyScript extends ActiveScript {

		@Override
		protected void setup() {
			System.out.println("Setup!");
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
		final GUI gui;
		ScriptLoader loader = new ScriptLoader();
		gui = new GUI(new File("farming-settings.ini"), loader);

	}

}
