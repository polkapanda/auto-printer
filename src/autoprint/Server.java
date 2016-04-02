package autoprint;

import java.io.IOException;
import java.net.ServerSocket;

public class Server implements Runnable{
	double width;
	double height;
	static ServerSocket server;
	public Server(double x, double y) {
		width = x;
		height = y;
	}
	/**
	 * 
	 * @param p
	 */
	public static void listenSocket(Printer p){
		try{
			server = new ServerSocket(5969);
		}catch(IOException ioe){
			System.out.println("Exception in main");
		}
		while(true){
			ClientWorker w;
			try{
				server.setSoTimeout(60000);
				w = new ClientWorker(server.accept(), p);
				Thread t = new Thread(w);
				t.start();
			}catch(IOException e){
				System.out.println("thread clientworker error");
			}
		}
	}
	public void run(){
		Printer p = new Printer(width, height);
		//listenSocket(p);
		p.startPrint("Really long string to print that hopefully will work and stuff but i really dont know as I am unable to get to a printer right now but maybe I can see if someone I know has a printer I can use for great convinience.");
	}
}
