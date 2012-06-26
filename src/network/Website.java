package network;

import informations.General;
import informations.Kurs;
import informations.Lehrer;
import informations.Schueler;

import java.io.FileNotFoundException;

import misc.Array;
import misc.Config;
import misc.Misc;

/**
 * Klasse mit Funktionen zum Erstellen von den Webseiten
 * 
 * @author jakob
 * 
 */

public class Website {

	// Allgemeine Konstanten
	private static final String HIDDEN_SESSIONKEY_PATTERN = "%hidden%";
	private static final String HIDDEN_SESSIONKEY = "<input type=hidden name='sk' value='%sessionkey%'>";
	private static final String LOGOUT_PATTERN = "%logout%";
	private static final String LOGOUT = "logout?sk=%sessionkey%";
	private static final String COMMAND_PATTERN = "%action%";
	private static final String SESSIONKEY_PATTERN = "%sessionkey%";
	private static final String KURS_OVERVIEW_PATTERN = "%overview%";
	private static final String KURS_OVERVIEW = "overview?sk=%sessionkey%";
	private static final String KURSLISTE_PATTERN = "%kursliste%";

	// TODO Eventuell noch verbesserungswürdig!!!! Beim einsetzen prüfen!!

	/**
	 * Erstellen der Admin Page
	 * 
	 * @param sessionkey
	 * @return inhalt
	 * @throws FileNotFoundException
	 */
	public static String adminPage(String sessionkey)
			throws FileNotFoundException {

		/*
		 * Pattern: %logout% %hidden% %kursliste% %overview%
		 */

		String inhalt = Misc.read(Config.getWebroot()
				+ Config.SUPER_LEHRER_PAGE);

		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN,
				HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN,
				LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(KURSLISTE_PATTERN, getKursliste());
		inhalt = inhalt.replaceAll(KURS_OVERVIEW_PATTERN,
				KURS_OVERVIEW.replaceAll(SESSIONKEY_PATTERN, sessionkey));

		return inhalt;
	}

	/**
	 * Erstellen der Antwortseite von der Wahl erstellen Seite
	 * 
	 * @param sessionkey
	 * @return inhalt
	 * @throws FileNotFoundException
	 */
	public static String wahlErstellenAnswerPage(String sessionkey)
			throws FileNotFoundException {

		/*
		 * Pattern: %logout% %hidden% %schueler% %lehrer%
		 */

		String inhalt = Misc.read(Config.getWebroot() + Config.WAHL_ANSWER);

		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN,
				HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN,
				LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll("%schueler%", getSchuelerliste());
		inhalt = inhalt.replaceAll("%lehrer%", getLehrerliste());

		return inhalt;
	}

	/**
	 * Erstellen der Antwortseite für dire Kurst-Erstellen-Seite
	 * 
	 * @param sessionkey
	 * @param isAdmin
	 * @param change
	 * @return inhalt
	 * @throws FileNotFoundException
	 */
	public static String kursAnswerPage(String sessionkey, boolean isAdmin,
			boolean change) throws FileNotFoundException {

		/*
		 * Pattern: %logout% %hidden% %action% %message% %button%
		 */

		String inhalt = Misc.read(Config.getWebroot() + Config.KURS_ANSWER);

		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN,
				HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN,
				LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		if (change) {
			inhalt = inhalt.replaceAll("%message%",
					"Der Kurs wurde erfolgreich ge&auml;ndert!");
			inhalt = inhalt.replaceAll("%button%",
					"Zur&uuml;ck zur Adminoberfl&auml;che!");
			inhalt = inhalt.replaceAll(COMMAND_PATTERN, "admin");
		} else {
			inhalt = inhalt.replaceAll("%message%",
					"Der Kurs wurde erfolgreich erstellt!");
			if (isAdmin) {
				inhalt = inhalt.replaceAll("%button%",
						"Zur&uuml;ck zur Adminoberfl&auml;che!");
				inhalt = inhalt.replaceAll(COMMAND_PATTERN, "admin");
			} else {
				inhalt = inhalt.replaceAll("%button%",
						"Noch einen Kurs erstellen!");
				inhalt = inhalt.replaceAll(COMMAND_PATTERN, "create");
			}
		}
		return inhalt;
	}

	/**
	 * Erstellen der Antwortseite für die Wahl
	 * 
	 * @param sessionkey
	 * @return inhalt
	 * @throws FileNotFoundException
	 */
	public static String voteAnswerPage(String sessionkey)
			throws FileNotFoundException {
		/*
		 * Pattern: %logout%
		 */

		String inhalt = Misc
				.read(Config.getWebroot() + Config.KURS_WAHL_ANSWER);

		inhalt = inhalt.replaceAll(LOGOUT_PATTERN,
				LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));

		return inhalt;
	}

	/**
	 * Erstellt die Antwort Seite zur Auswertung <--------
	 * 
	 * @param sessionkey
	 * @param kursListe
	 * @return inhalt
	 * @throws FileNotFoundException
	 */
	public static String auswertungAnswerPage(String sessionkey)
			throws FileNotFoundException {

		/*
		 * Pattern: %logout% %sessionkey% %hidden% %kurse%
		 */

		String inhalt = Misc.read(Config.getWebroot()
				+ Config.KURS_AUSWERTUNG_ANSWER);

		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN,
				HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN,
				LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(SESSIONKEY_PATTERN, sessionkey);

		// Erstellung der HTML-Tabelle der Auswertung
		Kurs[] kursListe = General.wahl.getKursListe();
		String kurse = "";
		Schueler[] schuelerListe;

		// Erstellt HTML-Kurs-Tabellen mit Schülern
		for (int k = 0; k < kursListe.length; k++) {
			kurse += "<p ><h3 align='center'>"
					+ kursListe[k].getName()
					+ "</h3></p>"
					+ "<table border='1' align='center'><thead><tr>"
					+ "<th align='center' style='width:150px;'>Sch&uuml;ler</th>"
					+ "</tr></thead>";
			schuelerListe = kursListe[k].getSchuelerliste();
			for (int s = 0; s < schuelerListe.length; s++) {
				kurse += "<tr><td align='left' style='width:150px;'>"
						+ schuelerListe[s].getName() + "</td></tr>";
			}
			kurse += "</table><br>";
		}

		// Erstellt Array mit nicht zugeteilten Schülern
		schuelerListe = General.wahl.getSchuelerList();
		Schueler[] failedSchueler = new Schueler[0];
		String[] reason = new String[0];

		for (int s = 0; s < schuelerListe.length; s++) {
			if (schuelerListe[s].getErstwunsch() == null
					|| schuelerListe[s].getZweitwunsch() == null
					|| schuelerListe[s].getDrittwunsch() == null) {
				failedSchueler = Schueler.valueOf(Array
						.addField(failedSchueler));
				reason = Misc.stringValueOf(Array.addField(reason));
				failedSchueler[failedSchueler.length - 1] = schuelerListe[s];
				reason[reason.length - 1] = "Sch&uuml;ler hat sich nicht eingew&auml;hlt!";
			}
		}

		// Erstellt HTML-Tabelle mit nicht zugeteilten Schülern
		kurse += "<p ><h3 align='center'>Nicht zugeteilte Sch&uuml;ler</h3></p>"
				+ "<table border='1' align='center'><thead><tr>"
				+ "<th align='center' style='width:150px;'>Sch&uuml;ler</th>"
				+ "<th align='center' style='width:300px;'>Grund</th>"
				+ "</tr></thead>";
		for (int s = 0; s < failedSchueler.length; s++) {
			kurse += "<tr><td align='left' style='width:150px;'>"
					+ failedSchueler[s].getName() + "</td>";
			kurse += "<td align='left' style='width:300px;'>" + reason[s]
					+ "</td></tr>";
		}
		kurse += "</table><br>";

		kurse += "<p ><h3 align='center'>&Uuml;berf&uuml;llte Kurse</h3></p>"
				+ "<table border='1' align='center'><thead><tr>"
				+ "<th align='center' style='width:150px;'>Sch&uuml;ler</th>"
				+ "</tr></thead>";
		for (int k = 0; k < kursListe.length; k++) {
			if (kursListe[k].getKursgroesse() < kursListe[k]
					.getTatsaechlicheKursgroesse()) {
				kurse += "<tr><td align='left' style='width:150px;'>"
						+ kursListe[k].getName() + "</td>";
			}
		}
		kurse += "</table><br>";

		inhalt = inhalt.replaceAll("%kurse%", kurse);

		return inhalt;
	}

	/**
	 * Erstellt die Seite zum Erstellen einer Wahl
	 * 
	 * @param sessionkey
	 * @param date
	 * @param number
	 * @param teacher
	 * @return inhalt
	 * @throws FileNotFoundException
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public static String wahlErstellenPage(String sessionkey, String date,
			String[] number, String teacher) throws FileNotFoundException,
			ArrayIndexOutOfBoundsException {
		/*
		 * Pattern: %logout% %hidden% %date% %G07% -> %G12% %R07% -> %R10% %H07%
		 * -> %H10% %teacher%
		 */

		if (number.length != 14) {
			throw new ArrayIndexOutOfBoundsException(
					"Array 'number' hat falsche Zahl an Feldern!");
		}

		String inhalt = Misc.read(Config.getWebroot()
				+ Config.WAHL_ERSTELLEN_PAGE);

		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN,
				HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN,
				LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));

		inhalt = inhalt.replaceAll("%date%", date);

		inhalt = inhalt.replaceAll("%G07%", number[0]);
		inhalt = inhalt.replaceAll("%G08%", number[1]);
		inhalt = inhalt.replaceAll("%G09%", number[2]);
		inhalt = inhalt.replaceAll("%G10%", number[3]);
		inhalt = inhalt.replaceAll("%G11%", number[4]);
		inhalt = inhalt.replaceAll("%G12%", number[5]);

		inhalt = inhalt.replaceAll("%R07%", number[6]);
		inhalt = inhalt.replaceAll("%R08%", number[7]);
		inhalt = inhalt.replaceAll("%R09%", number[8]);
		inhalt = inhalt.replaceAll("%R10%", number[9]);

		inhalt = inhalt.replaceAll("%H07%", number[10]);
		inhalt = inhalt.replaceAll("%H08%", number[11]);
		inhalt = inhalt.replaceAll("%H09%", number[12]);
		inhalt = inhalt.replaceAll("%H10%", number[13]);

		inhalt = inhalt.replaceAll("%teacher%", teacher);

		return inhalt;
	}

	/**
	 * Erstellt die Login Seite
	 * 
	 * @param username
	 * @param password
	 * @return inhalt
	 * @throws FileNotFoundException
	 */
	public static String loginPage(String username, String password)
			throws FileNotFoundException {
		/*
		 * Pattern: %username% %password%
		 */

		String inhalt = Misc.read(Config.getWebroot() + Config.START_PAGE);

		inhalt = inhalt.replaceAll("%username%", username);
		inhalt = inhalt.replaceAll("%password%", password);

		return inhalt;
	}

	/**
	 * Erstellt eine Kursübersicht
	 * 
	 * @return inhalt
	 * @throws FileNotFoundException
	 */
	public static String kursUebersichtPage() throws FileNotFoundException {
		/*
		 * Pattern: %kurse%
		 */

		String inhalt = Misc.read(Config.getWebroot()
				+ Config.KURS_UEBERSICHT_PAGE);
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
						+ kursList[k].getJahrgangsberechtigungMax() + "</td>"
						+ "</tr>";
			}

			inhalt = inhalt.replaceAll("%kurse%", kurse);
		} else {
			inhalt = inhalt.replaceAll("%kurse%", "");
			inhalt += "<script>alert('Es wurde noch keine Wahl erstellt!');</script>";
		}

		return inhalt;
	}

	/**
	 * Erstellt Seite zum erstellen von einem Kurs
	 * 
	 * @param sessionkey
	 * @param name
	 * @param size
	 * @param min
	 * @param max
	 * @param desc
	 * @return inhalt
	 * @throws FileNotFoundException
	 */
	public static String teacherPage(String sessionkey, String name,
			String size, String min, String max, String desc)
			throws FileNotFoundException {
		/*
		 * Pattern: %logout% %hidden% %name% %size% %min% %max% %desc%
		 */

		String inhalt = Misc.read(Config.getWebroot()
				+ Config.KURS_ERSTELLEN_PAGE);

		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN,
				HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN,
				LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));

		inhalt = inhalt.replaceAll("%name%", name);
		inhalt = inhalt.replaceAll("%size%", size);
		inhalt = inhalt.replaceAll("%min%", min);
		inhalt = inhalt.replaceAll("%max%", max);
		inhalt = inhalt.replaceAll("%desc%", desc);

		return inhalt;
	}

	/**
	 * Erstellt Seite zum Einwählen
	 * 
	 * @param sessionkey
	 * @return inhalt
	 * @throws FileNotFoundException
	 */
	public static String votePage(String sessionkey)
			throws FileNotFoundException {
		/*
		 * Pattern: %logout% %hidden% %sessionkey% %options%
		 */

		String inhalt = Misc.read(Config.getWebroot() + Config.KURS_WAHL_PAGE);

		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN,
				HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN,
				LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(SESSIONKEY_PATTERN, sessionkey);
		inhalt = inhalt.replaceAll("%options%", getKursliste());

		return inhalt;
	}

	/**
	 * Erstell User Liste
	 * 
	 * @param sessionkey
	 * @return inhalt
	 * @throws FileNotFoundException
	 */
	public static String userListPage(String sessionkey)
			throws FileNotFoundException {

		/*
		 * Pattern: %logout% %hidden% %schueler% %lehrer%
		 */

		String inhalt = Misc.read(Config.getWebroot() + Config.USER_LIST);

		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN,
				HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN,
				LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll("%schueler%", getExtendedSchuelerliste());
		inhalt = inhalt.replaceAll("%lehrer%", getLehrerliste());

		return inhalt;
	}

	// Interne Funktionen

	// Funktion zum erstellen einer Liste aller Kurse
	private static String getKursliste() {
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
		return liste;
	}

	// Funktion zum erstellen einer Liste aller Schueler
	private static String getSchuelerliste() {
		String liste = "";
		if (General.wahl != null) {
			Schueler[] schuelerListe = General.wahl.getSchuelerList();
			for (int i = 0; i < schuelerListe.length; i++) {
				liste += "<tr>" + "<td align='left' style='width:150px;'>"
						+ schuelerListe[i].getName() + "</td>"
						+ "<td align='left' style='width:150px;'>"
						+ schuelerListe[i].getPasswort() + "</td></tr>";
			}
		}
		return liste;
	}

	// Funktion zum erstellen einer Liste aller Schueler mit Kursen
	private static String getExtendedSchuelerliste() {
		String liste = "";
		if (General.wahl != null) {
			Schueler[] schuelerListe = General.wahl.getSchuelerList();
			for (int i = 0; i < schuelerListe.length; i++) {
				String erstwunsch;
				String zweitwunsch;
				String drittwunsch;
				
				if (schuelerListe[i].getErstwunsch() != null) {
					erstwunsch = schuelerListe[i].getErstwunsch()
							.getName();
				}else {
					erstwunsch = "";
				}
				if (schuelerListe[i].getZweitwunsch() != null) {
					zweitwunsch = schuelerListe[i].getZweitwunsch()
							.getName();
				}else {
					zweitwunsch = "";
				}
				if (schuelerListe[i].getDrittwunsch() != null) {
					drittwunsch = schuelerListe[i].getDrittwunsch()
							.getName();
				}else {
					drittwunsch = "";
				}

				liste += "<tr>" + "<td align='left' style='width:150px;'>"
						+ schuelerListe[i].getName() + "</td>"
						+ "<td align='left' style='width:150px;'>"
						+ schuelerListe[i].getPasswort() + "</td>"
						+ "<td align='left' style='width:150px;'>" + erstwunsch
						+ "</td>" + "<td align='left' style='width:150px;'>"
						+ zweitwunsch + "</td>"
						+ "<td align='left' style='width:150px;'>"
						+ drittwunsch + "</td>";

				Kurs[] kursListe = General.wahl.getKursListe();
				kurse: for (int k = 0; k < kursListe.length; k++) {
					Schueler[] schueler = kursListe[k].getSchuelerliste();
					for (int s = 0; s < schueler.length; s++) {
						if (schuelerListe[i].equals(schueler[s])) {
							liste += "<td align='left' style='width:150px;'>"
									+ kursListe[k].getName() + "</td></tr>";
							break kurse;
						}
					}
				}
			}
		}
		return liste;
	}

	// Funktion uim erstellen einer Liste aller Lehrer
	private static String getLehrerliste() {
		String liste = "";
		if (General.wahl != null) {
			Lehrer[] lehrerListe = General.wahl.getLehrerList();
			for (int i = 0; i < lehrerListe.length; i++) {
				liste += "<tr>" + "<td align='left' style='width:150px;'>"
						+ lehrerListe[i].getName() + "</td>"
						+ "<td align='left' style='width:150px;'>"
						+ lehrerListe[i].getPasswort() + "</td></tr>";
			}
		}
		return liste;
	}
}
