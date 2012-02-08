import evaluation.Auswertung;
import misc.Misc;
import misc.Print;
import informations.Kurs;
import informations.Schueler;

public class AuswertungsTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[][] out = new int[500][8];

		for (int i = 0; i < out.length; i++) {
			// erstelle Kurse
			Kurs[] kursListe = new Kurs[101];
			for (int k = 0; k < kursListe.length; k++) {
				kursListe[k] = new Kurs("kursname" + k, "beschreibung", 10, 7,
						12);
			}
			
			int plaetze = 0;
			for (int k = 0; k < kursListe.length; k++) {
				plaetze += kursListe[k].getKursgroesse();
			}
			
			// erstelle Schueler
			Schueler[] schuelerListe = new Schueler[1000];
			for (int s = 0; s < schuelerListe.length; s++) {
				schuelerListe[s] = new Schueler("schülername" + s, "pass", 5,
						"gym");
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
			
			if (schuelerListe.length > plaetze) {
				Print.err("Zu wenig Kursplätze!");
				System.exit(0);
			}

			Print.msg("###Starte Auswertung! Schüler: " + schuelerListe.length + " Kurse: " + kursListe.length + " Plätze: " + plaetze);
			long time = System.currentTimeMillis();
			out[i] = Auswertung.auswerten(schuelerListe, kursListe);
			long ftime = System.currentTimeMillis();
			out[i][6] = schuelerListe.length;
			out[i][7] = (int) (ftime - time);
			Print.deb(out[i][0] + " " + out[i][1] + " " + out[i][2] + " " + out[i][3] + " " + out[i][4] + " " + out[i][5] + " " + out[i][6]);
		}
		
		int uebrigeSchueler = 0;
		int drittwunsch = 0;
		int zweitwunsch = 0;
		int erstwunsch = 0;
		int zeit = 0;
		for (int i = 0; i < out.length; i++) {
			uebrigeSchueler += out[i][4];
			drittwunsch += out[i][3];
			zweitwunsch += out[i][2];
			erstwunsch += out[i][1];
			zeit += out[i][7];
		}
		
		Print.deb("Es wurden " + erstwunsch + " Erstwünsche, " + zweitwunsch + " Zweitwünsche und " + drittwunsch + " erreicht.");
		Print.deb("Desweiteren konnten " + uebrigeSchueler + " der Schüler nicht zugeteilt werden.");
		Print.deb("Es wurden " + zeit + " Millisekunden gebraucht.");
	}
}
