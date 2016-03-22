package autoprint;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

	static ServerSocket server;
	/**
	 * 
	 * @param p
	 * @param printerLock
	 */
	public static void listenSocket(Printer p, Lock printerLock){
		try{
			server = new ServerSocket(5969);
		}catch(IOException ioe){
			System.out.println("Exception in main");
		}
		while(true){
			ClientWorker w;
			try{
				w = new ClientWorker(server.accept(), p, printerLock);
				Thread t = new Thread(w);
				t.start();
			}catch(IOException e){
				System.out.println("thread clientworker error");
			}
		}
	}
	
	public static void main(String[] args) {
		Printer p = new Printer();
		Lock printerLock = new ReentrantLock();
		p.init();
		listenSocket(p, printerLock);
		//p.startPrint("Really long string to print that hopefully will work and stuff but i really dont know as I am unable to get to a printer right now but maybe I can see if someone I know has a printer I can use for great convinience.");
		
	}

}
