package ejb.logger;

import java.io.PrintStream;

public class Log {
	private static PrintStream msDefaultWriter = System.out;
	
	private static void displayLog(String inMessage) {
		msDefaultWriter.println(inMessage);
	}
	
	private static void displayLog(String inMessage, PrintStream inWriter) {
		inWriter.println(inMessage);
	}
	
	public static void setDefaultWriter(PrintStream inWriter) {
		msDefaultWriter = inWriter;
	}
	
	public static void info(String inTag, String inMessage) {
		displayLog(inTag + ":\t" + inMessage);
	}
	
	public static void info(String inTag, String inMessage, PrintStream inWriter) {
		displayLog(inTag + ":\t" + inMessage, inWriter);
	}
	
}
