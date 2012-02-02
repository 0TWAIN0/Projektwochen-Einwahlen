package misc;

public class Array {
	public static void show(Object[] o){
		String out = "";
		for (int i = 0; i<o.length;i++){
			out += "["+i+"] "+String.valueOf(o[i]) + "; ";
		}
		System.out.println(out);
	}
	
	public static void show(int[] o){
		String out = "[ARRAY] ";
		for (int i = 0; i<o.length;i++){
			out += "["+i+"] "+String.valueOf(o[i]) + "; ";
		}
		System.out.println(out);
	}
	
	public static Object[] addField(Object[] array){
		Object[] newArray = new Object[array.length+1];
		for (int i = 0; i < array.length; i++){
			newArray[i] = array[i];
		}
		return newArray;
	}
}
