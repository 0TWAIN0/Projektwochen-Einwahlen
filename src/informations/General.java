package informations;

import java.util.Calendar;

import misc.Misc;
import misc.Print;

public class General {
	public static Wahl wahl; // TODO Wahl erstellen
	private Calendar date = Calendar.getInstance();
	public static Admin admin = new Admin();

	public General() {
		Lehrer[] lehrer = new Lehrer[10];
		for (int i = 0; i < lehrer.length; i++) {
			lehrer[i] = new Lehrer("Lehrer" + i,Misc.gen(5));
			Print.deb("Name: " + lehrer[i].getName() + " Passwort: "
					+ lehrer[i].getPasswort() + " SK: " + lehrer[i].getSessionkey());
		}
		
		Schueler[] schueler = new Schueler[10];
		for (int i = 0; i < schueler.length; i++) {
			schueler[i] = new Schueler("Schueler" + i, Misc.gen(5), 7, "gym");
			Print.deb(schueler[i].getName() + "  " + schueler[i].getPasswort());
		}
		
		date.set(2012, 8, 6);
		try {
			wahl = new Wahl(date,schueler, lehrer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
