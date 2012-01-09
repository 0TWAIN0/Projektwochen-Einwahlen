package informations;

import misc.Misc;

public class Schueler extends Jahrgang {
	
	public static final int SCHUELER = 1;
	
	private String passwort;
	private String name;
	public boolean online = false;
	private String sessionkey = null;

	private Kurs erstwunsch;
	private Kurs zweitwunsch;
	private Kurs drittwunsch;

	public Schueler(String name, String passwort, int jahrgang,
			String schulzweig) {
		this.setPasswort(passwort);
		this.setName(name);
		super.setJahrgang(jahrgang);
		super.setSchulzweig(schulzweig);
	}

	/**
	 * @return Das Passwort.
	 */
	public String getPasswort() {
		return passwort;
	}

	/**
	 * @param passwort
	 *            Das zu festzulegende Passwort.
	 */
	private void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	/**
	 * @return Der Name (Indentifikationsnummer)
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            Der festzulegende Name (Indentifikationsnummer)
	 */
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Der Erstwunsch
	 */
	public Kurs getErstwunsch() {
		return erstwunsch;
	}

	/**
	 * @param erstwunsch
	 *            Der festzulegende Erstwunsch.
	 */
	public void setErstwunsch(Kurs erstwunsch) {
		this.erstwunsch = erstwunsch;
	}

	/**
	 * @return Der Zweitwunsch
	 */
	public Kurs getZweitwunsch() {
		return zweitwunsch;
	}

	/**
	 * @param zweitwunsch
	 *            Der festzulegende Zweitwunsch
	 */
	public void setZweitwunsch(Kurs zweitwunsch) {
		this.zweitwunsch = zweitwunsch;
	}

	/**
	 * @return Der Drittwunsch
	 */
	public Kurs getDrittwunsch() {
		return drittwunsch;
	}

	/**
	 * @param drittwunsch
	 *            Der festzulegende Drittwunsch.
	 */
	public void setDrittwunsch(Kurs drittwunsch) {
		this.drittwunsch = drittwunsch;
	}

	/**
	 * @return the sessionkey
	 */
	public String getSessionkey() {
		return sessionkey;
	}

	/**
	 * @param sessionkey
	 *            the sessionkey to set
	 */
	public void setSessionkey() {
		String sessionkey = null;
		boolean equal = true;
		Schueler[] schueler = General.wahl.getSchuelerList();
		
		while (equal) {
			sessionkey = Misc.gen(10);
			equal = false;
			for (int i = 0; i < schueler.length; i++) {
				if (sessionkey.equals(schueler[i].getSessionkey())) {
					equal = true;
					break;
				}
			}
		}

		this.sessionkey = sessionkey;
	}

	public boolean equals(Schueler schueler){
		boolean pass = schueler.passwort.equals(this.passwort);
		boolean name = schueler.name.equals(this.name);
		if (pass && name){
			return true;
		}
		return false;
		
	}
	
}
