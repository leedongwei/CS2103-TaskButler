// @author A0097836L
package butler.common;

import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerPreset {

	private static final String CONFIG_FILE = "logging.properties";
	private static final int REQUESTER_INDEX = 2;

	private LoggerPreset() {

	}

	/**
	 * Reads the logging configuration file and initializes the global logger
	 * behavior.
	 */
	public static void initializeGlobalLogger() {

		try {
			InputStream config = LoggerPreset.class.getResourceAsStream(CONFIG_FILE);
			LogManager.getLogManager().readConfiguration(config);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Use this method to instantiate loggers within the application.
	 * 
	 * @return a Logger object configured with the name of the calling class
	 */
	public static Logger getLogger() {

		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement logRequester = stackTrace[REQUESTER_INDEX];
		Logger logger = Logger.getLogger(logRequester.getClassName());

		return logger;
	}

}
