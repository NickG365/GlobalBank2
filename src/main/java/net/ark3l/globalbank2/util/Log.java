package net.ark3l.globalbank2.util;

import java.util.logging.Logger;

public class Log {

	private static final Logger log = Logger.getLogger("Minecraft");
	public static boolean verbose = false;

	public static void info(String msg) {
		log.info("[GlobalBank2] " + msg);
	}

	public static void warning(String msg) {
		log.warning("[GlobalBank2] " + msg);
	}

	public static void severe(String msg) {
		log.severe("[GlobalBank2] " + msg);
	}

}