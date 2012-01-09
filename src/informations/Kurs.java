package informations;

import misc.Print;

public class Kurs {
	private String name;
	private String beschreibung;
	private int kursgroesse;
	private int jahrgangsberechtigungMin;
	private int jahrgangsberechtigungMax;
	private int tatsaechlicheKursgroesse;
	private Schueler[] schuelerliste = new Schueler[0];
	
	public Kurs(String name, String beschreibung, int kursgroesse, int jahrgangsberechtigungMin, int jahrgangsberechtigungMax){
		setName(name);
		setBeschreibung(beschreibung);
		setKursgroesse(kursgroesse);
		setJahrgangsberechtigungMin(jahrgangsberechtigungMin);
		setJahrgangsberechtigungMax(jahrgangsberechtigungMax);
	}
	
	/**
	 * @return Der Kursname
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name Der festzulegende Kursame.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Die Beschreibung
	 */
	public String getBeschreibung() {
		return beschreibung;
	}
	/**
	 * @param beschreibung Die festzulegende Beschreibung
	 */
	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}
	/**
	 * @return Die Kursgroeße
	 */
	public int getKursgroesse() {
		return kursgroesse;
	}
	/**
	 * @param kursgroeße Die festzulegende Kursgroeße.
	 */
	public void setKursgroesse(int kursgroesse) {
		this.kursgroesse = kursgroesse;
	}
	/**
	 * @return Die minimale Jahrgangsberechtigung
	 */
	public int getJahrgangsberechtigungMin() {
		return jahrgangsberechtigungMin;
	}
	/**
	 * @param jahrgangsberechtigungMin Die festzulegende minimale Jahrgangsberechtigung
	 */
	public void setJahrgangsberechtigungMin(int jahrgangsberechtigungMin) {
		this.jahrgangsberechtigungMin = jahrgangsberechtigungMin;
	}
	/**
	 * @return Die maximale Jahrgangsberechtigung
	 */
	public int getJahrgangsberechtigungMax() {
		return jahrgangsberechtigungMax;
	}
	/**
	 * @param jahrgangsberechtigungMax Die festzulegende maximale Jahrgangsberechtigung
	 */
	public void setJahrgangsberechtigungMax(int jahrgangsberechtigungMax) {
		this.jahrgangsberechtigungMax = jahrgangsberechtigungMax;
	}
	/**
	 * @return the tatsaechlicheKursgroesse
	 */
	public int getTatsaechlicheKursgroesse() {
		return tatsaechlicheKursgroesse;
	}
	/**
	 * @param tatsaechlicheKursgroesse the tatsaechlicheKursgroesse to set
	 */
	public void setTatsaechlicheKursgroesse(int tatsaechlicheKursgroesse) {
		this.tatsaechlicheKursgroesse = tatsaechlicheKursgroesse;
	}
	/**
	 * @return the schuelerliste
	 */
	public Schueler[] getSchuelerliste() {
		return schuelerliste;
	}
	/**
	 * @param schuelerliste the schuelerliste to set
	 */
	public void addSchueler(Schueler schueler) {
		if (schueler == null){
			Print.deb("Schueler == null in Kurs.addSchueler()");
			return;
		}
		Schueler[] neueSchuelerliste = new Schueler[schuelerliste.length + 1];
		for(int i = 0; i<schuelerliste.length;i++){
			neueSchuelerliste[i] = schuelerliste[i];
		}
		neueSchuelerliste[schuelerliste.length] = schueler;
		this.tatsaechlicheKursgroesse++;
		schuelerliste = neueSchuelerliste;
	}
	
	public void removeSchueler(Schueler schueler) {
		Schueler[] neueSchuelerliste = new Schueler[schuelerliste.length - 1];
		int index = 0;
		for(int i = 0; i<schuelerliste.length;i++){
			if (!schuelerliste[i].equals(schueler)){
				neueSchuelerliste[index] = schuelerliste[i];
				index++;
			}
		}
		this.tatsaechlicheKursgroesse--;
		schuelerliste = neueSchuelerliste;
	}
	

}
