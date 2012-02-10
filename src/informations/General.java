package informations;

import java.util.Calendar;

import misc.Misc;

public class General {
	public static Wahl wahl; 
	public static final Admin admin = new Admin();
	
	public static void gen() throws Exception {
		if (wahl != null){
			throw new Exception("Es existiert bereits eine Wahl!");
		}
		
		int g07 = 10;
		int g08 = 10;
		int g09 = 5;
		int g10 = 3;
		int g11 = 10;
		int g12 = 15;

		int r07 = 9;
		int r08 = 9;
		int r09 = 8;
		int r10 = 7;

		int h07 = 10;
		int h08 = 20;
		int h09 = 11;
		int h10 = 3;
		
		int teacher = 10;
		
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
		
		
		Calendar date = Calendar.getInstance();
		date.set(2013, 8, 6);
		try {
			wahl = new Wahl(date,schuelerListe, lehrerListe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int kurse = schueler / 10;
		for (int i = 1; i <= kurse; i++){
			wahl.addKurs(new Kurs("Kurs " + i,"Beschreibung",10,07,Misc.gen(9, 12)));
		}
	}
	
	public static void vgen() throws Exception{
		if (wahl == null){
			throw new Exception("Es existiert noch keine Wahl!");
		}
		
		Schueler[] schuelerListe = wahl.getSchuelerList();
		Kurs[] kursListe = wahl.getKursListe();
		for (int s = 0; s < schuelerListe.length; s++) {
			int erstwunsch = Misc.gen(0, kursListe.length);
			int zweitwunsch = Misc.gen(0, kursListe.length);
			int drittwunsch = Misc.gen(0, kursListe.length);
			while (true) {
				zweitwunsch = Misc.gen(0, kursListe.length);
				drittwunsch = Misc.gen(0, kursListe.length);
				if (zweitwunsch != erstwunsch) {
					if (zweitwunsch != drittwunsch) {
						if (erstwunsch != drittwunsch) {
							break;
						}
					}
				}
			}
			schuelerListe[s].setErstwunsch(kursListe[erstwunsch]);
			schuelerListe[s].setZweitwunsch(kursListe[zweitwunsch]);
			schuelerListe[s].setDrittwunsch(kursListe[drittwunsch]);
		}
	}
}
