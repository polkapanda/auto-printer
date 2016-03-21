package autoprint;

import java.awt.*;
import java.awt.print.*;
import java.util.Vector;

public class Printer implements Printable {
	PrinterJob printJob;
	String stringToPrint;
	int[] pageBreaks = null;
	Vector<String> formattedString = null;
	public void init(){
	printJob = PrinterJob.getPrinterJob();
	}
	
	public void startPrint(String s){
		stringToPrint = s;
		boolean ok = printJob.printDialog();
		if (ok){
			PageFormat pf = printJob.defaultPage();
			Paper p = pf.getPaper();
			double width = 5.5d * 72d;
			double height = 8.5d * 72d;
			double margin = 0.2d * 72d;
			p.setSize(width, height);
			p.setImageableArea(
					margin,
					margin,
					width - (margin*2),
					height - (margin*2));
			pf.setOrientation(PageFormat.PORTRAIT);
			pf.setPaper(p);
			Book pBook = new Book();
			pBook.append(this, pf);
			printJob.setPageable(pBook);
			
			try{
				printJob.print();
			}catch(PrinterException pe){
				System.err.println("Print failed");
				System.err.println("Exception:\n" + pe);
			}
		}
	}
	
	

	

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        
		Font font = new Font("Serif", Font.PLAIN, 10);
        FontMetrics metrics = graphics.getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        
        int linesPerPage = (int) pageFormat.getImageableHeight()/lineHeight;
        double width = pageFormat.getImageableWidth();
        
        if (pageBreaks == null){
        	//breaks the text file from one big string into an array of strings
        	for (String line : stringToPrint.split("\n")){
        		if (metrics.stringWidth(line) > width){
        			String shortLine = new String();
        			for (String word : line.split(" ")){
        				//adds words until the line length is met
        				if (metrics.stringWidth(shortLine + word) > width)
        					shortLine += word;
        				//adds the shorter line to a vector and then resets the line to the new word
        				else{
        					formattedString.add(shortLine);
        					shortLine = word;
        				}
        			}
        			//adds the last line if the initial line is too long
        				formattedString.add(shortLine);
        		}else{
        			//adds the line if it is shorter than the line width
        			formattedString.add(line);
        		}
        	}
        	//gets place of page breaks
        	int numBreaks = (formattedString.size()-1)/linesPerPage;
        	pageBreaks = new int[numBreaks];
        	for(int b = 0; b < numBreaks; b++){
        		pageBreaks[b] = (b+1)*linesPerPage;
        	}


        }
        
		if (pageIndex >= 1) {
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
			graphics.drawString(formattedString.get(line), 0, y);
		}
		
		return Printable.PAGE_EXISTS;
	}
}
