package misc;


/**
 * Sammlung von Methoden zur Informationsausgabe in der Konsole.
 * 
 */
public class Print {
	/**
	 * Informationsausgabe mit dem Prefix "[Info]"
	 * @param msg
	 */
	public static void msg(String msg) {
		System.out.println("[Info]  " + msg);
	}

	/**
	 * Errorausgabe mit dem Prefix "[Error]"
	 * @param msg
	 */
	public static void err(String msg) {
		System.out.println("[Error] " + msg);
	}
	
	/**
	 * Debugausgabe mit dem Prefix "[DEBUG]"
	 * @param msg
	 */
	public static void deb(String msg) {
		if (!Config.DEBUGING){
			return;
		}
		//log(msg);
		System.out.println("[DEBUG] " + msg);
	}
	
	public static void deb(int msg) {
		if (!Config.DEBUGING){
			return;
		}
		System.out.println("[DEBUG] " + msg);
	}
	
	public static void deberr(String msg) {
		if (!Config.DEBUGING){
			return;
		}
		//log(msg);
		System.out.println("[ERROR] " + msg);
	}
	/**
	 * eingerückte Debugausgabe
	 * @param msg
	 */
	public static void debtab(String msg) {
		if (!Config.DEBUGING){
			return;
		}
		//log(msg);
		System.out.println("        " + msg);
		
	}
	/**
	 * eingerückte Textausgabe
	 * @param msg
	 */
	public static void tab(String msg) {
		System.out.println("        " + msg);
	}
	
//	private static void log(String msg){
//		//auswertung.log
//		//TODO Funktioniert noch nicht!
//		String path = "auswertung.log";
//		try {
//			Misc.write(new File(path), msg);
//		} catch (FileNotFoundException e) {
//			Print.err("Logfile konnte nicht geöffnet werden! Path: " + path);
//		}
//	}
}
