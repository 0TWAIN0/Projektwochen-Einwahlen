package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class Misc {
	public static String gen(int lenght) {
		String key = "";

		Random gen = new Random();
		for (int i = 1; i <= lenght; i++) {
			key = key + String.valueOf(gen.nextInt(10));
		}
		return key;
	}
	public static int gen(int min, int max) {
		if (min >= max){
			return 0;
		}
		Random gen = new Random();
		int key = gen.nextInt(max - min);
		key += min;
		return key;
	}
	
	public static String read(String path) throws FileNotFoundException{
		String inhalt = "";
		Scanner s = new Scanner(new File(path));
		while(true){
			try{
				inhalt = inhalt + s.nextLine();
			}catch(NoSuchElementException e){
				break;
			}
		}
		s.close();
		return inhalt;
	}
	public static String read(File f) throws FileNotFoundException{
		String inhalt = "";
		Scanner s = new Scanner(f);
		while(true){
			try{
				inhalt = inhalt + s.nextLine();
			}catch(NoSuchElementException e){
				break;
			}
		}
		s.close();
		return inhalt;
	}
	public static void write(File f, String text) throws FileNotFoundException{
		PrintWriter write = new PrintWriter(f);
		write.println(text);
		write.flush();
		write.close();
	}
}
