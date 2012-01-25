package network;

import informations.Admin;
import informations.General;
import informations.Kurs;
import informations.Lehrer;
import informations.Schueler;
import informations.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;

import misc.Config;
import misc.Misc;
import misc.Print;

public class Command {

	// TODO THREAD SAVE!!

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
				changeKurs = arguments[i][1];
			} else if (arguments[i][0].equals("del")) {
				delete = true;
			} else if (arguments[i][0].equals("delkurs")) {
				delKurs = arguments[i][1];
			} else if (arguments[i][0].equals("cancel")) {
				cancel = true;
			}
		}
		// Ueberpruefung des Sessionkeys
		if (!sessionkey.equals(General.wahl.admin.getSessionkey())) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden! Falscher Sessionkey!");
		}

		// Untersuchung der Argumente auf Fehler
		// Bei einem/keinem Fehler wird entsprechend reagiert
		if (change) {
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
				inhalt = inhalt.replaceAll("%overview%", "overview.html?sk="
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
					Print.msg(thread + " Kurs ändern failed! Kurs unknown!");

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
					Print.msg(thread + " Kurs ändern eingeleitet!");

					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client, inhalt);
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			}
		} else if (delete) {

		} else if (cancel) {

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
				date = arguments[i][1];
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
		if (!sessionkey.equals(General.wahl.admin.getSessionkey())) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden!");
		}

		Print.deb(thread + "Nutzer verifiziert!");

		// Eingaben auf Fehler pruefen
		String errorMessage = "";
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
				Print.msg(thread + " Kurs erstellen failed!");
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client, page + "<script>alert(unescape('"
						+ errorMessage + "'));</script>");
			} catch (IOException e) {
				Print.err(thread + " Fehler beim Sende an einen Client");
			}
		} else {
			// General.wahl.addKurs(new Kurs(name, description, size, min,
			// max));
			Print.deb(thread + "Wahl wurder erfolgreich hinzugefügt!");
			try {
				TCP.send(client, HTTP.HEADER_OK);
				TCP.send(client,
						"<script>alert('Die Wahl wurde erfolgreich erstellt!');</script>");
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
				name = arguments[i][1];
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
				description = arguments[i][1];
			} else if (arguments[i][0].equals("change")) {
				change = true;
				changeKurs = arguments[i][1];
			}
		}

		if (!checkSK(sessionkey, userType)) {
			throw new SecurityException(
					"Der Benutzer konnte nicht verifiziert werden! Falscher Sessionkey!");
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
				General.wahl
						.addKurs(new Kurs(name, description, size, min, max));
				Print.deb(thread + "Kurs wurder erfolgreich hinzugefügt!");
				try {
					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client,
							"<script>alert('Der Kurs wurde erfolgreich erstellt!');</script>");
				} catch (IOException e) {
					Print.err(thread + " Fehler beim Sende an einen Client");
				}
			} else {
				kursToChange.setName(name);
				kursToChange.setBeschreibung(description);
				kursToChange.setKursgroesse(size);
				kursToChange.setJahrgangsberechtigungMin(min);
				kursToChange.setJahrgangsberechtigungMax(max);
				Print.deb(thread + "Kurs wurder erfolgreich geändert!");
				
				String inhalt = Misc.read(Config.getWebroot()
						+ Config.SUPER_LEHRER_PAGE);

				inhalt = inhalt.replaceAll("%add%", "create");
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
				inhalt = inhalt
						+ "<script>alert(unescape('Der Kurs wurde erfolgreich ge%E4ndert!'));</script>";

				
				try {
					TCP.send(client, HTTP.HEADER_OK);
					TCP.send(client,inhalt);
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
				username = arguments[i][1];
			} else if (arguments[i][0].equals("pw")) {
				passwort = arguments[i][1];
			}
		}

		if (!username.equals("") && !passwort.equals("")) {

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

			// Ueberpruefung des Admin Accounts
			if (!login) {
				if (General.wahl.admin.getName().equals(username)
						&& General.wahl.admin.getPasswort().equals(passwort)) {
					login = true;
					isAdmin = true;
					Print.deb("Admin logged in!");
				}
			}
		}
		// Reagieren auf erfolgreichen bzw. nicht erfolgreichen Login
		if (login) {
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
					General.wahl.admin.setSessionkey();
					General.wahl.admin.online = true;
				}
				try {
					TCP.send(client, HTTP.HEADER_MOVED);
					TCP.send(
							client,
							"Location: admin?sk="
									+ General.wahl.admin.getSessionkey());
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
			String startseite = Misc.read(Config.getWebroot()
					+ Config.START_PAGE);
			startseite = startseite.replaceAll("%username%", username);
			startseite = startseite.replaceAll("%password%", passwort);
			try {
				Print.msg(thread + " Login failed!");
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
			inhalt = inhalt.replaceAll("%name%", "");
			inhalt = inhalt.replaceAll("%size%", String.valueOf(""));
			inhalt = inhalt.replaceAll("%min%", String.valueOf(""));
			inhalt = inhalt.replaceAll("%max%", String.valueOf(""));
			inhalt = inhalt.replaceAll("%desc%", "");
			inhalt = inhalt.replaceAll("%action%", "create");
			inhalt = inhalt.replaceAll("%hidden%",
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
		} else if (filePath.equals(Config.getWebroot()
				+ Config.WAHL_ERSTELLEN_PAGE)) {
			inhalt = inhalt.replaceAll("%date%", "dd:mm:yy");

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
			Kurs[] kursListe = General.wahl.getKursListe();
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
					"<input type=hidden name='sk' value='" + sessionkey + "'>");
		} else if (filePath.equals(Config.getWebroot()
				+ Config.KURS_UEBERSICHT_PAGE)) {
			String kurse = "";

			/*
			 * <tr> <td align="left" style="width:150px;"> </td> <td
			 * align="left" style="width:500px;"> </td> <td align="left"
			 * style="width:100px;"> </td> </tr>
			 */

			Kurs[] kursList = General.wahl.getKursListe();
			for (int k = 0; k < kursList.length; k++) {
				kurse = kurse + "<tr>"
						+ "<td align='left' style='width:150px;'>"
						+ kursList[k].getName() + "</td>"
						+ "<td align='left' style='width:500px;'>"
						+ kursList[k].getBeschreibung() + "</td>"
						+ "<td align='left' style='width:100px;'>"
						+ kursList[k].getJahrgangsberechtigungMin() + " - "
						+ kursList[k].getJahrgangsberechtigungMax() + "</td>"
						+ "</tr>";
			}

			inhalt = inhalt.replaceAll("%kurse%", kurse);
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

		if (filePath.equals(Config.getWebroot() + Config.START_PAGE)) {
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
		if (userType == Lehrer.LEHRER) {
			Lehrer[] userList = General.wahl.getLehrerList();
			for (int i = 0; i < userList.length; i++) {
				if (sessionkey.equals(userList[i].getSessionkey())) {
					if (userList[i].online) {
						authorized = true;
						break;
					}
				}
			}
			if (sessionkey.equals(General.wahl.admin.getSessionkey())) {
				if (General.wahl.admin.online) {
					authorized = true;
				}
			}
		} else if (userType == Schueler.SCHUELER) {
			Schueler[] userList = General.wahl.getSchuelerList();
			for (int i = 0; i < userList.length; i++) {
				if (sessionkey.equals(userList[i].getSessionkey())) {
					if (userList[i].online) {
						authorized = true;
						break;
					}
				}
			}
			if (sessionkey.equals(General.wahl.admin.getSessionkey())) {
				if (General.wahl.admin.online) {
					authorized = true;
				}
			}
		} else if (userType == Admin.ADMIN) {
			if (sessionkey.equals(General.wahl.admin.getSessionkey())) {
				if (General.wahl.admin.online) {
					authorized = true;
				}
			}

		} else if (userType == User.USER) {
			User[] userList = General.wahl.getLehrerList();
			for (int i = 0; i < userList.length; i++) {
				if (sessionkey.equals(userList[i].getSessionkey())) {
					if (userList[i].online) {
						authorized = true;
						break;
					}
				}
			}

			userList = General.wahl.getSchuelerList();
			for (int i = 0; i < userList.length; i++) {
				if (sessionkey.equals(userList[i].getSessionkey())) {
					if (userList[i].online) {
						authorized = true;
						break;
					}
				}
			}

			if (sessionkey.equals(General.wahl.admin.getSessionkey())) {
				if (General.wahl.admin.online) {
					authorized = true;
				}
			}

		}

		if (!authorized) {
			return false;
		}
		return true;
	}
}
