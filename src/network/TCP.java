package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Eine Klasse zum Empfangen und Senden von Nachrichten über ein Netzwerk.
 * 
 * @author Jakob Lochner
 *
 */
public class TCP {
	
	/**
	 * Empfägt EINE Zeile, die von einem Socket gesendet wurde.
	 * 
	 * @param client Ein verbundener Socket. 
	 * @return Die empfangene Zeile.
	 * @throws IOException Wenn der Input Stream vom Socket nicht geöffnet werden konnte.
	 * @throws NoSuchElementException Wenn der Socket nichts sendet.
	 */

	public static String recv(Socket client) throws IOException,
			NoSuchElementException {
		
		Scanner in = new Scanner(client.getInputStream());
		// Daten empfangen
		String empfang = "";
		empfang = in.nextLine();
		return empfang;
	}

	/**
	 * Sendet Informationen an einen verbundenen Socket im Netzwerk.
	 * @param client Verbundener Socket.
	 * @param data Zu sendende Informationen.
	 * @throws IOException Fehler beim öffnen des Outout Streams.
	 */
	public static void send(Socket client, String data) throws IOException {
		PrintWriter out = new PrintWriter(client.getOutputStream(), true);
		// Senden von data
		out.println(data);
	}
	
	public static void send(Socket client, int data) throws IOException {
		PrintWriter out = new PrintWriter(client.getOutputStream(), true);
		// Senden von data
		out.println(data);
	}
	
	public static void send(Socket client, byte[] data) throws IOException {
		PrintWriter out = new PrintWriter(client.getOutputStream());
		// Senden von data
		for (int i = 0; i<data.length;i++){
			out.print(data[i]);
		}
		out.flush();
		
	}

}
