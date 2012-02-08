package informations;

import java.util.Calendar;

import misc.Print;

public class Wahl {
	private Schueler[] schuelerList;
	private Lehrer[] lehrerList;
	private Kurs[] kursListe = new Kurs[0];
	private Calendar date;
	private boolean running = false;
	public boolean ausgewertet = false;
	//private String endDate;
	/* - Schüler Liste und Lehrer Liste sollten im Konstuktor angegeben werden
	 * 		- späteres verändern nicht möglich
	 * - Variable mit Enddatum  
	 * 
	 */
	
	/**
	 * 
	 * @param date Das End-Datum
	 * @param s Die Schüler Liste
	 * @param l Die Lehrer Liste
	 * @throws Exception Das End-Datum liegt in der Vergangenheit
	 */
	public Wahl(Calendar date, Schueler[] s, Lehrer[] l) throws Exception{
		setDate(date);
		setSchuelerListe(s);
		setLehrerListe(l);
	}
	
	/**
	 * @return the schueler
	 */
	public Schueler[] getSchuelerList() {
		return schuelerList;
	}
	
	private void setSchuelerListe(Schueler[] s){
		schuelerList = s;
	}
	
	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}
	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	/**
	 * @return the lehrerList
	 */
	public Lehrer[] getLehrerList() {
		return lehrerList;
	}
	
	private void setLehrerListe(Lehrer[] lehrer){
		lehrerList = lehrer;
	}
	
	/**
	 * @return the kursListe
	 */
	public Kurs[] getKursListe() {
		return kursListe;
	}
	
	public void addKurs(Kurs kurs){
		if (kurs == null){
			Print.deb("Kurs == null in Wahl.addKurs()");
			return;
		}
		Kurs[] neueKursliste = new Kurs[kursListe.length + 1];
		for(int i = 0; i<kursListe.length;i++){
			neueKursliste[i] = kursListe[i];
		}
		neueKursliste[kursListe.length] = kurs;
		kursListe = neueKursliste;
	}
	
	public void delKurs(Kurs kurs){
		if (kurs == null){
			Print.deb("Kurs == null in Wahl.delKurs()");
			return;
		}
		if (kursListe.length == 0){
			Print.deb("Es kann kein Kurs gelöscht werden, da keine Kurse existieren!");
			return;
		}
		boolean found = false;
		for(int i = 0; i<kursListe.length;i++){
			if (kursListe[i].equals(kurs)){
				found = true;
			}
		}
		if (!found) {
			Print.deb("Kurs zum löschen konnte nicht gefunden werden!");
			return;
		}
		Kurs[] neueKursliste = new Kurs[kursListe.length - 1];
		int index = 0;
		for(int i = 0; i<kursListe.length;i++){
			if (!kursListe[i].equals(kurs)){
				neueKursliste[index] = kursListe[i];
				index++;
			}
		}
		kursListe = neueKursliste;
	}

	/**
	 * @return the date
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 * @throws Exception 
	 */
	private void setDate(Calendar date) throws Exception {
		if (date.before(Calendar.getInstance())){
			throw new Exception("Date is in the past!");
		}
		this.date = date;
	}
}
