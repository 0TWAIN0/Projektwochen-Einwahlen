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
		// erstelle Kurse
		Kurs[] kursListe = new Kurs[51];
		for (int k = 0; k < kursListe.length; k++){
			kursListe[k] = new Kurs("kursname"+k,"beschreibung",10,7,12);
		}
		
		//erstelle Schueler
		Schueler[] schuelerListe = new Schueler[500];
		for (int s = 0; s < schuelerListe.length; s++){
			schuelerListe[s] = new Schueler("schülername"+s,"pass", 5, "gym");
			int erstwunsch = Misc.gen(0, kursListe.length);
			int zweitwunsch = Misc.gen(0, kursListe.length);
			int drittwunsch = Misc.gen(0, kursListe.length);
			while (true){
				zweitwunsch = Misc.gen(0, kursListe.length);
				drittwunsch = Misc.gen(0, kursListe.length);
				if (zweitwunsch != erstwunsch){
					if (zweitwunsch != drittwunsch){
						if (erstwunsch != drittwunsch){
							break;
						}
					}
				}
			}
			schuelerListe[s].setErstwunsch(kursListe[erstwunsch]);
			schuelerListe[s].setZweitwunsch(kursListe[zweitwunsch]);
			schuelerListe[s].setDrittwunsch(kursListe[drittwunsch]);			
		}
		
		int plaetze = 0;
		for (int k = 0; k < kursListe.length; k++){
			plaetze += kursListe[k].getKursgroesse();
		}
		if (schuelerListe.length > plaetze){
			Print.err("Zu wenig Kursplätze!");
			System.exit(0);
		}
		
		Auswertung eval = new Auswertung();
		Print.msg("Starte Auswertung!");
		long time = System.currentTimeMillis();
		eval.auswerten(schuelerListe, kursListe);
		long ftime = System.currentTimeMillis();
		Print.msg("Fertig mit der Auswertung nach "+ (ftime-time) +" Millisekunden!");
	}
}
