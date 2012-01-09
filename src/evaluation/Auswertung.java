package evaluation;

import informations.Kurs;
import informations.Schueler;

public class Auswertung {
	
	public void auswerten(Schueler[] schueler, Kurs[] kurs) {
		
		// Schueler den Erstwuenschen zuteilen
		for (int i = 0; i < schueler.length; i++) {
			Kurs erstwunsch = schueler[i].getErstwunsch();
			
			for (int p = 0; p < kurs.length; p++) {
				
				if (kurs[p].getName().equals(erstwunsch.getName())) {
					kurs[p].setTatsaechlicheKursgroesse(kurs[p]
							.getTatsaechlicheKursgroesse() + 1);
					kurs[p].setSchuelerliste(schueler[i]);
					break;
				}
			}
		}
// Ueberpruefung auf Ueberfullung der Kurse
		for(int i = 0; i<kurs.length; i++) {
			if(kurs[i].getKursgroesse() < kurs[i].getTatsaechlicheKursgroesse()) {
				int ueberfuellung = kurs[i].getTatsaechlicheKursgroesse() - kurs[i].getKursgroesse();
				Schueler[] schuelerliste = kurs[i].getSchuelerliste();
				for(int b = 0; b<schuelerliste.length; b++) {
					Kurs zweitwunsch = schuelerliste[b].getZweitwunsch();
					if(zweitwunsch.getKursgroesse() > zweitwunsch.getTatsaechlicheKursgroesse()) {
						// Schueler in Kurs reinschieben
					}
				}
				
			}
		}
	}

}