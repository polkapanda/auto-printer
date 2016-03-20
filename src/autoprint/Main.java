package autoprint;

public class Main {

	public static void main(String[] args) {
		Printer p = new Printer();
		Networker n = new Networker();
		p.init();
		p.startPrint("Really long string to print that hopefully will work and stuff but i really dont know as I am unable to get to a printer right now but maybe I can see if someone I know has a printer I can use for great convinience.");
		
	}

}
