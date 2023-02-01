package logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class PNEditorLogger {

	/**
	 *  Instantiate a logger to be used by the software
	 */
	private final static Logger LOGGER = Logger.getLogger(PNEditorLogger.class.getName());

	
	/**
	 * Specify a fileName.log to write logs
	 * 
	 * @param fileName
	 */
	public static void addFileLog(final String fileName) {
		final SimpleFormatter simpleFormatter = new SimpleFormatter();
		try {
			final FileHandler fileHandler = new FileHandler(fileName + ".log", true);
			fileHandler.setFormatter(simpleFormatter);
			LOGGER.addHandler(fileHandler);
		} catch (SecurityException e) {
			LOGGER.severe("Security Problem: can't add this file");
		} catch (IOException e) {
			LOGGER.severe("Can't add this file");
		}
	}


	/**
	 * Prints on console (+files if created) logs of severe Level
	 * 
	 * @param data
	 */
	public static void severeLogs(final String data) {
		LOGGER.severe(data);
	}

	/**
	 * Prints on console (+files if created) logs of warning Level
	 * 
	 * @param data
	 */
	public static void warningLogs(final String data) {
		LOGGER.warning(data);
	}
	
	/**
	 * Prints on console (+files if created) logs of info level
	 * 
	 * @param data
	 */
	public static void infoLogs(final String data) {
		LOGGER.info(data);
	}

	/**
	 * Prints on console (+files if created) logs of config Level
	 * 
	 * @param data
	 */
	public static void configLogs(final String data) {
		LOGGER.config(data);
	}
	
	/**
	 * Prints on console (+files if created) logs of fine Level
	 * 
	 * @param data
	 */
	public static void fineLogs(final String data) {
		LOGGER.fine(data);
	}
	
	/**
	 * Prints on console (+files if created) logs of finer Level
	 * 
	 * @param data
	 */
	public static void finerLogs(final String data) {
		LOGGER.finer(data);
	}
	
	/**
	 * Prints on console (+files if created) logs of finest Level
	 * 
	 * @param data
	 */
	public static void finestLogs(final String data) {
		LOGGER.finest(data);
	}
}
