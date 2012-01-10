package misc;

public class Array {
	public static void show(Object[] o){
		String out = "";
		for (int i = 0; i<o.length;i++){
			out += "["+i+"] "+String.valueOf(o[i]) + "; ";
		}
		Print.deb(out);
	}
	
	public static void show(int[] o){
		String out = "";
		for (int i = 0; i<o.length;i++){
			out += "["+i+"] "+String.valueOf(o[i]) + "; ";
		}
		Print.deb(out);
	}
}
