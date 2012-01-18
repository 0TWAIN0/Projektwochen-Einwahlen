package informations;

import misc.Misc;

public class Lehrer {
	public static final int LEHRER = 0;

	private String passwort;
	private String name;
	public boolean online = false;
	private String sessionkey = null;

	/**
	 * @return the passwort
	 */
	public String getPasswort() {
		return passwort;
	}

	/**
	 * @param passwort
	 *            the passwort to set
	 */
	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the sessionkey
	 */
	public String getSessionkey() {
		return sessionkey;
	}

	public void setSessionkey() {
		String sessionkey = null;
		boolean equal = true;
		Lehrer[] lehrer = General.lehrer;
		Schueler[] schueler = General.wahl.getSchuelerList();

		while (equal) {
			sessionkey = Misc.gen(10);
			equal = false;
			for (int i = 0; i < lehrer.length; i++) {
				if (sessionkey.equals(lehrer[i].getSessionkey())) {
					equal = true;
					break;
				}
			}
			for (int i = 0; i < schueler.length; i++) {
				if (sessionkey.equals(schueler[i].getSessionkey())) {
					equal = true;
					break;
				}
			}
		}
		this.sessionkey = sessionkey;
	}
}
