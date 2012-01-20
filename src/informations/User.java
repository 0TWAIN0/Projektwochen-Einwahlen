package informations;

import misc.Misc;

public class User {
	
	private String sessionkey = null;
	private String passwort;
	private String name;
	public boolean online = false;
	
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
	protected void setPasswort(String passwort) {
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
	protected void setName(String name) {
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
		Lehrer[] lehrer = General.wahl.getLehrerList();
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
			if (sessionkey.equals(General.admin.getSessionkey())) {
				equal = true;
			}
		}
		this.sessionkey = sessionkey;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Schueler)){
			return false;
		}
		Schueler s = (Schueler) obj;
		boolean pass = s.getPasswort().equals(this.getPasswort());
		boolean name = s.getName().equals(this.getName());
		if (pass && name) {
			return true;
		}
		return false;

	}

}
