
import informations.General;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import misc.Config;
import misc.Print;
import network.HTTP;
import network.HandleConnections;

/**
 * Hauptklasse des Webservers.
 *
 */
public class Webserver {
	public static HTTP http = null;
	public static final int CLIENT_TIMEOUT = 5000;
	public static final int SERVER_TIMEOUT = 5000;
	public static final File KOFIGURATIONS_DATEI = new File("config");
	public static final File ALLOWED_FILES_LIST = new File("allowedfiles");

	/**
	 * MAIN-Methode! Startet Server.
	 * 
	 */
	public static void main(String[] args) {
		Print.deb("MAIN THREAD: " + Thread.currentThread());
		// Config Einlesen
		try {
			new Config(KOFIGURATIONS_DATEI, ALLOWED_FILES_LIST);
		} catch (FileNotFoundException e1) {
			Print.err("Fehler beim Lesen der Konfigurationsdatei und der AllowedFiles Liste!");
		}
		
		new General();

		// Der Port an dem der Server lauschen soll
		final int port = Config.getPort();

		// Thread Pool
		ExecutorService executor = Executors.newCachedThreadPool();
		
		// Initialisierung des Servers
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			//server.setSoTimeout(SERVER_TIMEOUT);
			Print.msg("Server an Port " + port + " initialisiert!");
		} catch (BindException e) {
			Print.err("Der Server konne nicht am Port '"
							+ port
							+ "' erstellt werden!");
			Print.tab("Bitte überprüfen Sie ob Sie die nötigen Rechte besitzen,");
			Print.tab("bzw. ob nich eine andere Anwendung an diesem Port läuft!");
			System.exit(0);
		} catch (IOException e) {
			Print.err("I/O Error beim erstellen des Servers!");
		}

		// Entgegennehmen von Verbindungen
		while (true) {
			Socket client = null;

			try {
				client = server.accept();
				if (client != null) {
					client.setSoTimeout(CLIENT_TIMEOUT);
					HandleConnections c = new HandleConnections(client);
					executor.execute(c);
				}
			} catch (SecurityException e) {
				Print.err("Das Annehmen von Verbindungen wurde eventuell durch einen Sicherheitsmanager verhindert!");
				System.exit(0);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}