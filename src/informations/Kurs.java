package informations;

public class Kurs {
	private String name;
	private String beschreibung;
	private int kursgroesse;
	private int jahrgangsberechtigungMin;
	private int jahrgangsberechtigungMax;
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
	

}
