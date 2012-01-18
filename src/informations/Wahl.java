package informations;

public class Wahl {
	private Schueler[] schuelerList;
	private Lehrer[] lehrerList;
	private Kurs[] kursListe = new Kurs[0];
	/* - Schüler Liste und Lehrer Liste sollten im Konstuktor angegeben werden
	 * 		- späteres verändern nicht möglich
	 * - Variable mit Enddatum  
	 * 
	 */
	private boolean running = false;
	//private String endDate;
	/**
	 * @return the schueler
	 */
	public Schueler[] getSchuelerList() {
		return schuelerList;
	}
	/**
	 * @param schuelerList the schueler to set
	 */
	public void setSchueler(Schueler[] schuelerList) {
		this.schuelerList = schuelerList;
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
	/**
	 * @param lehrerList the lehrerList to set
	 */
	public void setLehrerList(Lehrer[] lehrerList) {
		this.lehrerList = lehrerList;
	}
	/**
	 * @return the kursListe
	 */
	public Kurs[] getKursListe() {
		return kursListe;
	}
	/**
	 * @param kursListe the kursListe to set
	 */
	public void setKursListe(Kurs[] kursListe) {
		this.kursListe = kursListe;
	}
	
	public void addKurs(Kurs kurs){
		
	}
	
	public void delKurs(Kurs kurs){
		
	}
}
