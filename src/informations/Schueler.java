package informations;

public class Schueler extends User {

	public static final int SCHUELER = 1;
	
	private int jahrgang;
	private String schulzweig = "";

	private Kurs erstwunsch;
	private Kurs zweitwunsch;
	private Kurs drittwunsch;

	public Schueler(String name, String passwort, int jahrgang,
			String schulzweig) {
		setPasswort(passwort);
		setName(name);
		setJahrgang(jahrgang);
		setSchulzweig(schulzweig);
	}
	
	/**
	 * @return Den Jahrgang
	 */
	public int getJahrgang() {
		return jahrgang;
	}

	/**
	 * @param jahrgang Den belegten Jahrgang
	 */
	public void setJahrgang(int jahrgang) {
		this.jahrgang = jahrgang;
	}
	
	/**
	 * @return den Schulzweig
	 */
	public String getSchulzweig() {
		return schulzweig;
	}

	/**
	 * @param schulzweig Der zugehörige Schulzweig (Gymnaisum/Realschule/Hauptschule)
	 */
	public void setSchulzweig(String schulzweig) {
		this.schulzweig = schulzweig;
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
}
