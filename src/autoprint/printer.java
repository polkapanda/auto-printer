package autoprint;

import java.awt.*;
import java.awt.print.*;

public class printer implements Printable {
	PrinterJob printJob = PrinterJob.getPrinterJob();
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		// TODO Auto-generated method stub
		return 0;
	}
}