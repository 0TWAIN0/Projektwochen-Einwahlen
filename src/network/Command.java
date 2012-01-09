package network;

import informations.General;
import informations.Lehrer;
import informations.Schueler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

import misc.Config;
import misc.Misc;
import misc.Print;

/**
 * 
 * @author Jakob Lochner
 *
 */
public class Command {

	// TODO THREAD SAVE!!
	/**
	 * Führt Login aus.
	 * @param client Verbundener Client
	 * @param args Vom Client übergebene Argumente (Username & Passwort)
	 * @param thread Thread Informationen
	 * @throws FileNotFoundException Datei konnte nicht gefunden werden
	 */
	public static void login(Socket client, String[] args, Thread thread)
			throws FileNotFoundException {
		String[][] arguments = handleArgs(args);
		String username = "";
		String passwort = "";
		boolean login = false;
		Schueler schueler = null;
		Lehrer lehrer = null;
		boolean isLehrer = false;

		// Überprüfen der Richtigkeit von Namen und Passwort
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("user")) {
				username = arguments[i][1];
			} else if (arguments[i][0].equals("pw")) {
				passwort = arguments[i][1];
			}
		}
		
		if (!username.equals("") && !passwort.equals("")) {
			
			Print.deb("User: " + username + " PW: " + passwort);
			Schueler[] schuelerList = General.wahl.getSchuelerList();
			for (int s = 0; s < schuelerList.length; s++) {
				if (schuelerList[s].getName().equals(username)
						&& schuelerList[s].getPasswort().equals(passwort)) {
					login = true;
					schueler = schuelerList[s];
					break;
				}
			}
			if (!login) {
				Lehrer[] lehrerList = General.lehrer;
				for (int l = 0; l < lehrerList.length; l++) {
					if (lehrerList[l].getName().equals(username)
							&& lehrerList[l].getPasswort().equals(passwort)) {
						login = true;
						lehrer = lehrerList[l];
						isLehrer = true;
						Print.deb("Lehrer found!");
						break;
					}
				}

			}
		}
		// Reagieren auf erfolgreichen bzw. nicht erfolgreichen Login
		if (login) {
			if (isLehrer) {
				//Erfolgreicher Login eines Lehrers
				lehrer.setSessionkey();
				lehrer.online = true;
				try {
					TCP.send(client, HTTP.HEADER_MOVED);
					TCP.send(client, "Location: create?sk=" + lehrer.getSessionkey());
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}

			} else {
				//Erfolgreicher Login eines Schuelers
				schueler.setSessionkey();
				schueler.online = true;
				try {
					TCP.send(client, HTTP.HEADER_MOVED);
					TCP.send(client, "Location: vote?sk=" + schueler.getSessionkey());
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
				
			}
		} else {
			//Fehlerhafter Login
			String startseite = Misc.read(Config.getWebroot()
					+ Config.START_PAGE);
			startseite = startseite.replaceAll("%username%", username);
			startseite = startseite.replaceAll("%password%", passwort);
			try {
				Print.msg(thread + " Login failed!");
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, startseite
						+ "<script>alert('Login Fehlgeschlagen! Benutzername oder Passwort falsch!');</script>");
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}

		}
	}

	/**
	 * Anfragen auf geschuetzte Seiten
	 * 
	 * @param client Verbundener Client
	 * @param filePath Datei Pfad zur Seite
	 * @param args Vom Clienten mitgegebene Argumente (Sessionkey)
	 * @param userType Art des Nutzers (Lehrer(0)/Schueler(1))
	 * @param thread Informationen über den Thread
	 * @throws SecurityException Nutzer hat keine Berechtigung
	 * @throws FileNotFoundException Seite konnte nicht gefunden werden
	 */
	public static void protectedFileReqest(Socket client, String filePath, String[] args, int userType,
			Thread thread) throws SecurityException, FileNotFoundException {

		//User identifizieren
		String[][] arguments = handleArgs(args);
		String sessionkey = null;
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			} 
		}
		if (sessionkey.equals(null)){
			throw new SecurityException("Der Benutzer konnte nicht verifiziert werden!");
		} 
		
		boolean authorized = false;
		if (userType == Lehrer.LEHRER){
			Lehrer[] userList = General.lehrer;
			for (int i = 0; i < userList.length;i++){
				if (sessionkey.equals(userList[i].getSessionkey())){
					authorized = true;
				}
			}
		}else if (userType == Schueler.SCHUELER) {
			Schueler[] userList = General.wahl.getSchuelerList();
			for (int i = 0; i < userList.length;i++){
				if (sessionkey.equals(userList[i].getSessionkey())){
					authorized = true;
				}
			}
		}
		
		if (!authorized){
			throw new SecurityException("Der Benutzer konnte nicht verifiziert werden!");
		}
		
		String inhalt = "";
		// Einlesen der Datei
		synchronized (HandleConnections.LOCK) {
			inhalt = Misc.read(filePath);
		}
		
//		if (filePath.equals(Config.getWebroot() + Config.START_PAGE)){
//			inhalt = inhalt.replaceAll("%username%", "");
//			inhalt = inhalt.replaceAll("%password%", "");
//		}

		// Senden der Datei
		Print.msg(thread + " Senden von: " + filePath);
		try {
			TCP.send(client, HTTP.HEADER_OK);
			TCP.send(client, inhalt);
		} catch (IOException e) {
			Print.err(thread + " Fehler beim Sende an einen Client");
		}
	}
	
	/**
	 * Liest die Datei ein und sendet Antwort an Client
	 * 
	 * @param client
	 *            Socket des verbundenen Client
	 * @param filePath
	 *            Pfad zur Datei
	 * @param thread
	 *            Threadinformationen
	 * @throws FileNotFoundException
	 */

	public static void allowedFileReqest(Socket client, String filePath,
			Thread thread) throws FileNotFoundException {
		
		String inhalt = "";

		// Einlesen der Datei
		synchronized (HandleConnections.LOCK) {
			inhalt = Misc.read(filePath);
		}
		
		if (filePath.equals(Config.getWebroot() + Config.START_PAGE)){
			inhalt = inhalt.replaceAll("%username%", "");
			inhalt = inhalt.replaceAll("%password%", "");
		}

		// Senden der Datei
		Print.msg(thread + " Senden von: " + filePath);
		try {
			TCP.send(client, HTTP.HEADER_OK);
			TCP.send(client, inhalt);
		} catch (IOException e) {
			Print.err(thread + " Fehler beim Sende an einen Client");
		}
	}

	private static String[][] handleArgs(String[] args) {
		String[][] arguments = new String[args.length][2];
		for (int i = 0; i < args.length; i++) {
			String[] split = args[i].split("=");
			arguments[i][0] = split[0];
			arguments[i][1] = split[1];
		}
		return arguments;
	}
}
