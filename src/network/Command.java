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

import misc.Config;
import misc.Misc;
import misc.Print;

public class Command {

	// TODO THREAD SAVE!!
	/**
	 * Auswertung
	 * 
	 * @param client
	 * @param args
	 * @param thread
	 * @throws FileNotFoundException
	 * @throws SecurityException
	 */
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

		// Überprüfen ob Wahl existiert oder bereits ausgewertet wurde
		if (General.wahl == null) {
			String inhalt = Website.adminPage(sessionkey);
			inhalt += "<script>alert(unescape('Es existiert noch keine Wahl!'));</script>";

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e1) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
			return;
		} else if (General.wahl.ausgewertet) {
			String inhalt = Website.adminPage(sessionkey);
			inhalt += "<script>alert(unescape('Die Wahl wurde bereits ausgewertet!'));</script>";

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e1) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
			return;
		}

		// Auswertung
		Schueler[] schuelerListe = General.wahl.getSchuelerList();
		Kurs[] kursListe = General.wahl.getKursListe();
		Auswertung.auswerten(schuelerListe, kursListe);
		General.wahl.ausgewertet = true;

		// Website zurückliefern
		String inhalt = Website.auswertungAnswerPage(sessionkey);

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

		// Vote Interface senden wenn User nur die Seite angefragt hat und sich
		// noch nicht einwählen will
		if (args.length == 1) {
			String inhalt = Website.votePage(sessionkey);
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

		// Einwahldaten auf Fehler prüfen
		String errorMessage = "";

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
			} else if (erstwunsch.getJahrgangsberechtigungMin() > user
					.getJahrgang()
					|| erstwunsch.getJahrgangsberechtigungMax() < user
							.getJahrgang()) {
				errorMessage += "Der Erstwunsch ist nicht f%FCr deinen Jahrgang freigegeben!%0A";
			} else if (zweitwunsch.getJahrgangsberechtigungMin() > user
					.getJahrgang()
					|| zweitwunsch.getJahrgangsberechtigungMax() < user
							.getJahrgang()) {
				errorMessage += "Der Zweitwunsch ist nicht f%FCr deinen Jahrgang freigegeben!%0A";
			} else if (drittwunsch.getJahrgangsberechtigungMin() > user
					.getJahrgang()
					|| drittwunsch.getJahrgangsberechtigungMax() < user
							.getJahrgang()) {
				errorMessage += "Der Drittwunsch ist nicht f%FCr deinen Jahrgang freigegeben!%0A";
			}
		}

		// Bei keinem Fehler Wünsche speichern und Erfolg melden
		if (errorMessage == "") {

			user.setErstwunsch(erstwunsch);
			user.setZweitwunsch(zweitwunsch);
			user.setDrittwunsch(drittwunsch);

			String inhalt = Website.voteAnswerPage(sessionkey);

			// Senden der Datei
			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		} else {
			// Bei Fehler den Nutzer auf den Fehler hinweisen
			String inhalt = Website.votePage(sessionkey);
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
		int userType = Admin.ADMIN;

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			}
		}
		if (!checkSK(sessionkey, userType)) {
			throw new SecurityException();
		}

		// Simuliere Einwahl
		try {
			General.vgen();
		} catch (Exception e) {
			String inhalt = Website.adminPage(sessionkey);
			inhalt += "<script>alert(unescape('Es existiert noch keine Wahl!'));</script>";

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e1) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
			return;
		}

		// Gebe Erfolg zurück
		String inhalt = Website.adminPage(sessionkey);
		inhalt += "<script>alert(unescape('Die Einwahlen wurde erfolgreich simuliert!'));</script>";

		Print.msg(thread + " Einwahlen wurde simuliert");
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
		int userType = Admin.ADMIN;

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("sk")) {
				sessionkey = arguments[i][1];
			}
		}
		if (!checkSK(sessionkey, userType)) {
			throw new SecurityException();
		}

		// Generiere Wahl
		try {
			General.gen();
		} catch (Exception e) {
			String inhalt = Website.adminPage(sessionkey);
			inhalt += "<script>alert(unescape('Es existiert bereits eine Wahl!'));</script>";

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e1) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
			return;
		}

		// Gebe Erfolg zurück
		String inhalt = Website.wahlErstellenAnswerPage(sessionkey);

		Print.msg(thread + " Wahl wurde erstellt");
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

		// Pberprüfen ob der User eingeloggt war
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

		// User ausloggen
		User user = User.getUserBySk(sessionkey);
		user.online = false;
		user.delSessionkey();

		// Erfolg zurück geben
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

		// Mögliche übergebene Argumente
		boolean change = false; // Kurs ändern
		String changeKurs = "";
		boolean delete = false; // Kurs löschen
		String delKurs = "";
		boolean cancel = false; // Wahl abbrechen

		// Übergebene Argumente analysieren
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
		if (General.wahl == null) {
			String inhalt = Website.adminPage(sessionkey);
			inhalt += "<script>alert(unescape('Es wurde noch keine Wahl erstellt!'));</script>";

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		}
		// ###### Kurs ändern ######
		else if (change) {
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

			// Auf gefundenen Fehler reagieren
			if (!errorMessage.equals("")) {
				String inhalt = Website.adminPage(sessionkey);
				inhalt = inhalt + "<script>alert(unescape('" + errorMessage
						+ "'));</script>";

				try {
					Print.deb(thread + " Kurs ändern failed! Kurs unknown!");

					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			} else {
				// Erfolg zurück geben
				Kurs[] kursListe = General.wahl.getKursListe();
				Kurs kurs = null;
				for (int k = 0; k < kursListe.length; k++) {
					if (changeKurs.equals(kursListe[k].getName())) {
						kurs = kursListe[k];
						break;
					}
				}
				String inhalt = Website.teacherPage(sessionkey, kurs.getName(),
						String.valueOf(kurs.getKursgroesse()),
						String.valueOf(kurs.getJahrgangsberechtigungMin()),
						String.valueOf(kurs.getJahrgangsberechtigungMax()),
						kurs.getBeschreibung());

				try {
					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			}
		}
		// ###### Kurs ändern END ######

		// ###### Kurs löschen ######
		else if (delete) {

			// Die Eingaben auf Fehler überprüfen
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

			// Reaktion wenn ein FEHLER gefunden wurde
			if (!errorMessage.equals("")) {
				String inhalt = Website.adminPage(sessionkey);
				inhalt = inhalt + "<script>alert(unescape('" + errorMessage
						+ "'));</script>";

				try {
					Print.deb(thread + " Kurs löschen failed!");

					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			} else { // Reaktion wenn KEIN Fehler gefunden wurde
				Kurs[] kursListe = General.wahl.getKursListe();
				Kurs kurs = null;
				for (int k = 0; k < kursListe.length; k++) {
					if (delKurs.equals(kursListe[k].getName())) {
						kurs = kursListe[k];
						break;
					}
				}

				// Kurs löschen
				String inhalt = Website.adminPage(sessionkey);
				inhalt = inhalt
						+ "<script>alert(unescape('Der Kurs wurde erfolgreich gels%F6cht!'));</script>";

				try {

					Print.msg(thread + " Kurs wurde gelöscht! Kursname: "
							+ kurs.getName());

					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			}
		}

		// ###### Kurs löschen END ######

		// ###### Wahl abbrechen ######
		else if (cancel) {
			// Kurs löschen
			General.wahl = null;

			// Erfolg melden
			String inhalt = Website.adminPage(sessionkey);
			inhalt = inhalt
					+ "<script>alert(unescape('Der Wahl wurde erfolgreich abgebrochen!'));</script>";

			Print.msg(thread + " Die Wahl wurde abgebrochen!");

			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		}
		// ###### Wahl abbreachen END ######
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

		// Variablen für Eingaben erstellen
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

		// Bei gefundenem Fahler reagieren
		if (!errorMessage.equals("")) {
			errorMessage = "Es wurde ein Fehler in den angegebenen Daten gefunden!%0A%0A"
					+ errorMessage;
			Print.deb(thread + errorMessage);

			String number[] = new String[14];
			number[0] = String.valueOf(g07);
			number[1] = String.valueOf(g08);
			number[2] = String.valueOf(g09);
			number[3] = String.valueOf(g10);
			number[4] = String.valueOf(g11);
			number[5] = String.valueOf(g12);
			number[6] = String.valueOf(r07);
			number[7] = String.valueOf(r08);
			number[8] = String.valueOf(r09);
			number[9] = String.valueOf(r10);
			number[10] = String.valueOf(h07);
			number[11] = String.valueOf(h08);
			number[12] = String.valueOf(h09);
			number[13] = String.valueOf(h10);

			String inhalt = Website.wahlErstellenPage(sessionkey, date, number,
					String.valueOf(teacher));

			try {
				Print.deb(thread + " Wahl erstellen failed!");
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt + "<script>alert(unescape('"
						+ errorMessage + "'));</script>");
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		}
		// Bei keinem Fehler Erfolg melden und Wahl erstellen
		else {
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

			try {
				General.wahl = new Wahl(cal, schuelerListe, lehrerListe);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// Answerpage zurueck senden
			String inhalt = Website.wahlErstellenAnswerPage(sessionkey);

			Print.msg(thread + "Wahl wurde erstellt");
			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, inhalt);
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		}
	}

	/**
	 * Methode zum erstellen/ändern eines Kurses
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
		
		//Variablen für Eingaben
		String name = "";
		int size = 0;
		int min = 0;
		int max = 0;
		String description = "";
		boolean change = false;
		String changeKurs = "";
		Kurs kursToChange = null;
		
		//Argumente analysieren
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
		
		//User verifizieren
		if (!checkSK(sessionkey, userType)) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden! Falscher Sessionkey!");
		}
		//Admin erkennen
		User user = User.getUserBySk(sessionkey);
		if (user instanceof Admin) {
			userType = Admin.ADMIN;
		}

		// Falls der Kurs geaendert werden soll, checken ob der Kurs existiert
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
				String inhalt = Website.adminPage(sessionkey);
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
		
		// Reaktion wenn Fehler gefunden wurde
		if (!errorMessage.equals("")) {
			errorMessage = "Es wurde ein Fehler in den angegebenen Daten gefunden!%0A%0A"
					+ errorMessage;
			Print.deb(thread + errorMessage);
			String page = Website.teacherPage(sessionkey, name,
					String.valueOf(size), String.valueOf(min),
					String.valueOf(max), description);

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
		} 
		// Wenn kein Fehler gefunden wurde
		else {

			if (!change) { // Wenn der Kurs neu erstellt werden soll
				User u = User.getUserBySk(sessionkey);
				String uname = "";
				if (u == null) {
					uname = "UNKNOWN";
				} else {
					uname = u.getName();
				}

				General.wahl
						.addKurs(new Kurs(name, description, size, min, max));

				Print.msg(thread + " Kurs wurde erstellt. Name: " + name
						+ " von " + uname);

				String inhalt = Website.kursAnswerPage(sessionkey,
						userType == Admin.ADMIN, false);

				try {
					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			} else { // Wenn der Kurs geändert werden soll
				User u = User.getUserBySk(sessionkey);
				String uname = "";
				if (u == null) {
					uname = "UNKNOWN";
				} else {
					uname = u.getName();
				}
				Print.msg(thread + " Kurs wurde geändert. Name: " + name
						+ " von " + uname);
				
				//Kurs ändern
				kursToChange.setName(name);
				kursToChange.setBeschreibung(description);
				kursToChange.setKursgroesse(size);
				kursToChange.setJahrgangsberechtigungMin(min);
				kursToChange.setJahrgangsberechtigungMax(max);

				String inhalt = Website.kursAnswerPage(sessionkey,
						userType == Admin.ADMIN, true);

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
		
		//Variablen für EIngaben erstellen
		String[][] arguments = handleArgs(args);
		String username = "";
		String passwort = "";
		
		boolean login = false;
		Schueler schueler = null;
		Lehrer lehrer = null;
		boolean isLehrer = false;
		boolean isAdmin = false;

		//Rausfiltern vom Username und dem Passwort
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i][0].equals("user")) {
				username = Misc.unescape(arguments[i][1]);
			} else if (arguments[i][0].equals("pw")) {
				passwort = Misc.unescape(arguments[i][1]);
			}
		}
		
		// Überprüfen der Richtigkeit von Namen und Passwort
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
			String startseite = Website.loginPage(username, passwort);

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

		// Bearbeitung der Datei (Ersetzen von Makros)
		String inhalt = "";
		if (filePath.equals(Config.getWebroot() + Config.KURS_ERSTELLEN_PAGE)) {
			if (General.wahl != null) {
				inhalt = Website.teacherPage(sessionkey, "", "", "", "", "");
			} else {
				inhalt = Website.adminPage(sessionkey);
				inhalt += "<script>alert('Es wurde noch keine Wahl erstellt!');</script>";
			}
		} else if (filePath.equals(Config.getWebroot()
				+ Config.WAHL_ERSTELLEN_PAGE)) {
			String[] number = new String[14];
			for (int i = 0; i < number.length; i++) {
				number[i] = "";
			}
			inhalt = Website.wahlErstellenPage(sessionkey, "dd:mm:yy", number,
					"");
		} else if (filePath.equals(Config.getWebroot()
				+ Config.SUPER_LEHRER_PAGE)) {
			inhalt = Website.adminPage(sessionkey);
		} else if (filePath.equals(Config.getWebroot()
				+ Config.KURS_UEBERSICHT_PAGE)) {

			inhalt = Website.kursUebersichtPage();
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
		if (filePath.equals(Config.getWebroot() + Config.START_PAGE)) {
			inhalt = Website.loginPage("", "");
		}else {
			inhalt = Misc.read(filePath);
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
