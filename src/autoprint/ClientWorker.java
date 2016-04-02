package autoprint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientWorker implements Runnable{
	private Socket client;
	private Printer p;
	private Lock printerLock = new ReentrantLock();;
	
	ClientWorker(Socket client, Printer p){
			this.client = client;
			this.p = p;
	}

	public void run(){
			String line = null;
			BufferedReader in = null;
			try {
				in = new BufferedReader (
						new InputStreamReader(client.getInputStream()));	
			}catch (Exception e){
				System.out.println("buffered error");
			}
			
				try{
					line = in.readLine();
					
				}catch (Exception e){
					System.out.println("line error");
				}
				printerLock.lock();
				try{
				p.startPrint(line);
				}finally{
					printerLock.unlock();
				}
	}

}
