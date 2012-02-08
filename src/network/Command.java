package network;

import informations.Admin;
import informations.General;
import informations.Kurs;
import informations.Lehrer;
import informations.Schueler;
import informations.User;
import informations.Wahl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;

import evaluation.Auswertung;

import misc.Array;
import misc.Config;
import misc.Misc;
import misc.Print;

public class Command {

	// TODO THREAD SAVE!!

	public static void eval(Socket client, String[] args, Thread thread)
			throws FileNotFoundException, SecurityException {
		
		// Ueberpruefung des Sessionkeys
		if (args == null) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}
		String[][] arguments = handleArgs(args);
		String sessionkey = null;
		int userType = User.USER;

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			}
		}
		if (!checkSK(sessionkey, userType)) {
			throw new SecurityException();
		}
		
		if (General.wahl == null){
			String inhalt = Misc.read(Config.getWebroot()
					+ Config.SUPER_LEHRER_PAGE);
			inhalt = inhalt.replaceAll("%add%", "create");
			inhalt = inhalt.replaceAll("%change%", "admin");
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);
			inhalt = inhalt.replaceAll("%kursliste%", "");
			inhalt = inhalt.replaceAll("%overview%", "overview?sk="
					+ sessionkey);
			inhalt = inhalt.replaceAll("%delkurs%", "admin");
			inhalt = inhalt.replaceAll("%create%", "createwahl");
			inhalt = inhalt.replaceAll("%cancel%", "admin");
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
			inhalt += "<script>alert(unescape('Es existiert noch keine Wahl!'));</script>";

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e1) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
			return;
		}else if (General.wahl.ausgewertet){
			String inhalt = Misc.read(Config.getWebroot()
					+ Config.SUPER_LEHRER_PAGE);
			inhalt = inhalt.replaceAll("%add%", "create");
			inhalt = inhalt.replaceAll("%change%", "admin");
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);
			String liste = "";
			Kurs[] kursListe = General.wahl.getKursListe();
			for (int i = 0; i < kursListe.length; i++) {
				if (i == 0) {
					liste = liste + "<option selected>"
							+ kursListe[i].getName() + "</option>";
				} else {
					liste = liste + "<option>" + kursListe[i].getName()
							+ "</option>";
				}
			}
			inhalt = inhalt.replaceAll("%kursliste%", liste);
			inhalt = inhalt.replaceAll("%overview%", "overview?sk="
					+ sessionkey);
			inhalt = inhalt.replaceAll("%delkurs%", "admin");
			inhalt = inhalt.replaceAll("%create%", "createwahl");
			inhalt = inhalt.replaceAll("%cancel%", "admin");
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
			inhalt += "<script>alert(unescape('Die Wahl wurde bereits ausgewertet!'));</script>";

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e1) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
			return;
		}
		
		Schueler[] schuelerListe = General.wahl.getSchuelerList();
		Kurs[] kursListe = General.wahl.getKursListe();
		Auswertung.auswerten(schuelerListe, kursListe);
		General.wahl.ausgewertet = true;
		kursListe = General.wahl.getKursListe();
		
		String kurse = "";
		
		for (int k = 0; k < kursListe.length; k++){
			kurse += "<p ><h3 align='center'>" + kursListe[k].getName() + "</h3></p>" +
					"<table border='1' align='center'><thead><tr>"+
					"<th align='center' style='width:150px;'>Sch&uuml;ler</th>"+
					"</tr></thead>";
			schuelerListe = kursListe[k].getSchuelerliste(); 
			for (int s = 0; s < schuelerListe.length; s++){
				kurse += "<tr><td align='left' style='width:150px;'>" + schuelerListe[s].getName() + "</td></tr>";
			}
			kurse += "</table><br>";
		}
		
		schuelerListe = General.wahl.getSchuelerList();
		Schueler[] failedSchueler = new Schueler[0]; 
		String[] reason = new String[0];
		
		for (int s = 0; s < schuelerListe.length; s++){
			if (schuelerListe[s].getErstwunsch() == null || schuelerListe[s].getZweitwunsch() == null || schuelerListe[s].getDrittwunsch() == null){
				failedSchueler = Schueler.valueOf(Array.addField(failedSchueler));
				reason = Misc.stringValueOf(Array.addField(reason));
				failedSchueler[failedSchueler.length-1] = schuelerListe[s];
				reason[reason.length-1] = "Sch&uuml;ler hat sich nicht eingew&auml;hlt!";
			}//else{
//				Schueler[] erstwunsch = schuelerListe[s].getErstwunsch().getSchuelerliste();
//				Schueler[] zweitwunsch = schuelerListe[s].getZweitwunsch().getSchuelerliste();
//				Schueler[] drittwunsch = schuelerListe[s].getDrittwunsch().getSchuelerliste();
//				boolean found = false;
//				for (int i = 0; i<erstwunsch.length;i++){
//					if(erstwunsch[i].equals(schuelerListe[s])){
//						found = true;
//						break;
//					}
//				}
//				if (!found){
//					for (int i = 0; i<zweitwunsch.length;i++){
//						if(zweitwunsch[i].equals(schuelerListe[s])){
//							found = true;
//							break;
//						}
//					}
//					if (!found){
//						for (int i = 0; i<drittwunsch.length;i++){
//							if(drittwunsch[i].equals(schuelerListe[s])){
//								found = true;
//								break;
//							}
//						}
//					}
//				}
//				if (!found){
//					failedSchueler = Schueler.valueOf(Array.addField(failedSchueler));
//					reason = Misc.stringValueOf(Array.addField(reason));
//					failedSchueler[failedSchueler.length-1] = schuelerListe[s];
//					reason[reason.length-1] = "Sch&uuml;ler konnte nicht zugeteilt werden!";
//				}
//			}
		}
		
		
		kurse += "<p ><h3 align='center'>Nicht zugeteilte Sch&uuml;ler</h3></p>" +
				"<table border='1' align='center'><thead><tr>"+
				"<th align='center' style='width:150px;'>Sch&uuml;ler</th>"+
				"<th align='center' style='width:300px;'>Grund</th>"+
				"</tr></thead>";		
		for (int s = 0; s < failedSchueler.length; s++){
			kurse += "<tr><td align='left' style='width:150px;'>" + failedSchueler[s].getName() + "</td>";
			kurse += "<td align='left' style='width:300px;'>" + reason[s] + "</td></tr>";
		}
		kurse += "</table><br>";
		
		kurse += "<p ><h3 align='center'>&Uuml;berf&uuml;llte Kurse</h3></p>" +
				"<table border='1' align='center'><thead><tr>"+
				"<th align='center' style='width:150px;'>Sch&uuml;ler</th>"+
				"</tr></thead>";		
		for (int k = 0; k < kursListe.length; k++){
			if (kursListe[k].getKursgroesse() < kursListe[k].getTatsaechlicheKursgroesse()){
				kurse += "<tr><td align='left' style='width:150px;'>" + kursListe[k].getName() + "</td>";
			}
		}
		kurse += "</table><br>";
		
		String inhalt = Misc.read(Config.getWebroot() + Config.KURS_AUSWERTUNG_ANSWER);
		inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey); 
		inhalt = inhalt.replaceAll("%hidden%", "<input type=hidden name='sk' value='" + sessionkey + "'>"); 
		inhalt = inhalt.replaceAll("%action%", "admin?sk="+sessionkey); 
		inhalt = inhalt.replaceAll("%kurse%", kurse); 
		
		try {
			TCP.send(client, HTTP.HEADER_OK);
			TCP.send(client, inhalt);
		} catch (IOException e) {
			Print.err(thread + " Fehler beim Sende an einen Client");
		}
	}

	/**
	 * Nimmt Einwahlversuche entgegen!
	 * 
	 * @param client
	 * @param args
	 * @param thread
	 * @throws FileNotFoundException
	 * @throws SecurityException
	 */
	public static void vote(Socket client, String[] args, Thread thread)
			throws FileNotFoundException, SecurityException {

		int userType = Schueler.SCHUELER;
		;

		if (args == null) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}

		// Uebergebene Informationen
		Kurs erstwunsch = null;
		Kurs zweitwunsch = null;
		Kurs drittwunsch = null;

		// User identifizieren
		String[][] arguments = handleArgs(args);
		String sessionkey = null;

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			} else if (arguments[i][0].equals("first")) {
				erstwunsch = Kurs.getKursByName(Misc.unescape(arguments[i][1]));
			} else if (arguments[i][0].equals("sec")) {
				zweitwunsch = Kurs
						.getKursByName(Misc.unescape(arguments[i][1]));
			} else if (arguments[i][0].equals("third")) {
				drittwunsch = Kurs
						.getKursByName(Misc.unescape(arguments[i][1]));
			}

		}

		if (!checkSK(sessionkey, userType)) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden! Falscher Sessionkey!");
		}
		User fuser = User.getUserBySk(sessionkey);
		Schueler user = (Schueler) fuser;

		if (args.length == 1) {
			String inhalt = Misc.read(Config.getWebroot()
					+ Config.KURS_WAHL_PAGE);
			String liste = "";
			if (General.wahl != null) {
				Kurs[] kursListe = General.wahl.getKursListe();
				for (int i = 0; i < kursListe.length; i++) {
					if (i == 0) {
						liste = liste + "<option selected>"
								+ kursListe[i].getName() + "</option>";
					} else {
						liste = liste + "<option>" + kursListe[i].getName()
								+ "</option>";
					}
				}
			}
			inhalt = inhalt.replaceAll("%options%", liste);
			inhalt = inhalt.replaceAll("%overview%", "overview?sk="
					+ sessionkey);
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);

			// Senden der Datei
			Print.deb(thread + " Senden von: " + Config.getWebroot()
					+ Config.KURS_WAHL_PAGE);
			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
			return;
		}

		String errorMessage = "";

		// TODO Schueler ist nicht in der Richtigen Jahrgangsstufe!

		if (user.getErstwunsch() != null || user.getZweitwunsch() != null
				|| user.getDrittwunsch() != null) {
			errorMessage += "Es wurde bereits eine Wahl abgegeben!%0A";
		} else {
			if (erstwunsch == null || zweitwunsch == null
					|| drittwunsch == null) {
				errorMessage += "Es wurde keine drei W%FCnsche angegeben oder einer der W%FCnsche existiert nicht!%0A";
			} else if (erstwunsch.equals(zweitwunsch)
					|| erstwunsch.equals(drittwunsch)
					|| zweitwunsch.equals(drittwunsch)) {
				errorMessage += "Es wurde gleiche Kurse angegeben!%0A";
			}
		}

		if (errorMessage == "") {

			user.setErstwunsch(erstwunsch);
			user.setZweitwunsch(zweitwunsch);
			user.setDrittwunsch(drittwunsch);

			String inhalt = Misc.read(Config.getWebroot()
					+ Config.KURS_WAHL_ANSWER);

			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);

			// Senden der Datei
			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		} else {
			String inhalt = Misc.read(Config.getWebroot()
					+ Config.KURS_WAHL_PAGE);
			String liste = "";
			if (General.wahl != null) {
				Kurs[] kursListe = General.wahl.getKursListe();
				for (int i = 0; i < kursListe.length; i++) {
					if (i == 0) {
						liste = liste + "<option selected>"
								+ kursListe[i].getName() + "</option>";
					} else {
						liste = liste + "<option>" + kursListe[i].getName()
								+ "</option>";
					}
				}
			}
			inhalt = inhalt.replaceAll("%options%", liste);
			inhalt = inhalt.replaceAll("%overview%", "overview?sk="
					+ sessionkey);
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);

			inhalt += "<script>alert(unescape('" + errorMessage
					+ "'));</script>";

			// Senden der Datei
			Print.deb(thread + " Senden von: " + Config.getWebroot()
					+ Config.KURS_WAHL_PAGE);
			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		}

	}

	/**
	 * Simuliert einwahlen
	 * 
	 * @param client
	 * @param args
	 * @param thread
	 * @throws FileNotFoundException
	 * @throws SecurityException
	 */
	public static void vgen(Socket client, String[] args, Thread thread)
			throws FileNotFoundException, SecurityException {
		// Ueberpruefung des Sessionkeys und der anderen Argumente
		if (args == null) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}
		String[][] arguments = handleArgs(args);
		String sessionkey = null;
		int userType = User.USER;

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			}
		}
		if (!checkSK(sessionkey, userType)) {
			throw new SecurityException();
		}

		try {
			General.vgen();
		} catch (Exception e) {
			String inhalt = Misc.read(Config.getWebroot()
					+ Config.SUPER_LEHRER_PAGE);
			inhalt = inhalt.replaceAll("%add%", "create");
			inhalt = inhalt.replaceAll("%change%", "admin");
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);
			inhalt = inhalt.replaceAll("%kursliste%", "");
			inhalt = inhalt.replaceAll("%overview%", "overview?sk="
					+ sessionkey);
			inhalt = inhalt.replaceAll("%delkurs%", "admin");
			inhalt = inhalt.replaceAll("%create%", "createwahl");
			inhalt = inhalt.replaceAll("%cancel%", "admin");
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
			inhalt += "<script>alert(unescape('Es existiert noch keine Wahl!'));</script>";

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e1) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
			return;
		}

		String inhalt = Misc.read(Config.getWebroot()
				+ Config.SUPER_LEHRER_PAGE);
		inhalt = inhalt.replaceAll("%add%", "create");
		inhalt = inhalt.replaceAll("%change%", "admin");
		inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);
		String liste = "";
		Kurs[] kursListe = General.wahl.getKursListe();
		for (int i = 0; i < kursListe.length; i++) {
			if (i == 0) {
				liste = liste + "<option selected>" + kursListe[i].getName()
						+ "</option>";
			} else {
				liste = liste + "<option>" + kursListe[i].getName()
						+ "</option>";
			}
		}
		inhalt = inhalt.replaceAll("%kursliste%", liste);
		inhalt = inhalt.replaceAll("%overview%", "overview?sk=" + sessionkey);
		inhalt = inhalt.replaceAll("%delkurs%", "admin");
		inhalt = inhalt.replaceAll("%create%", "createwahl");
		inhalt = inhalt.replaceAll("%cancel%", "admin");
		inhalt = inhalt.replaceAll("%hidden%",
				"<input type=hidden name='sk' value='" + sessionkey + "'>");
		inhalt += "<script>alert(unescape('Die Einwahlen wurde erfolgreich simuliert!'));</script>";

		User u = User.getUserBySk(sessionkey);
		String uname = "";
		if (u == null) {
			uname = "UNKNOWN";
		} else {
			uname = u.getName();
		}
		Print.msg(thread + " Einwahlen wurde von " + uname + " simuliert");
		try {
			TCP.send(client, HTTP.HEADER_OK);
			TCP.send(client, inhalt);
		} catch (IOException e) {
			Print.err(thread + " Fehler beim Sende an einen Client");
		}
	}

	/**
	 * Genereiert eine Wahl und Kurse
	 * 
	 * @param client
	 * @param args
	 * @param thread
	 * @throws FileNotFoundException
	 * @throws SecurityException
	 */
	public static void gen(Socket client, String[] args, Thread thread)
			throws FileNotFoundException, SecurityException {
		// Ueberpruefung des Sessionkeys und der anderen Argumente
		if (args == null) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}
		String[][] arguments = handleArgs(args);
		String sessionkey = null;
		int userType = User.USER;

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			}
		}
		if (!checkSK(sessionkey, userType)) {
			throw new SecurityException();
		}

		try {
			General.gen();
		} catch (Exception e) {
			String inhalt = Misc.read(Config.getWebroot()
					+ Config.SUPER_LEHRER_PAGE);
			inhalt = inhalt.replaceAll("%add%", "create");
			inhalt = inhalt.replaceAll("%change%", "admin");
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);
			String liste = "";
			Kurs[] kursListe = General.wahl.getKursListe();
			for (int i = 0; i < kursListe.length; i++) {
				if (i == 0) {
					liste = liste + "<option selected>"
							+ kursListe[i].getName() + "</option>";
				} else {
					liste = liste + "<option>" + kursListe[i].getName()
							+ "</option>";
				}
			}
			inhalt = inhalt.replaceAll("%kursliste%", liste);
			inhalt = inhalt.replaceAll("%overview%", "overview?sk="
					+ sessionkey);
			inhalt = inhalt.replaceAll("%delkurs%", "admin");
			inhalt = inhalt.replaceAll("%create%", "createwahl");
			inhalt = inhalt.replaceAll("%cancel%", "admin");
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
			inhalt += "<script>alert(unescape('Es existiert bereits eine Wahl!'));</script>";

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e1) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
			return;
		}

		Schueler[] schuelerListe = General.wahl.getSchuelerList();
		Lehrer[] lehrerListe = General.wahl.getLehrerList();

		String inhalt = Misc.read(Config.getWebroot() + Config.WAHL_ANSWER);
		inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);
		inhalt = inhalt.replaceAll("%hidden%",
				"<input type=hidden name='sk' value='" + sessionkey + "'>");
		inhalt = inhalt.replaceAll("%action%", "admin");
		String schuelerL = "";
		for (int i = 0; i < schuelerListe.length; i++) {
			schuelerL += "<tr>" + "<td align='left' style='width:150px;'>"
					+ schuelerListe[i].getName() + "</td>"
					+ "<td align='left' style='width:150px;'>"
					+ schuelerListe[i].getPasswort() + "</td></tr>";
		}
		inhalt = inhalt.replaceAll("%schueler%", schuelerL);
		String lehrerL = "";
		for (int i = 0; i < lehrerListe.length; i++) {
			lehrerL += "<tr>" + "<td align='left' style='width:150px;'>"
					+ lehrerListe[i].getName() + "</td>"
					+ "<td align='left' style='width:150px;'>"
					+ lehrerListe[i].getPasswort() + "</td></tr>";
		}
		inhalt = inhalt.replaceAll("%lehrer%", lehrerL);

		User u = User.getUserBySk(sessionkey);
		String uname = "";
		if (u == null) {
			uname = "UNKNOWN";
		} else {
			uname = u.getName();
		}
		Print.msg(thread + " Wahl wurde von " + uname + " erstellt");
		try {
			TCP.send(client, HTTP.HEADER_OK);
			TCP.send(client, inhalt);
		} catch (IOException e) {
			Print.err(thread + " Fehler beim Sende an einen Client");
		}
	}

	/**
	 * Behalndelt Logout - Versuche
	 * 
	 * @param client
	 * @param args
	 * @param thread
	 * @throws FileNotFoundException
	 */
	public static void logout(Socket client, String[] args, Thread thread)
			throws FileNotFoundException {
		// Ueberpruefung des Sessionkeys und der anderen Argumente
		if (args == null) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}
		String[][] arguments = handleArgs(args);
		String sessionkey = null;
		int userType = User.USER;

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			}
		}

		if (!checkSK(sessionkey, userType)) {
			try {
				Print.deb(thread + "Fehlerhaftes logout!");
				TCP.send(client, HTTP.HEADER_ACCESS_FORBIDDEN);
				TCP.send(client,
						"<script>alert('Du warst nicht eingeloggt!');</script>");
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
			return;
		}

		User user = User.getUserBySk(sessionkey);
		user.online = false;
		user.delSessionkey();

		String inhalt = Misc.read(Config.getWebroot() + Config.LOGOUT_PAGE);

		try {
			Print.msg(thread + " Der User " + user.getName()
					+ " hat sich ausgeloggt!");
			TCP.send(client, HTTP.HEADER_OK);
			TCP.send(client, inhalt);
		} catch (IOException e) {
			Print.err(thread + " Fehler beim Sende an einen Client");
		}
	}

	/**
	 * Bearbeitung von Admin-Aktivitaeten
	 * 
	 * @param client
	 * @param args
	 * @param thread
	 * @throws SecurityException
	 * @throws FileNotFoundException
	 */
	public static void admin(Socket client, String[] args, Thread thread)
			throws SecurityException, FileNotFoundException {

		// Ueberpruefung des Sessionkeys und der anderen Argumente
		if (args == null) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}

		String[][] arguments = handleArgs(args);
		String sessionkey = null;

		boolean change = false;
		String changeKurs = "";
		boolean delete = false;
		String delKurs = "";
		boolean cancel = false;

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			} else if (arguments[i][0].equals("change")) {
				change = true;
			} else if (arguments[i][0].equals("changekurs")) {
				changeKurs = Misc.unescape(arguments[i][1]);
			} else if (arguments[i][0].equals("delkurs")) {
				delKurs = Misc.unescape(arguments[i][1]);
				delete = true;
			} else if (arguments[i][0].equals("cancel")) {
				cancel = true;
			}
		}
		// Ueberpruefung des Sessionkeys
		if (!sessionkey.equals(General.admin.getSessionkey())) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden! Falscher Sessionkey!");
		}

		// Untersuchung der Argumente auf Fehler
		// Bei einem/keinem Fehler wird entsprechend reagiert
		if (General.wahl == null) {
			String inhalt = Misc.read(Config.getWebroot()
					+ Config.SUPER_LEHRER_PAGE);
			inhalt = inhalt.replaceAll("%add%", "create");
			inhalt = inhalt.replaceAll("%change%", "admin");
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);
			inhalt = inhalt.replaceAll("%kursliste%", "");
			inhalt = inhalt.replaceAll("%overview%", "overview?sk="
					+ sessionkey);
			inhalt = inhalt.replaceAll("%delkurs%", "admin");
			inhalt = inhalt.replaceAll("%create%", "createwahl");
			inhalt = inhalt.replaceAll("%cancel%", "admin");
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
			inhalt += "<script>alert(unescape('Es wurde noch keine Wahl erstellt!'));</script>";

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		} else if (change) {
			String errorMessage = "";
			if (General.wahl == null) {
				errorMessage = errorMessage
						+ "Es wurde noch keine Wahl erstellt!%0A";
			}
			if (changeKurs.equals("")) {
				errorMessage = errorMessage
						+ "Es wurde kein Kurs zum %C4ndern angegeben!%0A";
			} else {
				Kurs[] kursListe = General.wahl.getKursListe();
				Kurs kurs = null;
				for (int k = 0; k < kursListe.length; k++) {
					if (changeKurs.equals(kursListe[k].getName())) {
						kurs = kursListe[k];
						break;
					}
				}
				if (kurs == null) {
					errorMessage = errorMessage
							+ "Der Kurs existiert nicht!%0A";
				}
			}

			// Fehler wurde gefunden
			if (!errorMessage.equals("")) {
				String inhalt = Misc.read(Config.getWebroot()
						+ Config.SUPER_LEHRER_PAGE);
				inhalt = inhalt.replaceAll("%add%", "create");
				inhalt = inhalt.replaceAll("%logout%", "logout?sk="
						+ sessionkey);
				inhalt = inhalt.replaceAll("%change%", "admin");
				String liste = "";
				Kurs[] kursListe = General.wahl.getKursListe();
				for (int i = 0; i < kursListe.length; i++) {
					if (i == 0) {
						liste = liste + "<option selected>"
								+ kursListe[i].getName() + "</option>";
					} else {
						liste = liste + "<option>" + kursListe[i].getName()
								+ "</option>";
					}
				}
				inhalt = inhalt.replaceAll("%kursliste%", liste);
				inhalt = inhalt.replaceAll("%overview%", "overview?sk="
						+ sessionkey);
				inhalt = inhalt.replaceAll("%delkurs%", "admin");
				inhalt = inhalt.replaceAll("%create%", "createwahl");
				inhalt = inhalt.replaceAll("%cancel%", "admin");
				inhalt = inhalt.replaceAll("%hidden%",
						"<input type=hidden name='sk' value='" + sessionkey
								+ "'>");
				inhalt = inhalt + "<script>alert(unescape('" + errorMessage
						+ "'));</script>";

				try {
					Print.deb(thread + " Kurs ändern failed! Kurs unknown!");

					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			} else { // Kein Fehler wurde gefunden
				Kurs[] kursListe = General.wahl.getKursListe();
				Kurs kurs = null;
				for (int k = 0; k < kursListe.length; k++) {
					if (changeKurs.equals(kursListe[k].getName())) {
						kurs = kursListe[k];
						break;
					}
				}
				String inhalt = Misc.read(Config.getWebroot()
						+ Config.KURS_ERSTELLEN_PAGE);
				inhalt = inhalt.replaceAll("%logout%", "logout?sk="
						+ sessionkey);
				inhalt = inhalt.replaceAll("%name%", kurs.getName());
				inhalt = inhalt.replaceAll("%size%",
						String.valueOf(kurs.getKursgroesse()));
				inhalt = inhalt.replaceAll("%min%",
						String.valueOf(kurs.getJahrgangsberechtigungMin()));
				inhalt = inhalt.replaceAll("%max%",
						String.valueOf(kurs.getJahrgangsberechtigungMax()));
				inhalt = inhalt.replaceAll("%desc%", kurs.getBeschreibung());
				inhalt = inhalt.replaceAll("%action%", "create");
				inhalt = inhalt.replaceAll("%hidden%",
						"<input type=hidden name='sk' value='" + sessionkey
								+ "'><input type=hidden name='change' value='"
								+ kurs.getName() + "'>");

				try {
					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			}
		} else if (delete) { // Das Löschen von Kursen
			String errorMessage = "";
			if (General.wahl == null) {
				errorMessage = errorMessage
						+ "Es wurde noch keine Wahl erstellt!%0A";
			}
			if (delKurs.equals("")) {
				errorMessage = errorMessage
						+ "Es wurde kein Kurs zum %C4ndern angegeben!%0A";
			} else {
				Kurs[] kursListe = General.wahl.getKursListe();
				Kurs kurs = null;
				for (int k = 0; k < kursListe.length; k++) {
					if (delKurs.equals(kursListe[k].getName())) {
						kurs = kursListe[k];
						break;
					}
				}
				if (kurs == null) {
					errorMessage = errorMessage
							+ "Der Kurs existiert nicht!%0A";
				}
			}

			// Fehler wurde gefunden
			if (!errorMessage.equals("")) {
				String inhalt = Misc.read(Config.getWebroot()
						+ Config.SUPER_LEHRER_PAGE);
				inhalt = inhalt.replaceAll("%add%", "create");
				inhalt = inhalt.replaceAll("%change%", "admin");
				inhalt = inhalt.replaceAll("%logout%", "logout?sk="
						+ sessionkey);
				String liste = "";
				Kurs[] kursListe = General.wahl.getKursListe();
				for (int i = 0; i < kursListe.length; i++) {
					if (i == 0) {
						liste = liste + "<option selected>"
								+ kursListe[i].getName() + "</option>";
					} else {
						liste = liste + "<option>" + kursListe[i].getName()
								+ "</option>";
					}
				}
				inhalt = inhalt.replaceAll("%kursliste%", liste);
				inhalt = inhalt.replaceAll("%overview%", "overview?sk="
						+ sessionkey);
				inhalt = inhalt.replaceAll("%delkurs%", "admin");
				inhalt = inhalt.replaceAll("%create%", "createwahl");
				inhalt = inhalt.replaceAll("%cancel%", "admin");
				inhalt = inhalt.replaceAll("%hidden%",
						"<input type=hidden name='sk' value='" + sessionkey
								+ "'>");
				inhalt = inhalt + "<script>alert(unescape('" + errorMessage
						+ "'));</script>";

				try {
					Print.deb(thread + " Kurs löschen failed!");

					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			} else { // Kein Fehler wurde gefunden
				Kurs[] kursListe = General.wahl.getKursListe();
				Kurs kurs = null;
				for (int k = 0; k < kursListe.length; k++) {
					if (delKurs.equals(kursListe[k].getName())) {
						kurs = kursListe[k];
						break;
					}
				}

				// Kurs löschen
				General.wahl.delKurs(kurs);

				kursListe = General.wahl.getKursListe();

				String inhalt = Misc.read(Config.getWebroot()
						+ Config.SUPER_LEHRER_PAGE);
				inhalt = inhalt.replaceAll("%add%", "create");
				inhalt = inhalt.replaceAll("%change%", "admin");
				inhalt = inhalt.replaceAll("%logout%", "logout?sk="
						+ sessionkey);
				String liste = "";
				for (int i = 0; i < kursListe.length; i++) {
					if (i == 0) {
						liste = liste + "<option selected>"
								+ kursListe[i].getName() + "</option>";
					} else {
						liste = liste + "<option>" + kursListe[i].getName()
								+ "</option>";
					}
				}
				inhalt = inhalt.replaceAll("%kursliste%", liste);
				inhalt = inhalt.replaceAll("%overview%", "overview?sk="
						+ sessionkey);
				inhalt = inhalt.replaceAll("%delkurs%", "admin");
				inhalt = inhalt.replaceAll("%create%", "createwahl");
				inhalt = inhalt.replaceAll("%cancel%", "admin");
				inhalt = inhalt.replaceAll("%hidden%",
						"<input type=hidden name='sk' value='" + sessionkey
								+ "'>");
				inhalt = inhalt
						+ "<script>alert(unescape('Der Kurs wurde erfolgreich gels%F6cht!'));</script>";

				try {
					User u = User.getUserBySk(sessionkey);
					String uname = "";
					if (u == null) {
						uname = "UNKNOWN";
					} else {
						uname = u.getName();
					}
					Print.msg(thread + " Kurs wurde gelöscht! Kursname: "
							+ kurs.getName() + " von " + uname);

					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			}
		} else if (cancel) {
			User u = User.getUserBySk(sessionkey);
			String uname = "";
			if (u == null) {
				uname = "UNKNOWN";
			} else {
				uname = u.getName();
			}
			Print.msg(thread + " Wahl wurde von " + uname + " abgebrochen");

			// Kurs löschen
			General.wahl = null;

			String inhalt = Misc.read(Config.getWebroot()
					+ Config.SUPER_LEHRER_PAGE);
			inhalt = inhalt.replaceAll("%add%", "create");
			inhalt = inhalt.replaceAll("%change%", "admin");
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);
			inhalt = inhalt.replaceAll("%kursliste%", "");
			inhalt = inhalt.replaceAll("%overview%", "overview?sk="
					+ sessionkey);
			inhalt = inhalt.replaceAll("%delkurs%", "admin");
			inhalt = inhalt.replaceAll("%create%", "createwahl");
			inhalt = inhalt.replaceAll("%cancel%", "admin");
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
			inhalt = inhalt
					+ "<script>alert(unescape('Der Wahl wurde erfolgreich abgebrochen!'));</script>";

			try {
				u = User.getUserBySk(sessionkey);
				uname = "";
				if (u == null) {
					uname = "UNKNOWN";
				} else {
					uname = u.getName();
				}
				Print.msg(thread + " Die Wahl wurde von " + uname
						+ " abgebrochen!");

				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		}
	}

	/**
	 * Erstellt eine neue Wahl
	 * 
	 * @param client
	 *            verbundener Client
	 * @param args
	 *            Uebergebene Argumente
	 * @param thread
	 * @throws SecurityException
	 * @throws FileNotFoundException
	 */
	public static void createWahl(Socket client, String[] args, Thread thread)
			throws SecurityException, FileNotFoundException {
		if (args == null) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}

		String[][] arguments = handleArgs(args);
		String sessionkey = null;

		String date = "";
		Calendar cal = Calendar.getInstance();
		int g07 = 0;
		int g08 = 0;
		int g09 = 0;
		int g10 = 0;
		int g11 = 0;
		int g12 = 0;

		int r07 = 0;
		int r08 = 0;
		int r09 = 0;
		int r10 = 0;

		int h07 = 0;
		int h08 = 0;
		int h09 = 0;
		int h10 = 0;

		int teacher = 0;

		// Auslesen der Argumente
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			} else if (arguments[i][0].equals("date")) {
				date = Misc.unescape(arguments[i][1]);
			} else if (arguments[i][0].equals("G07")) {
				try {
					g07 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					g07 = 0;
				}
			} else if (arguments[i][0].equals("G08")) {
				try {
					g08 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					g08 = 0;
				}
			} else if (arguments[i][0].equals("G09")) {
				try {
					g09 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					g09 = 0;
				}
			} else if (arguments[i][0].equals("G10")) {
				try {
					g10 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					g10 = 0;
				}
			} else if (arguments[i][0].equals("G11")) {
				try {
					g11 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					g11 = 0;
				}
			} else if (arguments[i][0].equals("G12")) {
				try {
					g12 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					g12 = 0;
				}
			} else if (arguments[i][0].equals("R07")) {
				try {
					r07 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					r07 = 0;
				}
			} else if (arguments[i][0].equals("R08")) {
				try {
					r08 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					r08 = 0;
				}
			} else if (arguments[i][0].equals("R09")) {
				try {
					r09 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					r09 = 0;
				}
			} else if (arguments[i][0].equals("R10")) {
				try {
					r10 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					r10 = 0;
				}
			} else if (arguments[i][0].equals("H07")) {
				try {
					h07 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					h07 = 0;
				}
			} else if (arguments[i][0].equals("H08")) {
				try {
					h08 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					h08 = 0;
				}
			} else if (arguments[i][0].equals("H09")) {
				try {
					h09 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					h09 = 0;
				}
			} else if (arguments[i][0].equals("H10")) {
				try {
					h10 = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					h10 = 0;
				}
			} else if (arguments[i][0].equals("teacher")) {
				try {
					teacher = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					teacher = 0;
				}
			}
		}

		// Ueberpruefung des Sessionkeys
		if (!sessionkey.equals(General.admin.getSessionkey())) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}

		Print.deb(thread + "Nutzer verifiziert!");

		// Eingaben auf Fehler pruefen
		String errorMessage = "";
		if (General.wahl != null) {
			errorMessage += "Es wurde bereits eine Wahl erstellt!%0A";
		}
		if (date.equals("")) {
			errorMessage = errorMessage
					+ "Es wurde kein Einwahlschluss angegeben!%0A";
		} else {
			String[] split = date.split("\\.");
			if (split.length != 3) {
				Print.deb("Falsches Datum: " + date + " split.lenght="
						+ split.length);
				errorMessage = errorMessage
						+ "Es wurde ein falscher Einwahlschluss angegeben! (dd:mm:yy)%0A";
			} else {
				try {
					int year = Integer.valueOf(split[2]);
					int month = Integer.valueOf(split[1]);
					int day = Integer.valueOf(split[0]);
					cal.clear();
					cal.set(year, month - 1, day);
					if (cal.before(Calendar.getInstance())) {
						errorMessage = errorMessage
								+ "Es wurde ein falscher Einwahlschluss angegeben! Das Datum lieg in der Vergangenheit!%0A";
					}
				} catch (NumberFormatException e) {
					Print.deb("Falsches Datum: " + date);
					errorMessage = errorMessage
							+ "Es wurde ein falscher Einwahlschluss angegeben!%0A";
				}
			}
		}

		if (g07 < 0 || g08 < 0 || g09 < 0 || g10 < 0 || g11 < 0 || g12 < 0
				|| r07 < 0 || r08 < 0 || r09 < 0 || r10 < 0 || h07 < 0
				|| h08 < 0 || h09 < 0 || h10 < 0) {
			errorMessage = errorMessage
					+ "Es wurde eine negative Sch%FCleranzahl angegeben!%0A";
		}

		if ((g07 + g08 + g09 + g10 + g11 + g12 + r07 + r08 + r09 + r10 + h07
				+ h08 + h09 + h10) <= 0) {
			errorMessage = errorMessage
					+ "Es wurden keine Sch%FCler angegeben!%0A";
		}

		if (teacher < 0) {
			errorMessage = errorMessage
					+ "Es wurde eine negative Lehreranzahl angegeben!%0A";
		}

		// Fehler Ausgabe
		if (!errorMessage.equals("")) {
			errorMessage = "Es wurde ein Fehler in den angegebenen Daten gefunden!%0A%0A"
					+ errorMessage;
			Print.deb(thread + errorMessage);
			String page = Misc.read(Config.getWebroot()
					+ Config.WAHL_ERSTELLEN_PAGE);

			page = page.replaceAll("%date%", date);
			page = page.replaceAll("%logout%", "logout?sk=" + sessionkey);

			page = page.replaceAll("%G07%", String.valueOf(g07));
			page = page.replaceAll("%G08%", String.valueOf(g08));
			page = page.replaceAll("%G09%", String.valueOf(g09));
			page = page.replaceAll("%G10%", String.valueOf(g10));
			page = page.replaceAll("%G11%", String.valueOf(g11));
			page = page.replaceAll("%G12%", String.valueOf(g12));

			page = page.replaceAll("%R07%", String.valueOf(r07));
			page = page.replaceAll("%R08%", String.valueOf(r08));
			page = page.replaceAll("%R09%", String.valueOf(r09));
			page = page.replaceAll("%R10%", String.valueOf(r10));

			page = page.replaceAll("%H07%", String.valueOf(h07));
			page = page.replaceAll("%H08%", String.valueOf(h08));
			page = page.replaceAll("%H09%", String.valueOf(h09));
			page = page.replaceAll("%H10%", String.valueOf(h10));

			page = page.replaceAll("%teacher%", String.valueOf(teacher));
			page = page.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");

			try {
				Print.deb(thread + " Wahl erstellen failed!");
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, page + "<script>alert(unescape('"
						+ errorMessage + "'));</script>");
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		} else {
			int schueler = 0;
			schueler = g07 + g08 + g09 + g10 + g11 + g12 + r07 + r08 + r09
					+ r10 + h07 + h08 + h09 + h10;
			Schueler[] schuelerListe = new Schueler[schueler];
			Lehrer[] lehrerListe = new Lehrer[teacher];
			int schuelerIndex = 0;
			// Schueler generieren
			for (int i = 1; i <= g07; i++) {
				String name = "G07" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 7;
				String schulzweig = "G";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= g08; i++) {
				String name = "G08" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 8;
				String schulzweig = "G";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= g09; i++) {
				String name = "G09" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 9;
				String schulzweig = "G";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= g10; i++) {
				String name = "G10" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 10;
				String schulzweig = "G";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= g11; i++) {
				String name = "G11" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 11;
				String schulzweig = "G";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= g12; i++) {
				String name = "G12" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 12;
				String schulzweig = "G";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}

			for (int i = 1; i <= r07; i++) {
				String name = "R07" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 7;
				String schulzweig = "R";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= r08; i++) {
				String name = "R08" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 8;
				String schulzweig = "R";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= r09; i++) {
				String name = "R09" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 9;
				String schulzweig = "R";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= r10; i++) {
				String name = "R10" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 10;
				String schulzweig = "R";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= h07; i++) {
				String name = "H07" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 7;
				String schulzweig = "H";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= h08; i++) {
				String name = "H08" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 8;
				String schulzweig = "H";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= h09; i++) {
				String name = "H09" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 9;
				String schulzweig = "H";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}
			for (int i = 1; i <= h10; i++) {
				String name = "H10" + i;
				String passwort = Misc.gen(6);
				int jahrgang = 10;
				String schulzweig = "H";
				schuelerListe[schuelerIndex] = new Schueler(name, passwort,
						jahrgang, schulzweig);
				schuelerIndex++;
			}

			// Lehrer generieren
			for (int i = 0; i < lehrerListe.length; i++) {
				String name = "LEHRER" + (i + 1);
				String passwort = Misc.gen(6);
				lehrerListe[i] = new Lehrer(name, passwort);
			}

			// Answerpage zurueck senden
			String inhalt = Misc.read(Config.getWebroot() + Config.WAHL_ANSWER);
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
			inhalt = inhalt.replaceAll("%action%", "admin");
			String schuelerL = "";
			for (int i = 0; i < schuelerListe.length; i++) {
				schuelerL += "<tr>" + "<td align='left' style='width:150px;'>"
						+ schuelerListe[i].getName() + "</td>"
						+ "<td align='left' style='width:150px;'>"
						+ schuelerListe[i].getPasswort() + "</td></tr>";
			}
			inhalt = inhalt.replaceAll("%schueler%", schuelerL);
			String lehrerL = "";
			for (int i = 0; i < lehrerListe.length; i++) {
				lehrerL += "<tr>" + "<td align='left' style='width:150px;'>"
						+ lehrerListe[i].getName() + "</td>"
						+ "<td align='left' style='width:150px;'>"
						+ lehrerListe[i].getPasswort() + "</td></tr>";
			}
			inhalt = inhalt.replaceAll("%lehrer%", lehrerL);

			try {
				General.wahl = new Wahl(cal, schuelerListe, lehrerListe);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			User u = User.getUserBySk(sessionkey);
			String uname = "";
			if (u == null) {
				uname = "UNKNOWN";
			} else {
				uname = u.getName();
			}
			Print.msg(thread + "Wahl wurde von " + uname + " erstellt");
			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		}
	}

	/**
	 * Methode zum erstellen eines Kurses
	 * 
	 * @param client
	 * @param args
	 *            Vom Client übergebenen Argumente
	 * @param userType
	 *            Typ des Nutzers (Schüler/Lehrer)
	 * @param thread
	 * @throws SecurityException
	 *             Nutzer konnte nicht authentifiziert werden
	 * @throws FileNotFoundException
	 */
	public static void createKurs(Socket client, String[] args, int userType,
			Thread thread) throws SecurityException, FileNotFoundException {
		if (args == null) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}

		// User identifizieren
		String[][] arguments = handleArgs(args);
		String sessionkey = null;
		String name = "";
		int size = 0;
		int min = 0;
		int max = 0;
		String description = "";
		boolean change = false;
		String changeKurs = "";
		Kurs kursToChange = null;

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			} else if (arguments[i][0].equals("name")) {
				name = Misc.unescape(arguments[i][1]);
			} else if (arguments[i][0].equals("size")) {
				try {
					size = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					size = 0;
				}
			} else if (arguments[i][0].equals("min")) {
				try {
					min = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					min = 0;
				}
			} else if (arguments[i][0].equals("max")) {
				try {
					max = Integer.valueOf(arguments[i][1]);
				} catch (NumberFormatException e) {
					max = 0;
				}
			} else if (arguments[i][0].equals("desc")) {
				description = Misc.unescape(arguments[i][1]);
			} else if (arguments[i][0].equals("change")) {
				change = true;
				changeKurs = Misc.unescape(arguments[i][1]);
			}
		}

		if (!checkSK(sessionkey, userType)) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden! Falscher Sessionkey!");
		}
		User user = User.getUserBySk(sessionkey);
		if (user instanceof Admin) {
			userType = Admin.ADMIN;
		}

		Print.deb(thread + "Nutzer verifiziert!");

		// Falls der Kurs gesendert werden soll, checken ob der Kurs existiert
		if (change) {
			Kurs[] kursListe = General.wahl.getKursListe();
			Kurs kurs = null;
			for (int k = 0; k < kursListe.length; k++) {
				if (changeKurs.equals(kursListe[k].getName())) {
					kurs = kursListe[k];
					break;
				}
			}
			if (kurs == null) {
				String inhalt = Misc.read(Config.getWebroot()
						+ Config.SUPER_LEHRER_PAGE);

				inhalt = inhalt.replaceAll("%add%", "create");
				inhalt = inhalt.replaceAll("%change%", "admin");
				inhalt = inhalt.replaceAll("%logout%", "logout?sk="
						+ sessionkey);
				String liste = "";
				for (int i = 0; i < kursListe.length; i++) {
					if (i == 0) {
						liste = liste + "<option selected>"
								+ kursListe[i].getName() + "</option>";
					} else {
						liste = liste + "<option>" + kursListe[i].getName()
								+ "</option>";
					}
				}
				inhalt = inhalt.replaceAll("%kursliste%", liste);
				inhalt = inhalt.replaceAll("%overview%", "overview?sk="
						+ sessionkey);
				inhalt = inhalt.replaceAll("%delkurs%", "admin");
				inhalt = inhalt.replaceAll("%create%", "createwahl");
				inhalt = inhalt.replaceAll("%cancel%", "admin");
				inhalt = inhalt.replaceAll("%hidden%",
						"<input type=hidden name='sk' value='" + sessionkey
								+ "'>");
				inhalt = inhalt
						+ "<script>alert(unescape('Der Kurs konnte nicht gefunden werden!'));</script>";

				try {
					Print.msg(thread + " Kurs ändern failed! Kurs unknown!");

					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}

				return;
			} else {
				kursToChange = kurs;
			}
		}

		// Eingaben ueberpruefen
		String errorMessage = "";
		if (name.equals("")) {
			errorMessage = errorMessage
					+ "Es wurde kein Kursname angegeben!%0A";
		}
		if (size <= 0 || size >= Config.MAXIMAL_KURS_GROESSE) {
			errorMessage = errorMessage
					+ "Die Kursgr%F6%DFe ist entweder zu klein oder zu gro%DF!%0A";
		}
		if (min < Config.MINIMAL_JAHRGANG || min > Config.MAXIMAL_JAHRGANG) {
			errorMessage = errorMessage
					+ "Die untere Grenze der Jahrgangsberechtigung ist nicht korrekt!%0A";
		}
		if (max < Config.MINIMAL_JAHRGANG || max > Config.MAXIMAL_JAHRGANG) {
			errorMessage = errorMessage
					+ "Die obere Grenze der Jahrgangsberechtigung ist nicht korrekt!%0A";
		}
		if (min > max) {
			errorMessage = errorMessage
					+ "Die untere Grenze der Jahrgangsberechtigung gr%F6%DFer als die Obere!%0A";
		}
		if (description.equals("")) {
			errorMessage = errorMessage
					+ "Es wurde keine Beschreibung angegeben!%0A";
		}
		Kurs[] kursList = General.wahl.getKursListe();
		for (int i = 0; i < kursList.length; i++) {
			if (change && kursList[i].equals(kursToChange)) {
				continue;
			}
			if (name.equals(kursList[i].getName())) {
				errorMessage = errorMessage
						+ "Es existiert bereits ein gleichnamiger Kurs!%0A";
				break;
			}
		}

		if (!errorMessage.equals("")) {
			errorMessage = "Es wurde ein Fehler in den angegebenen Daten gefunden!%0A%0A"
					+ errorMessage;
			Print.deb(thread + errorMessage);
			String page = Misc.read(Config.getWebroot()
					+ Config.KURS_ERSTELLEN_PAGE);
			page = page.replaceAll("%logout%", "logout?sk=" + sessionkey);
			page = page.replaceAll("%name%", name);
			page = page.replaceAll("%size%", String.valueOf(size));
			page = page.replaceAll("%min%", String.valueOf(min));
			page = page.replaceAll("%max%", String.valueOf(max));
			page = page.replaceAll("%desc%", description);
			page = page.replaceAll("%action%", "create");
			page = page.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");

			try {
				if (!change) {
					Print.msg(thread + " Kurs erstellen failed!");
				} else {
					Print.msg(thread + " Kurs ändern failed!");
				}
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, page + "<script>alert(unescape('"
						+ errorMessage + "'));</script>");
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		} else {

			if (!change) {
				User u = User.getUserBySk(sessionkey);
				String uname = "";
				if (u == null) {
					uname = "UNKNOWN";
				} else {
					uname = u.getName();
				}
				Print.msg(thread + " Kurs wurde erstellt. Name: " + name
						+ " von " + uname);
				General.wahl
						.addKurs(new Kurs(name, description, size, min, max));

				String inhalt = Misc.read(Config.getWebroot()
						+ Config.KURS_ANSWER);
				inhalt = inhalt.replaceAll("%logout%", "logout?sk="
						+ sessionkey);
				inhalt = inhalt.replaceAll("%message%",
						"Der Kurs wurde erfolgreich erstellt!");
				inhalt = inhalt.replaceAll("%hidden%",
						"<input type=hidden name='sk' value='" + sessionkey
								+ "'>");
				if (userType == Lehrer.LEHRER) {
					inhalt = inhalt.replaceAll("%button%",
							"Noch einen Kurs erstellen!");
					inhalt = inhalt.replaceAll("%action%", "create");
				} else if (userType == Admin.ADMIN) {
					inhalt = inhalt.replaceAll("%button%",
							"Zur&uuml;ck zur Adminoberfl&auml;che!");
					inhalt = inhalt.replaceAll("%action%", "admin");
				}

				try {
					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			} else {
				User u = User.getUserBySk(sessionkey);
				String uname = "";
				if (u == null) {
					uname = "UNKNOWN";
				} else {
					uname = u.getName();
				}
				Print.msg(thread + " Kurs wurde geändert. Name: " + name
						+ " von " + uname);

				kursToChange.setName(name);
				kursToChange.setBeschreibung(description);
				kursToChange.setKursgroesse(size);
				kursToChange.setJahrgangsberechtigungMin(min);
				kursToChange.setJahrgangsberechtigungMax(max);

				String inhalt = Misc.read(Config.getWebroot()
						+ Config.KURS_ANSWER);

				inhalt = inhalt.replaceAll("%logout%", "logout?sk="
						+ sessionkey);
				inhalt = inhalt.replaceAll("%message%",
						"Der Kurs wurde erfolgreich ge&auml;ndert!");
				inhalt = inhalt.replaceAll("%hidden%",
						"<input type=hidden name='sk' value='" + sessionkey
								+ "'>");
				inhalt = inhalt.replaceAll("%button%",
						"Zur&uuml;ck zur Adminoberfl&auml;che!");
				inhalt = inhalt.replaceAll("%action%", "admin");

				try {
					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			}

		}
	}

	/**
	 * Führt Login aus.
	 * 
	 * @param client
	 *            Verbundener Client
	 * @param args
	 *            Vom Client übergebene Argumente (Username & Passwort)
	 * @param thread
	 *            Thread Informationen
	 * @throws FileNotFoundException
	 *             Datei konnte nicht gefunden werden
	 */
	public static void login(Socket client, String[] args, Thread thread)
			throws FileNotFoundException {

		if (args == null) {
			allowedFileReqest(client, Config.getWebroot() + Config.START_PAGE,
					thread);
			return;
		}

		String[][] arguments = handleArgs(args);
		String username = "";
		String passwort = "";
		boolean login = false;
		Schueler schueler = null;
		Lehrer lehrer = null;
		boolean isLehrer = false;
		boolean isAdmin = false;

		// Überprüfen der Richtigkeit von Namen und Passwort
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("user")) {
				username = Misc.unescape(arguments[i][1]);
			} else if (arguments[i][0].equals("pw")) {
				passwort = Misc.unescape(arguments[i][1]);
			}
		}

		if (!username.equals("") && !passwort.equals("")) {

			if (General.wahl != null) {
				// Durchsuche Userlist nach Name und Passwort
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
				// Druchsuche Lehrerlist nach Name und Passwort
				if (!login) {
					Lehrer[] lehrerList = General.wahl.getLehrerList();
					for (int l = 0; l < lehrerList.length; l++) {
						if (lehrerList[l].getName().equals(username)
								&& lehrerList[l].getPasswort().equals(passwort)) {
							login = true;
							lehrer = lehrerList[l];
							isLehrer = true;
							Print.deb("Lehrer logged in!");
							break;
						}
					}
				}
			}

			// Ueberpruefung des Admin Accounts
			if (!login) {
				if (General.admin.getName().equals(username)
						&& General.admin.getPasswort().equals(passwort)) {
					login = true;
					isAdmin = true;
					Print.deb("Admin logged in!");
				}
			}
		}
		// Reagieren auf erfolgreichen bzw. nicht erfolgreichen Login
		if (login) {
			Print.msg(thread + " Login! User: " + username + ", Passwort: "
					+ passwort);
			if (isLehrer) {
				// Erfolgreicher Login des Admins

				synchronized (HandleConnections.LOCK) {
					lehrer.setSessionkey();
					lehrer.online = true;
				}
				try {
					TCP.send(client, HTTP.HEADER_MOVED);
					TCP.send(client,
							"Location: create?sk=" + lehrer.getSessionkey());
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}

			} else if (isAdmin) {
				synchronized (HandleConnections.LOCK) {
					General.admin.setSessionkey();
					General.admin.online = true;
				}
				try {
					TCP.send(client, HTTP.HEADER_MOVED);
					TCP.send(
							client,
							"Location: admin?sk="
									+ General.admin.getSessionkey());
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			} else {
				// Erfolgreicher Login eines Schuelers
				synchronized (HandleConnections.LOCK) {
					schueler.setSessionkey();
					schueler.online = true;
				}
				try {
					TCP.send(client, HTTP.HEADER_MOVED);
					TCP.send(client,
							"Location: vote?sk=" + schueler.getSessionkey());
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}

			}
		} else {
			// Fehlerhafter Login

			Print.msg(thread + " Fehlerhafter Loginversuch von "
					+ client.getInetAddress());
			String startseite = Misc.read(Config.getWebroot()
					+ Config.START_PAGE);
			startseite = startseite.replaceAll("%username%", username);
			startseite = startseite.replaceAll("%password%", passwort);
			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(
						client,
						startseite
								+ "<script>alert('Login Fehlgeschlagen! Benutzername oder Passwort falsch!');</script>");
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}

		}

	}

	/**
	 * Anfragen auf geschuetzte Seiten
	 * 
	 * @param client
	 *            Verbundener Client
	 * @param filePath
	 *            Datei Pfad zur Seite
	 * @param args
	 *            Vom Clienten mitgegebene Argumente (Sessionkey)
	 * @param userType
	 *            Art des Nutzers (Lehrer(0)/Schueler(1))
	 * @param thread
	 *            Informationen über den Thread
	 * @throws SecurityException
	 *             Nutzer hat keine Berechtigung
	 * @throws FileNotFoundException
	 *             Seite konnte nicht gefunden werden
	 */
	public static void protectedFileReqest(Socket client, String filePath,
			String[] args, int userType, Thread thread)
			throws SecurityException, FileNotFoundException {

		// User identifizieren
		if (args == null) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}
		String[][] arguments = handleArgs(args);
		String sessionkey = null;
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			}
		}
		if (!checkSK(sessionkey, userType)) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}

		// Einlesen der Datei
		String inhalt = "";
		inhalt = Misc.read(filePath);

		// Bearbeitung der Datei (Ersetzen von Makros)
		if (filePath.equals(Config.getWebroot() + Config.KURS_ERSTELLEN_PAGE)) {
			if (General.wahl != null) {
				inhalt = inhalt.replaceAll("%name%", "");
				inhalt = inhalt.replaceAll("%logout%", "logout?sk="
						+ sessionkey);
				inhalt = inhalt.replaceAll("%size%", String.valueOf(""));
				inhalt = inhalt.replaceAll("%min%", String.valueOf(""));
				inhalt = inhalt.replaceAll("%max%", String.valueOf(""));
				inhalt = inhalt.replaceAll("%desc%", "");
				inhalt = inhalt.replaceAll("%action%", "create");
				inhalt = inhalt.replaceAll("%hidden%",
						"<input type=hidden name='sk' value='" + sessionkey
								+ "'>");
			} else {
				inhalt = Misc.read(Config.getWebroot()
						+ Config.SUPER_LEHRER_PAGE);
				inhalt = inhalt.replaceAll("%add%", "create");
				inhalt = inhalt.replaceAll("%change%", "admin");
				inhalt = inhalt.replaceAll("%logout%", "logout?sk="
						+ sessionkey);
				String liste = "";
				if (General.wahl != null) {
					Kurs[] kursListe = General.wahl.getKursListe();
					for (int i = 0; i < kursListe.length; i++) {
						if (i == 0) {
							liste = liste + "<option selected>"
									+ kursListe[i].getName() + "</option>";
						} else {
							liste = liste + "<option>" + kursListe[i].getName()
									+ "</option>";
						}
					}
				}
				inhalt = inhalt.replaceAll("%kursliste%", liste);
				inhalt = inhalt.replaceAll("%overview%", "overview?sk="
						+ sessionkey);
				inhalt = inhalt.replaceAll("%delkurs%", "admin");
				inhalt = inhalt.replaceAll("%create%", "createwahl");
				inhalt = inhalt.replaceAll("%cancel%", "admin");
				inhalt = inhalt.replaceAll("%hidden%",
						"<input type=hidden name='sk' value='" + sessionkey
								+ "'>");
				inhalt += "<script>alert('Es wurde noch keine Wahl erstellt!');</script>";
			}
		} else if (filePath.equals(Config.getWebroot()
				+ Config.WAHL_ERSTELLEN_PAGE)) {
			inhalt = inhalt.replaceAll("%date%", "dd:mm:yy");
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);

			inhalt = inhalt.replaceAll("%G07%", "");
			inhalt = inhalt.replaceAll("%G08%", "");
			inhalt = inhalt.replaceAll("%G09%", "");
			inhalt = inhalt.replaceAll("%G10%", "");
			inhalt = inhalt.replaceAll("%G11%", "");
			inhalt = inhalt.replaceAll("%G12%", "");

			inhalt = inhalt.replaceAll("%R07%", "");
			inhalt = inhalt.replaceAll("%R08%", "");
			inhalt = inhalt.replaceAll("%R09%", "");
			inhalt = inhalt.replaceAll("%R10%", "");

			inhalt = inhalt.replaceAll("%H07%", "");
			inhalt = inhalt.replaceAll("%H08%", "");
			inhalt = inhalt.replaceAll("%H09%", "");
			inhalt = inhalt.replaceAll("%H10%", "");

			inhalt = inhalt.replaceAll("%teacher%", "");
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
		} else if (filePath.equals(Config.getWebroot()
				+ Config.SUPER_LEHRER_PAGE)) {
			inhalt = inhalt.replaceAll("%add%", "create");
			inhalt = inhalt.replaceAll("%change%", "admin");
			inhalt = inhalt.replaceAll("%logout%", "logout?sk=" + sessionkey);
			String liste = "";
			if (General.wahl != null) {
				Kurs[] kursListe = General.wahl.getKursListe();
				for (int i = 0; i < kursListe.length; i++) {
					if (i == 0) {
						liste = liste + "<option selected>"
								+ kursListe[i].getName() + "</option>";
					} else {
						liste = liste + "<option>" + kursListe[i].getName()
								+ "</option>";
					}
				}
			}
			inhalt = inhalt.replaceAll("%kursliste%", liste);
			inhalt = inhalt.replaceAll("%overview%", "overview?sk="
					+ sessionkey);
			inhalt = inhalt.replaceAll("%delkurs%", "admin");
			inhalt = inhalt.replaceAll("%create%", "createwahl");
			inhalt = inhalt.replaceAll("%cancel%", "admin");
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
		} else if (filePath.equals(Config.getWebroot()
				+ Config.KURS_UEBERSICHT_PAGE)) {

			if (General.wahl != null) {
				String kurse = "";
				Kurs[] kursList = General.wahl.getKursListe();
				// Kurslisten erstellen
				for (int k = 0; k < kursList.length; k++) {
					// Zeilenumbruch nach 75 Zeichen in Beschreibung einfuegen
					String beschreibung = kursList[k].getBeschreibung();
					String[] beschrSplit = beschreibung.split("");
					beschreibung = "";
					for (int i = 0; i < beschrSplit.length; i++) {
						beschreibung += beschrSplit[i];
						if (((i + 1) % 75) == 0) {
							beschreibung += "<br>";
						}
					}

					kurse = kurse + "<tr>"
							+ "<td align='left' style='width:150px;'>"
							+ kursList[k].getName() + "</td>"
							+ "<td align='left' style='width:500px;'>"
							+ beschreibung + "</td>"
							+ "<td align='left' style='width:100px;'>"
							+ kursList[k].getJahrgangsberechtigungMin() + " - "
							+ kursList[k].getJahrgangsberechtigungMax()
							+ "</td>" + "</tr>";
				}

				inhalt = inhalt.replaceAll("%kurse%", kurse);
			} else {
				inhalt = inhalt.replaceAll("%kurse%", "");
				inhalt += "<script>alert('Es wurde noch keine Wahl erstellt!');</script>";
			}
		}

		// Senden der Datei
		Print.deb(thread + " Senden von: " + filePath);
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

		if (filePath.equals(Config.getWebroot() + Config.START_PAGE)) {
			inhalt = inhalt.replaceAll("%username%", "");
			inhalt = inhalt.replaceAll("%password%", "");
		}

		// Senden der Datei
		Print.deb(thread + " Senden von: " + filePath);
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
			if (split.length > 1) {
				arguments[i][1] = split[1];
			} else {
				arguments[i][1] = "";
			}
		}
		return arguments;
	}

	private static boolean checkSK(String sessionkey, int userType) {
		if (sessionkey == null) {
			return false;
		}

		boolean authorized = false;
		User user = User.getUserBySk(sessionkey);
		if (user != null) {
			if (userType == Lehrer.LEHRER) {
				authorized = (user instanceof Lehrer)
						|| (user instanceof Admin);
			} else if (userType == Schueler.SCHUELER) {
				authorized = (user instanceof Schueler);
			} else if (userType == Admin.ADMIN) {
				authorized = user instanceof Admin;
			} else if (userType == User.USER) {
				authorized = (user instanceof Lehrer)
						|| (user instanceof Admin)
						|| (user instanceof Schueler);
			}
		}

		if (!authorized) {
			return false;
		}
		return true;
	}
}
