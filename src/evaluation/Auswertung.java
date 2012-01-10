package evaluation;

import misc.Misc;
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
				//Print.msg("Kurs Überfüllung 1. Grad! Anzahl: " + ueberfuellung
				//		+ " Name: " + kurs[i].getName());
				Schueler[] kursSchuelerliste = kurs[i].getSchuelerliste();
				for (int b = 0; b < kursSchuelerliste.length; b++) {
					Kurs zweitwunsch = kursSchuelerliste[b].getZweitwunsch();
					if (zweitwunsch.getKursgroesse() > zweitwunsch
							.getTatsaechlicheKursgroesse()) {
						zweitwunsch.addSchueler(kursSchuelerliste[b], 0);
						kurs[i].removeSchueler(kursSchuelerliste[b], 0);
					}
					if (kurs[i].getKursgroesse() >= kurs[i]
							.getTatsaechlicheKursgroesse()) {
						break;
					}
				}

			}
		}

		// // Ueberpruefung auf immer noch existierende Ueberfullung der Kurse
		// &&
		// // Verschiebung moeglicher Schueler in 2.Wunsch => neue
		// Ueberfuellungen
		// for (int i = 0; i < kurs.length; i++) {
		// if (kurs[i].getKursgroesse() < kurs[i]
		// .getTatsaechlicheKursgroesse()) {
		// int ueberfuellung = kurs[i].getTatsaechlicheKursgroesse()
		// - kurs[i].getKursgroesse();
		// Print.msg("Kurs Überfüllung 2. Grad! Anzahl: "
		// + ueberfuellung + " Name: " + kurs[i].getName());
		// Schueler[] kursSchuelerliste = kurs[i].getSchuelerliste();
		// for (int b = 0; b < kursSchuelerliste.length; b++) {
		// Kurs zweitwunsch = kursSchuelerliste[b].getZweitwunsch();
		// if (kursSchuelerliste[b].getErstwunsch() == kurs[i]) {
		// // Print.msg("Move "+loops+".2. Grad! " +
		// // kursSchuelerliste[b].getName() + " from " +
		// // kurs[i].getName() + " to " + zweitwunsch.getName());
		// zweitwunsch.addSchueler(kursSchuelerliste[b], 0);
		// kurs[i].removeSchueler(kursSchuelerliste[b], 0);
		// }
		// if (kurs[i].getKursgroesse() >= kurs[i]
		// .getTatsaechlicheKursgroesse()) {
		// break;
		// }
		// }
		//
		// }
		// }
		
		
		// Zuteilung von Drittwünschen
		for (int i = 0; i < kurs.length; i++) {
			if (kurs[i].getKursgroesse() < kurs[i]
					.getTatsaechlicheKursgroesse()) {
				int ueberfuellung = kurs[i].getTatsaechlicheKursgroesse()
						- kurs[i].getKursgroesse();
				//Print.msg("Kurs Überfüllung 2. Grad! Anzahl: " + ueberfuellung
				//		+ " Name: " + kurs[i].getName());
				for (int b = 1; b <= ueberfuellung; b++) {
					boolean succ = false;
					while(!succ){
						Schueler[] kursSchuelerliste = kurs[i].getSchuelerliste();
						int rand = Misc.gen(0, kursSchuelerliste.length - 1);
						Kurs drittwunsch = kursSchuelerliste[rand].getDrittwunsch();
						if (!(drittwunsch.getKursgroesse() <= drittwunsch
								.getTatsaechlicheKursgroesse())) { //Drittwunsch ist nicht ueberfuellt
							//Print.deb("Drittwunsch NICHT Überfüllt!");
							drittwunsch.addSchueler(kursSchuelerliste[rand], 0);
							kurs[i].removeSchueler(kursSchuelerliste[rand], 0);
							break;
						}else{ //Drittwunsch ist ueberfuellt
							//Print.deb("Drittwunsch Überfüllt!");
							Schueler[] drittwSchuelerliste = drittwunsch.getSchuelerliste();
							for (int s = 0; s < drittwSchuelerliste.length; s++) {
								//Jemand der diesen Kurs als Erstwunsch hat und drinne ist
								if(drittwSchuelerliste[s].getErstwunsch().equals(drittwunsch)){ 
									//ist ein anderer Wunsch dieser Person noch frei?
									if (drittwSchuelerliste[s].getZweitwunsch().getTatsaechlicheKursgroesse() < drittwSchuelerliste[s].getZweitwunsch().getKursgroesse()){
										drittwunsch.addSchueler(kursSchuelerliste[rand], 0);
										kurs[i].removeSchueler(kursSchuelerliste[rand], 0);
										
										drittwSchuelerliste[s].getZweitwunsch().addSchueler(drittwSchuelerliste[s], 0);
										drittwunsch.removeSchueler(drittwSchuelerliste[s], 0);
										succ = true;
										break;
									}else if(drittwSchuelerliste[s].getDrittwunsch().getTatsaechlicheKursgroesse() < drittwSchuelerliste[s].getDrittwunsch().getKursgroesse()){
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
				}
			}
		}

		
		
		// Statistik
		int verteilen = 0;
		int ueberf = 0;
		for (int i = 0; i < kurs.length; i++) {
			if (kurs[i].getKursgroesse() < kurs[i]
					.getTatsaechlicheKursgroesse()) {
				int ueberfuellung = kurs[i].getTatsaechlicheKursgroesse()
						- kurs[i].getKursgroesse();
				verteilen += ueberfuellung;
				ueberf++;
			}
		}
		Print.msg(ueberf + " Kurse sind überfüllt");

		int erstwunsch = 0;
		int zweitwunsch = 0;
		int drittwunsch = 0;
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
		Print.msg("Es gibt " + erstwunsch + " Erstwünsche, " + zweitwunsch
				+ " Zweitwünsche und " + drittwunsch + " Drittwünsche!");
		Print.msg("Es müssen noch " + verteilen + " Schüler verteilt werden!");
	}

	private boolean checkAufUeberfuellung(Kurs[] kurs) {
		for (int i = 0; i < kurs.length; i++) {
			if (kurs[i].getKursgroesse() < kurs[i]
					.getTatsaechlicheKursgroesse()) {
				return true;
			}
		}
		return false;
	}

}