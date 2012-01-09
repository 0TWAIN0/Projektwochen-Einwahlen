package evaluation;

import misc.Print;
import informations.Kurs;
import informations.Schueler;

public class Auswertung {

	public void auswerten(Schueler[] schueler, Kurs[] kurs) {

		// Schueler den Erstwuenschen zuteilen
		for (int i = 0; i < schueler.length; i++) {
			Kurs erstwunsch = schueler[i].getErstwunsch();
			for (int p = 0; p < kurs.length; p++) {
				if (kurs[p].getName().equals(erstwunsch.getName())) {
					kurs[p].addSchueler(schueler[i]);
					break;
				}
			}
		}

		// Kurze Kurs uebersicht
		for (int k = 0; k < kurs.length; k++) {
			String teilies = "";
			for (int s = 0; s < kurs[k].getSchuelerliste().length; s++) {
				teilies += kurs[k].getSchuelerliste()[s].getName() + "; ";
			}
			Print.msg("KursName: " + kurs[k].getName() + " Größe: "
					+ kurs[k].getTatsaechlicheKursgroesse() + " Schüler: "
					+ teilies);
		}
		
		int loops = 0;
		while(checkAufUeberfuellung(kurs) && loops <= 3){
			loops++;
			// Ueberpruefung auf Ueberfullung der Kurse && Verschiebung moeglicher
			// Schueler in 2.Wunsch
			for (int i = 0; i < kurs.length; i++) {
				if (kurs[i].getKursgroesse() < kurs[i]
						.getTatsaechlicheKursgroesse()) {
					int ueberfuellung = kurs[i].getTatsaechlicheKursgroesse()
							- kurs[i].getKursgroesse();
					Print.msg("Kurs Überfüllung "+loops+".Grad! Anzahl: " + ueberfuellung
							+ " Name: " + kurs[i].getName());
					Schueler[] kursSchuelerliste = kurs[i].getSchuelerliste();
					for (int b = 0; b < kursSchuelerliste.length; b++) {
						Kurs zweitwunsch = kursSchuelerliste[b].getZweitwunsch();
						if (zweitwunsch.getKursgroesse() > zweitwunsch
								.getTatsaechlicheKursgroesse()) {
							Print.msg("Move "+loops+".Grad! " + kursSchuelerliste[b].getName());
							zweitwunsch.addSchueler(kursSchuelerliste[b]);
							kurs[i].removeSchueler(kursSchuelerliste[b]);
	
						}
						if (kurs[i].getKursgroesse() >= kurs[i]
								.getTatsaechlicheKursgroesse()) {
							break;
						}
					}
	
				}
			}
	
			// Ueberpruefung auf immer noch existierende Ueberfullung der Kurse &&
			// Verschiebung moeglicher Schueler in 2.Wunsch => neue Ueberfuellungen
			for (int i = 0; i < kurs.length; i++) {
				if (kurs[i].getKursgroesse() < kurs[i]
						.getTatsaechlicheKursgroesse()) {
					int ueberfuellung = kurs[i].getTatsaechlicheKursgroesse()
							- kurs[i].getKursgroesse();
					Print.msg("Kurs Überfüllung "+loops+".2.Grad! Anzahl: " + ueberfuellung
							+ " Name: " + kurs[i].getName());
					Schueler[] kursSchuelerliste = kurs[i].getSchuelerliste();
					for (int b = 0; b < kursSchuelerliste.length; b++) {
						Kurs zweitwunsch = kursSchuelerliste[b].getZweitwunsch();
						if (kursSchuelerliste[b].getErstwunsch() == kurs[i]) {
							Print.msg("Move "+loops+".2. Grad! " + kursSchuelerliste[b].getName());
							zweitwunsch.addSchueler(kursSchuelerliste[b]);
							kurs[i].removeSchueler(kursSchuelerliste[b]);
						}
						if (kurs[i].getKursgroesse() >= kurs[i].getTatsaechlicheKursgroesse()) {
							Print.deb("break");
							break;
						}
					}
	
				}
			}
		}
		
		if (checkAufUeberfuellung(kurs)){
			//Drittwunsch zuordnen
		}
		
	}
	
	public boolean checkAufUeberfuellung(Kurs[] kurs){
		for (int i = 0; i < kurs.length; i++) {
			if (kurs[i].getKursgroesse() < kurs[i]
					.getTatsaechlicheKursgroesse()) {
				return true;
			}
		}
		return false;
	}
	
}