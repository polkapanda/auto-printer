package autoprint;

import java.awt.*;
import java.awt.print.*;

public class Printer implements Printable {
	PrinterJob printJob;
	String stringToPrint;
	public void init(){
	printJob = PrinterJob.getPrinterJob();
	}
	
	public void startPrint(String s){
		stringToPrint = "<html>"+s+"</html>";
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
        
        
		if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }
		Graphics2D g2d = (Graphics2D)graphics;
		g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
		double width = pageFormat.getImageableWidth();
		double height = pageFormat.getImageableHeight();
		graphics.drawString(stringToPrint, 0, 0);
		return Printable.PAGE_EXISTS;
	}
}