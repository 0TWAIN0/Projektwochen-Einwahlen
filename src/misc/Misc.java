package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class Misc {

	public static String gen(int lenght) {
		//TODO Buchstaben mit integrieren
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
	
	// Quelle http://www.w3.org/
	public static String unescape(String s) 
	{
		s = s.replaceAll("%E4", "ae");
		s = s.replaceAll("%F6", "oe");
		s = s.replaceAll("%FC", "ue");
		s = s.replaceAll("%C4","Ae");
		s = s.replaceAll("%D6", "Oe");
		s = s.replaceAll("%DC", "Ue");
		s = s.replaceAll("%DF", "ss");
		
		Print.deb(s);
	    StringBuffer sbuf = new StringBuffer();
	    int l  = s.length() ;
	    int ch = -1 ;
	    int b, sumb = 0;

	     for (int i = 0, more = -1; i < l; i++) 
	    {

	        // Get next byte b from URL segment s 
	        switch (ch = s.charAt(i)) 
	        {
	            case '%':
	                ch = s.charAt (++i);
	                int hb = (Character.isDigit ((char)ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF ;
	                ch = s.charAt(++i);
	                int lb = (Character.isDigit ((char)ch) ? ch - '0' : 10 + Character.toLowerCase ((char) ch)-'a') & 0xF;
	                b = (hb << 4) | lb;
	                break ;
	            case '+':
	                  b = ' ';
	                  break;
	            default:
	              b = ch ;
	        }

	        // Decode byte b as UTF-8, sumb collects incomplete chars
	        if ((b & 0xc0) == 0x80) // 10xxxxxx (continuation byte) 
	        {			
	            sumb = (sumb << 6) | (b & 0x3f);	// Add 6 bits to sumb
	            if(--more == 0) sbuf.append((char) sumb); // Add char to sbuf
	        } 
	        else if((b & 0x80) == 0x00) // 0xxxxxxx (yields 7 bits) 
	        {		
	            sbuf.append((char) b) ;			// Store in sbuf
	        } 
	        else if((b & 0xe0) == 0xc0)    // 110xxxxx (yields 5 bits) 
	        {		
	            sumb = b & 0x1f;
	            more = 1;				// Expect 1 more byte
	        } 
	        else if((b & 0xf0) == 0xe0) // 1110xxxx (yields 4 bits) 
	        {		
	            sumb = b & 0x0f;
	            more = 2;				// Expect 2 more bytes
	        } 
	        else if((b & 0xf8) == 0xf0) // 11110xxx (yields 3 bits) 
	        {		
	            sumb = b & 0x07;
	            more = 3;				// Expect 3 more bytes
	        } 
	        else if((b & 0xfc) == 0xf8)    // 111110xx (yields 2 bits) 
	        {		
	            sumb = b & 0x03;
	            more = 4;				// Expect 4 more bytes
	        } 
	        else // 1111110x (yields 1 bit)
	        {	
	            sumb = b & 0x01;
	            more = 5;				// Expect 5 more bytes
	        }
	        // We don't test if the UTF-8 encoding is well-formed 
	    }

	    return sbuf.toString() ;
	}
	
	public static String[] stringValueOf(Object[] obj){
		String[] string = new String[obj.length];
		for (int i = 0; i < obj.length; i++){
			string[i] = String.valueOf(obj[i]);
		}
		return string;
	}
	
}
