package informations;

import java.util.Calendar;

public class Wahl {
	private Schueler[] schuelerList;
	private Lehrer[] lehrerList;
	private Kurs[] kursListe = new Kurs[0];
	private Calendar date;
	/* - Schüler Liste und Lehrer Liste sollten im Konstuktor angegeben werden
	 * 		- späteres verändern nicht möglich
	 * - Variable mit Enddatum  
	 * 
	 */
	
	public Wahl(Calendar date, Schueler[] s, Lehrer[] l) throws Exception{
		setDate(date);
		setSchuelerListe(s);
		setLehrerListe(l);
	}
	
	private boolean running = false;
	//private String endDate;
	/**
	 * @return the schueler
	 */
	public Schueler[] getSchuelerList() {
		return schuelerList;
	}
	
	public void setSchuelerListe(Schueler[] s){
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
	
	public void setLehrerListe(Lehrer[] lehrer){
		lehrerList = lehrer;
	}
	
	/**
	 * @return the kursListe
	 */
	public Kurs[] getKursListe() {
		return kursListe;
	}
	
	public void addKurs(Kurs kurs){
		
	}
	
	public void delKurs(Kurs kurs){
		
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
