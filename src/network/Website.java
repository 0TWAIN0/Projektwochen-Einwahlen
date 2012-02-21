package network;

import informations.General;
import informations.Kurs;
import informations.Lehrer;
import informations.Schueler;

import java.io.FileNotFoundException;

import misc.Config;
import misc.Misc;

public class Website {
	private static final String HIDDEN_SESSIONKEY_PATTERN = "%hidden%";
	private static final String HIDDEN_SESSIONKEY = "<input type=hidden name='sk' value='%sessionkey%'>";
	private static final String LOGOUT_PATTERN = "%logout%";
	private static final String LOGOUT = "logout?sk=%sessionkey%";
	private static final String COMMAND_PATTERN = "%action%";
	private static final String SESSIONKEY_PATTERN = "%sessionkey%";
	private static final String KURS_OVERVIEW_PATTERN = "%overview%";
	private static final String KURS_OVERVIEW = "overview?sk=%sessionkey%";
	private static final String KURSLISTE_PATTERN = "%kursliste%";
	
	public static String adminPage(String sessionkey) throws FileNotFoundException{
		
		/*
		 * Pattern:
		 * 	%logout%
		 * 	%hidden%
		 * 	%kursliste%
		 * 	%overview%
		 */
		
		String inhalt = Misc.read(Config.SUPER_LEHRER_PAGE);
		
		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN, HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN,sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN, LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll(KURSLISTE_PATTERN, getKursliste());
		inhalt = inhalt.replaceAll(KURS_OVERVIEW_PATTERN, KURS_OVERVIEW.replaceAll(SESSIONKEY_PATTERN, sessionkey));

		return inhalt;
	}

	public static String wahlErstellenAnswerPage(String sessionkey) throws FileNotFoundException{
		
		/*
		 * Pattern:
		 * 	%logout%
		 * 	%hidden%
		 * 	%schueler%
		 * 	%lehrer%
		 */
		
		String inhalt = Misc.read(Config.WAHL_ANSWER);
		
		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN, HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN,sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN, LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		inhalt = inhalt.replaceAll("%schueler%",getSchuelerliste());
		inhalt = inhalt.replaceAll("%lehrer%", getLehrerliste());
		
		return inhalt;
	}
	
	public static String kursAnswerPage(String sessionkey,boolean isAdmin, boolean change) throws FileNotFoundException{
		
		/*
		 * Pattern:
		 * 	%logout%
		 * 	%hidden%
		 * 	%action%
		 * 	%message%
		 * 	%button%
		 */
		
		String inhalt = Misc.read(Config.getWebroot()
				+ Config.KURS_ANSWER);
		
		inhalt = inhalt.replaceAll(HIDDEN_SESSIONKEY_PATTERN, HIDDEN_SESSIONKEY.replaceAll(SESSIONKEY_PATTERN,sessionkey));
		inhalt = inhalt.replaceAll(LOGOUT_PATTERN, LOGOUT.replaceAll(SESSIONKEY_PATTERN, sessionkey));
		if (change){
			inhalt = inhalt.replaceAll("%message%", "Der Kurs wurde erfolgreich ge&auml;ndert!");
			inhalt = inhalt.replaceAll("%button%", "Zur&uuml;ck zur Adminoberfl&auml;che!");
			inhalt = inhalt.replaceAll(COMMAND_PATTERN, "admin");
		}else{
			inhalt = inhalt.replaceAll("%message%", "Der Kurs wurde erfolgreich erstellt!");
			if (isAdmin){
				inhalt = inhalt.replaceAll("%button%", "Zur&uuml;ck zur Adminoberfl&auml;che!");
				inhalt = inhalt.replaceAll(COMMAND_PATTERN, "admin");
			}else{
				inhalt = inhalt.replaceAll("%button%", "Noch einen Kurs erstellen!");
				inhalt = inhalt.replaceAll(COMMAND_PATTERN, "create");
			}
		}
		return inhalt;
	}
	
	private static String getKursliste(){
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
	private static String getSchuelerliste(){
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
	private static String getLehrerliste(){
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
