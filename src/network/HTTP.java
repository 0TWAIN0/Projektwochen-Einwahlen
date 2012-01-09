package network;

import informations.Lehrer;
import informations.Schueler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import misc.Config;
import misc.Print;

/**
 * Klasse zum reagieren auf GET-Anfragen und Fehler. Bezogen auf das Hypertext
 * Transfer Protocol.
 * 
 * @author Jakob Lochner
 * 
 */
public class HTTP {
	
	//Speicher Informationen
	private final String webroot;
	private final String errorPage;
	
	//HTTP Header
	public static final String HEADER_OK = "HTTP/1.1 200 OK\r\n";
	public static final String HEADER_FILE_NOT_FOUND = "HTTP/1.1 404 Not Found\r\n";
	public static final String HEADER_ACCESS_FORBIDDEN = "HTTP/1.1 403 Access Forbidden\r\n";
	public static final String HEADER_BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\n";
	public static final String HEADER_MOVED = "HTTP/1.1 302 Moved Temporarily";
	
	//ERROR Typen
	public static final int FILE_NOT_FOUND_ERROR = 1;
	public static final int SYNTAX_ERROR = 2;
	public static final int ACCESS_FORBIDDEN_ERROR = 3;

	//Befehle
	private static final String COMMAND_START_PAGE = "/";
	private static final String COMMAND_LOGIN = "/login";
	private static final String COMMAND_CREATE_KURS = "/create";
	private static final String COMMAND_VOTE = "/vote";


	/**
	 * Bearbeitet GET-Anfrage und Antwortet.
	 * 
	 * @param client
	 *            Socket der Angefragt hat und dem auch geantwortet werden soll.
	 * @param splitted
	 *            Bereits bei jedem Leerzeichen getrennte GET-Anfrage.
	 * @param thread
	 *            Zur identivizierung des laufenden Threads
	 */
	public void get(Socket client, String[] splitted, Thread thread) {
		
		// Identifizierung des übergebenen Befehls
		if (splitted[1].equals(COMMAND_START_PAGE)) {
			Print.deb(thread + " Anfrage: Startpage");
			String startPage = webroot + Config.START_PAGE;
			try {
				Command.allowedFileReqest(client, startPage, thread);
			} catch (FileNotFoundException e) {
				Print.err(thread + " Fehler beim Lesen der StartPage: "
						+ startPage);
				error(client, FILE_NOT_FOUND_ERROR, thread);
			}
		} else {
			String command;
			String[] arguments = null;

			// Untersuchung nach übergebenen Argumenten mit Hilfe eines '?'
			if (splitted[1].contains("?")) {

				String[] args = splitted[1].split("\\?");
				if (args.length > 2) {
					Print.deb(thread + "Syntax Error im übergebenen Befehl.");
					error(client, SYNTAX_ERROR, thread);
					return;
				}
				command = args[0];
				Print.deb(thread + " Command: " + command);

				// Untersuchung falls mehrere Argumente durch ein '&' getrennt
				// wurden
				if (args[1].contains("&")) {
					arguments = args[1].split("&");
				} else {
					arguments = new String[1];
					arguments[0] = args[1];
				}

				Print.deb(thread + " ArgsList:");
				for (int i = 0; i < arguments.length; i++) {
					Print.debtab(arguments[i]);
				}
			} else {
				command = splitted[1];
				Print.deb(thread + " Command: " + command);
			}

			// Reaktions auf Befehl
			if (command.equals(COMMAND_LOGIN)) {
				try {
					Command.login(client, arguments, thread);
				} catch (FileNotFoundException e) {
					Print.err(thread + " Fehler beim Lesen einer Datei");
					error(client, FILE_NOT_FOUND_ERROR, thread);
				}
			} else if(command.equals(COMMAND_CREATE_KURS)){
				try {
					Command.protectedFileReqest(client, Config.getWebroot() + Config.KURS_ERSTELLEN_PAGE, arguments, Lehrer.LEHRER, thread);
				} catch (SecurityException e) {
					Print.msg(thread + " Fehlgeschlagese Verifikation!");
					error(client, ACCESS_FORBIDDEN_ERROR, thread);
				} catch (FileNotFoundException e) {
					Print.err(thread + " Datei wurde nicht gefunden: " + Config.getWebroot() + Config.KURS_ERSTELLEN_PAGE);
					error(client, FILE_NOT_FOUND_ERROR, thread);
				}
			} else if(command.equals(COMMAND_VOTE)){
				try {
					Command.protectedFileReqest(client, Config.getWebroot() + Config.KURS_WAHL_PAGE, arguments, Schueler.SCHUELER, thread);
				} catch (SecurityException e) {
					Print.msg(thread + " Fehlgeschlagese Verifikation!");
					error(client, ACCESS_FORBIDDEN_ERROR, thread);
				} catch (FileNotFoundException e) {
					Print.err(thread + " Datei wurde nicht gefunden: " + Config.getWebroot() + Config.KURS_ERSTELLEN_PAGE);
					error(client, FILE_NOT_FOUND_ERROR, thread);
				}
			} else {
				command = webroot + command;
				boolean allowed =false;
				for (int i = 0; i < Config.getAllowedFiles().length;i++){
					if (command.equals(Config.getAllowedFiles()[i])){
						allowed = true;
						break;
					}
				}
				if (allowed){
					Print.deb(thread + " Allowed File Request: " + command);
					try {
						Command.allowedFileReqest(client, command, thread);
					} catch (FileNotFoundException e) {
						Print.err(thread + " Fehler beim Lesen der Datei: "
								+ command);
						error(client, FILE_NOT_FOUND_ERROR, thread);
					}
				}else{
					error(client, SYNTAX_ERROR, thread);
				}
			} 

		}

	}

	/**
	 * Fehler Behandlung bei Syntax Error der Anfrage oder falsch angefragte
	 * Datei.
	 * 
	 * @param client
	 *            client Socket der Angefragt hat und dem auch geantwortet
	 *            werden soll.
	 * @param flag
	 *            Muss 1 sein bei nicht gefundener Datei und 2 sein bei Syntax
	 *            Error
	 * @param thread
	 *            Zur identivizierung des laufenden Threads
	 * 
	 */
	public void error(Socket client, int flag, Thread thread) {

		if (flag == FILE_NOT_FOUND_ERROR) {
			
			File f;
			Scanner file;
			String data = "";
			
			synchronized (HandleConnections.LOCK) {
				f = new File(errorPage);

				// Datei im Scanner öffnen
				try {
					file = new Scanner(f);
				} catch (FileNotFoundException e1) {
					Print.err(thread
							+ " Die Error Seite konnte nicht gefunden werden!");
					return;
				}

				// Datei auslesen
				while (true) {
					try {
						data = data + file.nextLine();
					} catch (NoSuchElementException e) {
						break;
					}
				}

				file.close(); // Datei schließen
			}

			// Senden der Error Page
			Print.msg(thread + " Sending " + errorPage + " to "
					+ client.getInetAddress());
			try {
				TCP.send(client, HEADER_FILE_NOT_FOUND);
				TCP.send(client, data);
			} catch (IOException e) {
				Print.err(thread + " Senden an " + client.getInetAddress()
						+ " fehlgeschlagen!");
				e.printStackTrace();
			}
		} else if (flag == SYNTAX_ERROR) {
			// Senden der des Errors
			Print.msg(thread + " Sending Bad Request Error to "
					+ client.getInetAddress());
			try {
				TCP.send(client, HEADER_BAD_REQUEST);
				TCP.send(client, "<html><h2>400 - Bad Request</h2></html>");
			} catch (IOException e) {
				Print.err(thread + " Senden an " + client.getInetAddress()
						+ " fehlgeschlagen!");
				e.printStackTrace();
			}
		}else if (flag == ACCESS_FORBIDDEN_ERROR) {
			// Senden der des Errors
			Print.msg(thread + " Sending Access Forbidden Error to "
					+ client.getInetAddress());
			try {
				TCP.send(client, HEADER_BAD_REQUEST);
				TCP.send(client, "<html><h2>403 - Access Forbidden</h2></html>");
			} catch (IOException e) {
				Print.err(thread + " Senden an " + client.getInetAddress()
						+ " fehlgeschlagen!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initialisierung des Hypertext Transfer Protocol.
	 * 
	 * @param webroot
	 *            Pfad zu den Website-Dateien.
	 * @param errorPage
	 *            Pfad zur Fehler Seite.
	 */
	public HTTP(String webroot, String errorPage) {
		this.webroot = webroot;
		this.errorPage = errorPage;
	}
}