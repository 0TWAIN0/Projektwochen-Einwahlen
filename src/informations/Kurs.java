package informations;

import misc.Array;
import misc.Print;

public class Kurs {
	
	private static Kurs[] kursListe = new Kurs[0];
	
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
		kursListe = Kurs.valueOf(Array.addField(kursListe));
		kursListe[kursListe.length-1] = this;
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
	 * @param kursgroesse Die festzulegende Kursgroesse.
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
	 * @param schueler Der Schueler der hinzugefuegt werden soll
	 */
	public void addSchueler(Schueler schueler, int flag) {
		if (schueler == null){
			Print.deb("Schueler == null in Kurs.addSchueler()");
			return;
		}
		Schueler[] neueSchuelerliste = new Schueler[schuelerliste.length + 1];
		
		if (flag == 1){
			Print.deb("add: " + schueler);
			Array.show(schuelerliste);
		}
		for(int i = 0; i<schuelerliste.length;i++){
			neueSchuelerliste[i] = schuelerliste[i];
		}
		neueSchuelerliste[schuelerliste.length] = schueler;
		this.tatsaechlicheKursgroesse++;
		schuelerliste = neueSchuelerliste;
	}
	
	public void removeSchueler(Schueler schueler, int flag) {
		if (schueler == null){
			Print.deb("Schueler == null in Kurs.removeSchueler()");
			return;
		}
		if (schuelerliste.length == 0){
			Print.deb("Schueler kann nicht gelöscht werden, da keine Schüler existieren!");
			return;
		}
		Schueler[] neueSchuelerliste = new Schueler[schuelerliste.length - 1];
		int index = 0;
		if (flag == 1){
			Print.deb("remove: " + schueler);
			Array.show(schuelerliste);
		}
		boolean found = false;
		for(int i = 0; i<schuelerliste.length;i++){
			if (schuelerliste[i].equals(schueler)){
				found = true;
			}
		}
		if (!found) {
			Print.deb("Schueler zum löschen konnte nicht gefunden werden!");
			return;
		}
		
		for(int i = 0; i<schuelerliste.length;i++){
			if (!schuelerliste[i].equals(schueler)){
				neueSchuelerliste[index] = schuelerliste[i];
				index++;
			}
		}
		this.tatsaechlicheKursgroesse--;
		schuelerliste = neueSchuelerliste;
	}
	
	public boolean equals(Object obj){
		if (!(obj instanceof Kurs)){
			return false;
		}
		Kurs k = (Kurs)obj;
		boolean name = this.name.equals(k.name);
		boolean beschreibung = this.beschreibung.equals(k.beschreibung);
		boolean kursgroesse = this.kursgroesse == k.kursgroesse;
		if (name && beschreibung && kursgroesse){
			return true;
		}
		
		return false;
	}
	
	public static Kurs getKursByName(String name){
		Kurs kurs = null;
		for (int k = 0; k < kursListe.length; k++){
			if (kursListe[k].getName().equals(name)){
				kurs = kursListe[k];
				break;
			}
		}
		return kurs;
	}
	
	public static Kurs valueOf(Object obj) {
		Kurs kurs = (Kurs)obj;
		return kurs;		
	}
	public static Kurs[] valueOf(Object[] obj) {
		Kurs[] kursList = new Kurs[obj.length];
		for (int i = 0 ; i < obj.length; i++){
			kursList[i] = Kurs.valueOf(obj[i]);
		}
		return kursList;
	}

}
