package autoprint;

import java.awt.*;
import java.awt.print.*;
import java.util.Vector;

public class Printer implements Printable {
	double widthInches;
	double heightInches;
	PrinterJob printJob;
	String stringToPrint;
	boolean ok;
	int[] pageBreaks = null;
	Vector<String> formattedString = null;
	
	public Printer(double w, double h){
	printJob = PrinterJob.getPrinterJob();
	ok = printJob.printDialog();
	widthInches = w;
	heightInches = h;
	}
	
	public void startPrint(String s){
		stringToPrint = s;
		if (ok){
			PageFormat pf = printJob.defaultPage();
			Paper p = pf.getPaper();
			double width = widthInches * 72d;
			double height = heightInches * 72d;
			p.setSize(width, height);
			p.setImageableArea(
					0,
					0,
					width,
					height);
			pf.setOrientation(PageFormat.PORTRAIT);
			pf.setPaper(p);
			printJob.setPrintable(this, pf);
			try{
				printJob.print();
			}catch(PrinterException pe){
				System.err.println("Print failed");
				System.err.println("Exception:\n" + pe);
			}

		}
	}
	
	public void clearString(){
		formattedString = null;
	}

	

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        
		Font font = new Font("Serif", Font.PLAIN, 10);
        graphics.setColor(Color.black);
		FontMetrics metrics = graphics.getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        graphics.setFont(font);
        int linesPerPage = (int) pageFormat.getImageableHeight()/lineHeight;
        double width = pageFormat.getImageableWidth();
        System.out.println("unformated width: " + metrics.stringWidth(stringToPrint));
        System.out.println("Page Width: " + width);
        
        //breaks the text file from one big string into an array of strings
        if(formattedString == null){
        	formattedString = new Vector<String>();
        	for (String line : stringToPrint.split("\n")){
        	System.out.println("line length: " + metrics.stringWidth(line));
        	if (metrics.stringWidth(line) > width){
        		String shortLine = new String("");
        		for (String word : line.split(" ")){
        			//adds words until the line length is met
        			if (metrics.stringWidth(shortLine + word + " ") < width){
        				shortLine += (word + " ");
        			}
        			//adds the shorter line to a vector and then resets the line to the new word
        			else{
        				formattedString.add(shortLine);
        				shortLine = (word + " ");
        			}
        		}
        		//adds the last line if the initial line is too long
        			formattedString.add(shortLine);
        	}else{
        		//adds the line if it is shorter than the line width
        		formattedString.add(line);
        	}
       	}
        }
        
        //gets line number of page breaks
        int numBreaks = (formattedString.size()-1)/linesPerPage;
        pageBreaks = new int[numBreaks];
        for(int b = 0; b < numBreaks; b++){
        	pageBreaks[b] = (b+1)*linesPerPage;
        }
        
		if (pageIndex > pageBreaks.length) {
            return Printable.NO_SUCH_PAGE;
        }
		
		Graphics2D g2d = (Graphics2D)graphics;
		g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
		
		int y = 0;
		int start = (pageIndex == 0) ? 0 //if
									 : pageBreaks[pageIndex - 1]; //else
		int end = (pageIndex == pageBreaks.length) ? formattedString.size() //if
												   : pageBreaks[pageIndex]; //else
		//prints a page of lines
		for(int line = start; line < end; line++){
			y += lineHeight;
			graphics.drawString(formattedString.get(line), 6, y);
		}
		System.out.println("END!!");
		return Printable.PAGE_EXISTS;
	}
}
