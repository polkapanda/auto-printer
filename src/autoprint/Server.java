package autoprint;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements Runnable{
	double width;
	double height;
	String font = new String();
	int fontSize;
	static int portNum;
	static ServerSocket server;

	public Server(double x, double y, int pN, String fn, int fs) {
		width = x;
		height = y;
		font = fn;
		fontSize = fs;
		portNum = pN;
	}
	public Server(double x, double y, int pN, String fn) {
		width = x;
		height = y;
		font = fn;
		fontSize = 10;
		portNum = pN;
	}
	public Server(double x, double y, int pN) {
		width = x;
		height = y;
		font = "Serif";
		fontSize = 10;
		portNum = pN;
	}
	public Server(double x, double y, String fn, int fs){
		width = x;
		height = y;
		font = fn;
		fontSize = fs;
		portNum = 5969;
	}
	public Server(double x, double y, String fn){
		width = x;
		height = y;
		font = fn;
		fontSize = 10;
		portNum = 5969;
	}
	public Server(double x, double y) {
		width = x;
		height = y;
		font = "Serif";
		fontSize = 10;
		portNum = 5969;
	}
	/**
	 * 
	 * @param p
	 */
	public static void listenSocket(Printer p){
		ReentrantLock printerLock = new ReentrantLock();
		try{
			server = new ServerSocket(portNum);
		}catch(IOException ioe){
			System.out.println("Exception creating server");
		}
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
	public void run(){
		Printer p = new Printer();
		if (p.init(width, height, font, fontSize) == true){
			Main.goodPrint();
		}else{
			Main.printerNotConnect();
		}
		listenSocket(p);
		//p.startPrint("Really long string to print that hopefully will work and stuff but i really dont know as I am unable to get to a printer right now but maybe I can see if someone I know has a printer I can use for great convinience.");
	}
}
