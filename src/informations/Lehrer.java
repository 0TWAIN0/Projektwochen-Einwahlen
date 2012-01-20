package informations;

public class Lehrer extends User{
	public static final int LEHRER = 0;
	
	public Lehrer(String name, String passwort) {
		setPasswort(passwort);
		setName(name);
	}
}
