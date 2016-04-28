package autoprint;

import java.awt.*;
import java.awt.print.*;
import java.util.Vector;

public class Printer implements Printable {
	/**
	 * widthInches - width of paper
	 * heightInches - height of paper 
	 * printJob - the printJob
	 * stringToPrint - the unformatted order string
	 * ok - whether the printer is ok
	 * pageBreaks - line numbers of where the page breaks start
	 * formattedString - each entry in the vector is a line to print
	 * font - initialized to the default font
	 */
	double widthInches;
	double heightInches;
	PrinterJob printJob;
	String stringToPrint;
	boolean ok;
	int[] pageBreaks = null;
	Vector<String> formattedString = null;
	Font font = new Font("Serif", Font.PLAIN, 10);
	
	/**
	 * sets the values to what is passed
	 * @param w - width of paper
	 * @param h - height of paper
	 * @param font - font
	 * @param fsize - font size
	 * @return returns whether the printer is ok to use
	 */
	public Boolean init(double w, double h, String font, int fsize){
	
	printJob = PrinterJob.getPrinterJob();
	ok = printJob.printDialog();
	widthInches = w;
	heightInches = h;
	this.font = new Font(font, Font.PLAIN, fsize);

	return ok;
	}
	
	/**
	 * gets stuff ready to print the string
	 * @param s - a string to print
	 */
	public void startPrint(String s){
		stringToPrint = s;
		formattedString = null;
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
	
	
	
	/**
	 * formats the string and then prints it line by line
	 */
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        graphics.setColor(Color.black);
		FontMetrics metrics = graphics.getFontMetrics(font);
        int lineHeight = metrics.getHeight();
        graphics.setFont(font);
        int linesPerPage = (int) pageFormat.getImageableHeight()/lineHeight;
        double width = pageFormat.getImageableWidth();
         
        //breaks the text file from one big string into an array of strings
        if(formattedString == null){
        	formattedString = new Vector<String>();
        	for (String line : stringToPrint.split("\n")){
        	if (metrics.stringWidth(line) + 6 > width){
        		String shortLine = new String("");
        		for (String word : line.split(" ")){
        			//adds words until the line length is met
        			if (metrics.stringWidth(shortLine + word + " ") + 6 < width){
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
		return Printable.PAGE_EXISTS;
	}
}
