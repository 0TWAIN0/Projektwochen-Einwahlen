package network;

import informations.General;
import informations.Kurs;
import informations.Lehrer;
import informations.Schueler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

import misc.Config;
import misc.Misc;
import misc.Print;

public class Command {

	// TODO THREAD SAVE!!
	public static void createKurs(Socket client, String[] args, int userType,
			Thread thread) throws SecurityException, FileNotFoundException{
		if (args == null){
			throw new SecurityException("Der Benutzer konnte nicht verifiziert werden!");
		}
		
		//User identifizieren
		String[][] arguments = handleArgs(args);
		String sessionkey = null;
		String name = "";
		int size = 0;
		int min = 0;
		int max = 0;
		String description = "";
		
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			}else if (arguments[i][0].equals("name")) {
				name = arguments[i][1];
			} else if (arguments[i][0].equals("size")) {
				try{
					size = Integer.valueOf(arguments[i][1]);
				}catch(NumberFormatException e){
					size = 0;
				}
			} else if (arguments[i][0].equals("min")) {
				try{
					min = Integer.valueOf(arguments[i][1]);
				}catch(NumberFormatException e){
					min = 0;
				}
			} else if (arguments[i][0].equals("max")) {
				try{
					max = Integer.valueOf(arguments[i][1]);
				}catch(NumberFormatException e){
					max = 0;
				}
			} else if (arguments[i][0].equals("desc")) {
				description = arguments[i][1];
			} 
		}
		
		if (!checkSK(sessionkey, userType)){
			throw new SecurityException("Der Benutzer konnte nicht verifiziert werden!");
		}
		
		Print.deb(thread + "Nutzer verifiziert!");
		
		String errorMessage = "";
		if (name.equals("")){
			errorMessage = errorMessage + "Es wurde kein Kursname angegeben!%0A";
		}
		if (size <=0 || size >= Config.MAXIMAL_KURS_GROESSE ){
			errorMessage = errorMessage + "Die Kursgr%F6%DFe ist entweder zu klein oder zu gro%DF!%0A";
		}
		if (min < Config.MINIMAL_JAHRGANG || min > Config.MAXIMAL_JAHRGANG){
			errorMessage = errorMessage + "Die untere Grenze der Jahrgangsberechtigung ist nicht korrekt!%0A";
		}
		if (max < Config.MINIMAL_JAHRGANG || max > Config.MAXIMAL_JAHRGANG){
			errorMessage = errorMessage + "Die obere Grenze der Jahrgangsberechtigung ist nicht korrekt!%0A";
		}
		if (min > max){
			errorMessage = errorMessage + "Die untere Grenze der Jahrgangsberechtigung gr%F6%DFer als die Obere!%0A";
		}
		if (description.equals("")){
			errorMessage = errorMessage + "Es wurde keine Beschreibung angegeben!%0A";
		}
		Kurs[] kursList = General.wahl.getKursListe();
		for (int i = 0; i < kursList.length; i++){
			if (name.equals(kursList[i].getName())){
				errorMessage = errorMessage + "Es existiert bereits ein gleichnamiger Kurs!%0A";
				break;
			}
		}
		
		if (!errorMessage.equals("")){
			errorMessage = "Es wurde ein Fehler in den angegebenen Daten gefunden!%0A%0A" + errorMessage;
			Print.deb(thread + errorMessage);
			String page = Misc.read(Config.getWebroot()
					+ Config.KURS_ERSTELLEN_PAGE);
			
			page = page.replaceAll("%name%", name);
			page = page.replaceAll("%size%", String.valueOf(size));
			page = page.replaceAll("%min%", String.valueOf(min));
			page = page.replaceAll("%max%", String.valueOf(max));
			page = page.replaceAll("%desc%", description);
			page = page.replaceAll("%action%", "create");
			page = page.replaceAll("%hidden%", "<input type=hidden name='sk' value='"+ sessionkey +"'>");
			
			try {
				Print.msg(thread + " Kurs erstellen failed!");
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, page + "<script>alert(unescape('" + errorMessage + "'));</script>");
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		}else{
			General.wahl.addKurs(new Kurs(name, description, size, min, max));
			Print.deb(thread + "Kurs wurder erfolgreich hinzugefügt!");
			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client,"<script>alert('Der Kurs wurde erfolgreich erstellt!');</script>");
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		}
	}
	
	/**
	 * Führt Login aus.
	 * @param client Verbundener Client
	 * @param args Vom Client übergebene Argumente (Username & Passwort)
	 * @param thread Thread Informationen
	 * @throws FileNotFoundException Datei konnte nicht gefunden werden
	 */
	public static void login(Socket client, String[] args, Thread thread)
			throws FileNotFoundException {
		
		if (args == null){
			allowedFileReqest(client, Config.getWebroot() + Config.START_PAGE,thread);
			return;
		}
		
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
				synchronized(HandleConnections.LOCK){
					lehrer.setSessionkey();
					lehrer.online = true;
				}
				try {
					TCP.send(client, HTTP.HEADER_MOVED);
					TCP.send(client, "Location: create?sk=" + lehrer.getSessionkey());
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}

			} else {
				//Erfolgreicher Login eines Schuelers
				synchronized(HandleConnections.LOCK){
					schueler.setSessionkey();
					schueler.online = true;
				}
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

		if (args == null){
			throw new SecurityException("Der Benutzer konnte nicht verifiziert werden!");
		}
		
		//User identifizieren
		String[][] arguments = handleArgs(args);
		String sessionkey = null;
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			} 
		}
		
		if (!checkSK(sessionkey, userType)){
			throw new SecurityException("Der Benutzer konnte nicht verifiziert werden!");
		}
		
		String inhalt = "";
		// Einlesen der Datei
		inhalt = Misc.read(filePath);
		
		if (filePath.equals(Config.getWebroot() + Config.KURS_ERSTELLEN_PAGE)){
			inhalt = inhalt.replaceAll("%name%", "");
			inhalt = inhalt.replaceAll("%size%", String.valueOf(""));
			inhalt = inhalt.replaceAll("%min%", String.valueOf(""));
			inhalt = inhalt.replaceAll("%max%", String.valueOf(""));
			inhalt = inhalt.replaceAll("%desc%", "");
			inhalt = inhalt.replaceAll("%action%", "create?sk=" + sessionkey);
			inhalt = inhalt.replaceAll("%hidden%", "<input type=hidden name='sk' value='"+ sessionkey +"'>");
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
		inhalt = Misc.read(filePath);
		
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
			if (split.length > 1){
				arguments[i][1] = split[1];
			}else{
				arguments[i][1] = "";
			}
		}
		return arguments;
	}
	
	private static boolean checkSK(String sessionkey, int userType){
		if (sessionkey == null){
			return false;
		} 
		
		boolean authorized = false;
		if (userType == Lehrer.LEHRER){
			Lehrer[] userList = General.lehrer;
			for (int i = 0; i < userList.length;i++){
				if (sessionkey.equals(userList[i].getSessionkey())){
					if (userList[i].online){
						authorized = true;
						break;
					}
				}
			}
		}else if (userType == Schueler.SCHUELER) {
			Schueler[] userList = General.wahl.getSchuelerList();
			for (int i = 0; i < userList.length;i++){
				if (sessionkey.equals(userList[i].getSessionkey())){
					if (userList[i].online){
						authorized = true;
						break;
					}
				}
			}
		}
		
		if (!authorized){
			return false;
		}
		return true;
	}
}
