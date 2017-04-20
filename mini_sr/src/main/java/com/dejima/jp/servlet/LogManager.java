package com.dejima.jp.servlet;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides logging functions.
 * 
 * @author jito
 */
public class LogManager {

	/**
	 * System log.
	 */
	private static final Logger systemLog;

	/**
	 * Access log.
	 */
	private static final Logger accessLog;

	/**
	 * Initialize log system.
	 */
	static {
		systemLog = Logger.getLogger(Httpd.class.getName() + ".system");
		accessLog = Logger.getLogger(Httpd.class.getName() + ".access");
	}

	public static void debug(String msg) {
		log(Level.CONFIG, msg);
	}

	public static void info(String msg) {
		log(Level.INFO, msg);
	}

	public static void warn(String msg) {
		log(Level.WARNING, msg);
	}

	public static void warn(Throwable ex) {
		log(Level.WARNING, ex.getMessage(), ex);
	}

	public static void error(String msg, Throwable ex) {
		log(Level.SEVERE, msg, ex);
	}

	public static void error(Throwable ex) {
		log(Level.SEVERE, ex.getMessage(), ex);
	}

	private static void log(Level level, String msg) {
		StackTraceElement[] trace = (new Exception()).getStackTrace();

		systemLog.logp(level, trace[2].getClassName(), trace[2].getMethodName(), msg);
	}

	private static void log(Level level, String msg, Throwable ex) {
		StackTraceElement[] trace = ex.getStackTrace();

		systemLog.logp(level, trace[0].getClassName(), trace[0].getMethodName(), msg, ex);
	}

	public static void accessLog(String msg) {
		accessLog.info(msg);
	}

}
