package evaluation;

import misc.Misc;
import misc.Print;
import informations.Kurs;
import informations.Schueler;

public class Auswertung {

	public int[] auswerten(Schueler[] schueler, Kurs[] kurs) {

		// Schueler den Erstwuenschen zuteilen
		Print.deb("Verschiebe Schüler zum Erstwunsch");
		for (int i = 0; i < schueler.length; i++) {
			Kurs erstwunsch = schueler[i].getErstwunsch();
			for (int p = 0; p < kurs.length; p++) {
				if (kurs[p].getName().equals(erstwunsch.getName())) {
					kurs[p].addSchueler(schueler[i], 0);
					break;
				}
			}
		}

		// Kurze Kurs uebersicht
//		for (int k = 0; k < kurs.length; k++) {
//			String teilies = "";
//			for (int s = 0; s < kurs[k].getSchuelerliste().length; s++) {
//				teilies += kurs[k].getSchuelerliste()[s].getName() + "; ";
//			}
//			Print.msg("KursName: " + kurs[k].getName() + " Größe: "
//					+ kurs[k].getTatsaechlicheKursgroesse() + " Schüler: "
//					+ teilies);
//		}

		// Ueberpruefung auf Ueberfullung der Kurse && Verschiebung moeglicher
		// Schueler in 2.Wunsch
		for (int i = 0; i < kurs.length; i++) {
			if (kurs[i].getKursgroesse() < kurs[i]
					.getTatsaechlicheKursgroesse()) {
				int ueberfuellung = kurs[i].getTatsaechlicheKursgroesse()
						- kurs[i].getKursgroesse();
				Print.deb("Kurs Überfüllung 1. Grad! Anzahl: " + ueberfuellung
						+ " Name: " + kurs[i].getName());
				Schueler[] kursSchuelerliste = kurs[i].getSchuelerliste();
				for (int b = 0; b < kursSchuelerliste.length; b++) {
					Kurs zweitwunsch = kursSchuelerliste[b].getZweitwunsch();
					if (zweitwunsch.getKursgroesse() > zweitwunsch
							.getTatsaechlicheKursgroesse()) {
						Print.deb("Move! " + kurs[i].getName() + " > " + zweitwunsch.getName());
						zweitwunsch.addSchueler(kursSchuelerliste[b], 0);
						kurs[i].removeSchueler(kursSchuelerliste[b], 0);
					}
					if (kurs[i].getKursgroesse() >= kurs[i]
							.getTatsaechlicheKursgroesse()) {
						Print.deb("Überfüllung behoben! Kurs: " + kurs[i].getName());
						break;
					}
				}
				if (kurs[i].getKursgroesse() < kurs[i]
						.getTatsaechlicheKursgroesse()) {
					ueberfuellung = kurs[i].getTatsaechlicheKursgroesse()
							- kurs[i].getKursgroesse();
					Print.deb("Überfüllung noch nicht behoben! Kurs: " + kurs[i].getName() + " Anzahl: " + ueberfuellung);
				}
				Print.debtab("");
			}
		}

		// Zuteilung von Drittwünschen
		for (int i = 0; i < kurs.length; i++) {
			if (kurs[i].getKursgroesse() < kurs[i]
					.getTatsaechlicheKursgroesse()) {
				int ueberfuellung = kurs[i].getTatsaechlicheKursgroesse()
						- kurs[i].getKursgroesse();
				Print.deb("Kurs Überfüllung 2. Grad! Anzahl: " + ueberfuellung
						+ " Name: " + kurs[i].getName());
				for (int b = 1; b <= ueberfuellung; b++) {
					boolean succ = false;
					int versuche = 0;
					while(!succ){
						versuche++;
						Schueler[] kursSchuelerliste = kurs[i].getSchuelerliste();
						if (versuche > kursSchuelerliste.length){
							break;
						}
						int rand = Misc.gen(0, kursSchuelerliste.length - 1);
						Kurs drittwunsch = kursSchuelerliste[rand].getDrittwunsch();
						if (drittwunsch.equals(kurs[i])){
							continue;
						}
						if (!(drittwunsch.getKursgroesse() <= drittwunsch
								.getTatsaechlicheKursgroesse())) { //Drittwunsch ist nicht ueberfuellt
							
							Print.deb("Drittwunsch NICHT Überfüllt!");
							Print.deb("Move! " + kurs[i].getName() + " > " + drittwunsch.getName());
							
							drittwunsch.addSchueler(kursSchuelerliste[rand], 0);
							kurs[i].removeSchueler(kursSchuelerliste[rand], 0);
							succ = true;
							break;
						}else{ //Drittwunsch ist ueberfuellt
							Print.deb("Drittwunsch Überfüllt!");
							Schueler[] drittwSchuelerliste = drittwunsch.getSchuelerliste();
							for (int s = 0; s < drittwSchuelerliste.length; s++) {
								//Jemand der diesen Kurs als Erstwunsch hat und drinne ist
								if(drittwSchuelerliste[s].getErstwunsch().equals(drittwunsch)){ 
									//ist ein anderer Wunsch dieser Person noch frei?
									if (drittwSchuelerliste[s].getZweitwunsch().getTatsaechlicheKursgroesse() < drittwSchuelerliste[s].getZweitwunsch().getKursgroesse()){
										
										Print.deb("Schaffe Platz! V1");
										Print.deb("Move! " + kurs[i].getName() + " > " + drittwunsch.getName());
										Print.deb("Move! " + drittwunsch.getName() + " > " + drittwSchuelerliste[s].getZweitwunsch().getName());
										
										drittwunsch.addSchueler(kursSchuelerliste[rand], 0);
										kurs[i].removeSchueler(kursSchuelerliste[rand], 0);
										
										drittwSchuelerliste[s].getZweitwunsch().addSchueler(drittwSchuelerliste[s], 0);
										drittwunsch.removeSchueler(drittwSchuelerliste[s], 0);
										succ = true;
										break;
									}else if(drittwSchuelerliste[s].getDrittwunsch().getTatsaechlicheKursgroesse() < drittwSchuelerliste[s].getDrittwunsch().getKursgroesse()){
										
										Print.deb("Schaffe Platz! V2");
										Print.deb("Move! " + kurs[i].getName() + " > " + drittwunsch.getName());
										Print.deb("Move! " + drittwunsch.getName() + " > " + drittwSchuelerliste[s].getDrittwunsch().getName());
										Print.deb("Schüler Info: 1.: " + kursSchuelerliste[rand].getErstwunsch().getName() + " 2.: " + kursSchuelerliste[rand].getZweitwunsch().getName() + " 3.: " + kursSchuelerliste[rand].getDrittwunsch().getName());
										
										drittwunsch.addSchueler(kursSchuelerliste[rand], 0);
										kurs[i].removeSchueler(kursSchuelerliste[rand], 0);
										
										drittwSchuelerliste[s].getDrittwunsch().addSchueler(drittwSchuelerliste[s], 0);
										drittwunsch.removeSchueler(drittwSchuelerliste[s], 0);
										succ = true;
										break;
									}
									
								//Jemand der diesen Kurs als Zweitwunsch hat und drinne ist
								}else if(drittwSchuelerliste[s].getZweitwunsch().equals(drittwunsch)){
									if(drittwSchuelerliste[s].getDrittwunsch().getTatsaechlicheKursgroesse() < drittwSchuelerliste[s].getDrittwunsch().getKursgroesse()){
										
										Print.deb("Schaffe Platz! V3");
										Print.deb("Move! " + kurs[i].getName() + " > " + drittwunsch.getName());
										Print.deb("Move! " + drittwunsch.getName() + " > " + drittwSchuelerliste[s].getDrittwunsch().getName());
										
										drittwunsch.addSchueler(kursSchuelerliste[rand], 0);
										kurs[i].removeSchueler(kursSchuelerliste[rand], 0);
										
										drittwSchuelerliste[s].getDrittwunsch().addSchueler(drittwSchuelerliste[s], 0);
										drittwunsch.removeSchueler(drittwSchuelerliste[s], 0);
										succ = true;
										break;
									}
									
								}
							}
						}
					}
					if (succ){
						Print.deb("Schüler erfolgreich verschoben!");
					}
				}
				if (kurs[i].getKursgroesse() >= kurs[i]
						.getTatsaechlicheKursgroesse()) {
							
					Print.deb("Überfüllung erfolgreich behoben!");
				}else if(kurs[i].getKursgroesse() >= kurs[i]
						.getTatsaechlicheKursgroesse()){
					ueberfuellung = kurs[i].getTatsaechlicheKursgroesse()- kurs[i].getKursgroesse();
					Print.deb("Überfüllung konnte nicht behoben werden! Kurs: " + kurs[i].getName() + " Anzahl: " + ueberfuellung);
				}
				Print.debtab("");
			}
		}

		
		
		// Statistik
		int verteilen = 0;
		int ueberf = 0;
		int erstwunsch = 0;
		int zweitwunsch = 0;
		int drittwunsch = 0;
		
		//Untersuchung auf Ueberfuellung
		for (int i = 0; i < kurs.length; i++) {
			if (kurs[i].getKursgroesse() < kurs[i]
					.getTatsaechlicheKursgroesse()) {
				int ueberfuellung = kurs[i].getTatsaechlicheKursgroesse()
						- kurs[i].getKursgroesse();
				verteilen += ueberfuellung;
				ueberf++;
			}
		}
		
		//Untersuchung der Wunscherfuellungen
		for (int i = 0; i < kurs.length; i++) {
			Schueler[] kursSchuelerliste = kurs[i].getSchuelerliste();
			for (int b = 0; b < kursSchuelerliste.length; b++) {
				if (kurs[i].equals(kursSchuelerliste[b].getErstwunsch())) {
					erstwunsch++;
				} else if (kurs[i]
						.equals(kursSchuelerliste[b].getZweitwunsch())) {
					zweitwunsch++;
				} else if (kurs[i]
						.equals(kursSchuelerliste[b].getDrittwunsch())) {
					drittwunsch++;
				}
			}
		}
		
		Print.deb(ueberf + " Kurse sind überfüllt");
		Print.deb("Es gibt " + erstwunsch + " Erstwünsche, " + zweitwunsch
				+ " Zweitwünsche und " + drittwunsch + " Drittwünsche!");
		Print.deb("Es müssen noch " + verteilen + " Schüler verteilt werden!");
		
		int[] feedback = new int[7];
		feedback[0] = ueberf;
		feedback[1] = erstwunsch;
		feedback[2] = zweitwunsch;
		feedback[3] = drittwunsch;
		feedback[4] = verteilen;				
		
		return feedback;
	}

//	private boolean checkAufUeberfuellung(Kurs[] kurs) {
//		for (int i = 0; i < kurs.length; i++) {
//			if (kurs[i].getKursgroesse() < kurs[i]
//					.getTatsaechlicheKursgroesse()) {
//				return true;
//			}
//		}
//		return false;
//	}

}