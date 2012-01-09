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
		Kurs[] kursListe = new Kurs[50];
		for (int k = 0; k < kursListe.length; k++){
			kursListe[k] = new Kurs("name"+k,"beschreibung",10,7,12);
		}
		
		//erstelle Schueler
		Schueler[] schuelerListe = new Schueler[500];
		for (int s = 0; s < schuelerListe.length; s++){
			schuelerListe[s] = new Schueler("name"+s,"pass", 5, "gym");
			int erstwunsch = Misc.gen(0, kursListe.length);
			int zweiwunsch = Misc.gen(0, kursListe.length);
			int drittwunsch = Misc.gen(0, kursListe.length);
			while (erstwunsch != zweiwunsch && erstwunsch != drittwunsch && zweiwunsch != drittwunsch ){
				zweiwunsch = Misc.gen(0, kursListe.length);
				drittwunsch = Misc.gen(0, kursListe.length);
			}
			schuelerListe[s].setErstwunsch(kursListe[erstwunsch]);
			schuelerListe[s].setZweitwunsch(kursListe[zweiwunsch]);
			schuelerListe[s].setDrittwunsch(kursListe[drittwunsch]);
			
		}
		
		Auswertung eval = new Auswertung();
		Print.msg("Starte Auswertung!");
		float time = System.currentTimeMillis();
		eval.auswerten(schuelerListe, kursListe);
		Print.msg("Fertig mit der Auswertung nach "+ (System.currentTimeMillis()-time)/1000 +" Sekunden!");
	}
}
