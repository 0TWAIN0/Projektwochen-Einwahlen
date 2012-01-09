package informations;

import misc.Misc;
import misc.Print;

public class General {
	public static Wahl wahl = new Wahl(); // TODO Wahl erstellen
	public static Lehrer[] lehrer = new Lehrer[10];

	public General() {
		for (int i = 0; i < lehrer.length; i++) {
			lehrer[i] = new Lehrer();
		}

		lehrer[0].setName("SuperLehrer");
		lehrer[0].setPasswort(Misc.gen(5));
		Print.deb("Name: " + lehrer[0].getName() + " Passwort: "
				+ lehrer[0].getPasswort());
		for (int i = 1; i < lehrer.length; i++) {
			lehrer[i].setName("Lehrer" + i);
			lehrer[i].setPasswort(Misc.gen(5));
			Print.deb("Name: " + lehrer[i].getName() + " Passwort: "
					+ lehrer[i].getPasswort() + " SK: " + lehrer[i].getSessionkey());
		}

		Schueler[] schueler = new Schueler[10];
		for (int i = 0; i < schueler.length; i++) {
			schueler[i] = new Schueler("Schueler" + i, Misc.gen(5), 7, "gym");
			Print.deb(schueler[i].getName() + "  " + schueler[i].getPasswort());
		}
		wahl.setSchueler(schueler);
	}
}
