package autoprint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.locks.Lock;

public class ClientWorker implements Runnable{
	private Socket client;
	private Printer p;
	private Lock printerLock;
	
	ClientWorker(Socket client, Printer p, Lock printerLock){
			this.client = client;
			this.p = p;
			this.printerLock = printerLock;
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
					String s = new String();
					while((s = in.readLine()) != null){
					if (s.equals("<order>")){
						line = new String();
					}else if (s.equals("</order>")){
						line += "";
					}else{
						line+= s + "\n";
					}
					}
				}catch (Exception e){
					System.out.println(e.getMessage());
				}
				if (line != null){
				printerLock.lock();
				try{
				p.startPrint(line);
				}finally{
					printerLock.unlock();
				}
				}
				
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
}
