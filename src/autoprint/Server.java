/**
 * @author - Robert Miller
 * @author - Hunter Quant
**/

package autoprint;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements Runnable{
	private double width;
	private double height;
	private String font = new String();
	private int fontSize;
	/**
	 * @param portNum - the port number for the socket
	 * @param server - the server socket
	 */
	static int portNum;
	static ServerSocket server;

	/**
	 * Constructor when all the options are changed
	 * @param x - width of paper
	 * @param y - height of paper
	 * @param pN - port number
	 * @param fn - font 
	 * @param fs - font size
	 */
	public Server(double x, double y, int pN, String fn, int fs) {
		width = x;
		height = y;
		font = fn;
		fontSize = fs;
		portNum = pN;
	}
	/**
	 * Constructor when all but font size are changed
	 * @param x - width of paper
	 * @param y - height of paper
	 * @param pN - port number
	 * @param fn - font
	 */
	public Server(double x, double y, int pN, String fn) {
		width = x;
		height = y;
		font = fn;
		fontSize = 10;
		portNum = pN;
	}
	/**
	 * Constructor when width, height and port number are given
	 * @param x - width of paper
	 * @param y - height of paper
	 * @param pN - port number
	 */
	public Server(double x, double y, int pN) {
		width = x;
		height = y;
		font = "Serif";
		fontSize = 10;
		portNum = pN;
	}
	/**
	 * Constructor when width, height, font and font size are given 
	 * @param x - width of paper
	 * @param y - height of paper
	 * @param fn - font
	 * @param fs - font size
	 */
	public Server(double x, double y, String fn, int fs){
		width = x;
		height = y;
		font = fn;
		fontSize = fs;
		portNum = 5969;
	}
	/**
	 * Constructor when width, height, and font are given
	 * @param x - width of paper
	 * @param y - height of paper
	 * @param fn - font 
	 */
	public Server(double x, double y, String fn){
		width = x;
		height = y;
		font = fn;
		fontSize = 10;
		portNum = 5969;
	}
	/**
	 * Constructor when only width and height are given
	 * @param x - width of paper
	 * @param y - height of paper
	 */
	public Server(double x, double y) {
		width = x;
		height = y;
		font = "Serif";
		fontSize = 10;
		portNum = 5969;
	}
	/**
	 * Listens on the socket until application is closed
	 * @param p - printer that is being printed to 
	 */
	public static void listenSocket(Printer p){
		ReentrantLock printerLock = new ReentrantLock();
		try{
			server = new ServerSocket(portNum);
		}catch(IOException ioe){
			System.out.println("Exception creating server");
		}
		//spools off a new thread whenever a socket tries to connect
		while(true){
			Socket clientSocket = null;
			try{
				clientSocket = server.accept();
			}catch(IOException e){
				System.out.println("thread clientworker error");
			}
			new Thread(
			new ClientWorker(clientSocket, p, printerLock)).start();
		}
	}
	/**
	 * sets up a printer
	 */
	public void run(){
		//creates a printer, initializes it and then starts listening if it is good
		Printer p = new Printer();
		if (p.init(width, height, font, fontSize) == true){
			Main.goodPrint();
			listenSocket(p);
		}else{
			Main.printerNotConnect();
		}
	}
}
