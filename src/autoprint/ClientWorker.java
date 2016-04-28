/**
 * @author - Robert Miller
 * @author - Hunter Quant
**/

package autoprint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.Lock;
import java.util.Date;
import java.util.TimeZone;

public class ClientWorker implements Runnable{
	private Socket client;
	private Printer p;
	private Lock printerLock;
	
	/**
	 * 
	 * @param client - the client socket
	 * @param p - the printer
	 * @param printerLock - lock so only one thread prints at a time
	 */
	ClientWorker(Socket client, Printer p, Lock printerLock){
			this.client = client;
			this.p = p;
			this.printerLock = printerLock;
	}

	/**
	 * gets the order from the client
	 * locks the printer object
	 * prints the order
	 * unlocks the printer and then closes the client
	 */
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
						line += "Time Ordered: ";
						line += new SimpleDateFormat("HH:mm:ss").format(new Date()) + '\n';
					}else if (s.equals("</order>")){
						line += "";
					}else{
						line+= s + "\n";
					}
					}
					in.close();
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
