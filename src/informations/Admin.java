package informations;

public class Admin extends User {
	private static final String name = "admin";
	private static final String pass = "123456";
	public static final int ADMIN = 2;
	
	public Admin(){
		super.setName(name);
		super.setPasswort(pass);
	}
}
