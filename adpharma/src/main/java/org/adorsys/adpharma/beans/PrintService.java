package org.adorsys.adpharma.beans;
import java.io.*;  
import java.util.List;

import javax.print.*;  
import javax.servlet.http.HttpServletRequest;

import org.adorsys.adpharma.domain.PrinterConfiguration;

import com.lowagie.text.Document;
public class PrintService {
	
	public static void printFile(String fileUrl) throws Exception {  
		  //we are going to print "printtest.txt" file which is inside working directory  
		  File file = new File(fileUrl);  
		  InputStream is = new BufferedInputStream(new FileInputStream(file));  
		    
		  //Discover the default print service. If you call PrintServiceLookup.lookupPrintServices  
		  //then it will return an array of print services available and you can choose a  
		  //printer from them  
		  javax.print.PrintService service = PrintServiceLookup.lookupDefaultPrintService();  
		    
		  //Doc flavor specifies the output format of the file (Mime type + Char encoding)  
		  //You can retrieve doc flavors supported by the printer, like this  
		  //DocFlavor [] supportedFlavors = service.getSupportedDocFlavors();  
		  DocFlavor flavor = DocFlavor.INPUT_STREAM.PDF;    
		    
		    
		  // Create the print job  
		  DocPrintJob job = service.createPrintJob();  
		  //Create the Doc. You can pass set of attributes(type of PrintRequestAttributeSet) as the   
		  //3rd parameter specifying the page setup, orientation, no. of copies, etc instead of null.   
		  Doc doc = new SimpleDoc(is, flavor, null);  
		  
		  //Order to print, (can pass attributes instead of null)  
		  job.print(doc, null);  
		  com.lowagie.tools.Executable.printDocumentSilent(fileUrl); 
		  
		  //DocPrintJob.print() is not guaranteed to be synchronous. So it's better to wait on completion  
		  //of the print job before closing the stream. (See the link below)  
		  is.close();  
		  System.out.println("Printing done....");  
		 } 
	
	public static boolean print(String fileUrl ,HttpServletRequest httpServletRequest){
		  DocFlavor flavor = DocFlavor.INPUT_STREAM.PDF;    
		    javax.print.PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
		    System.out.println(services);
		    FileInputStream psStream = null;  
		    try {  
		        psStream = new FileInputStream(fileUrl);  
		        } catch (FileNotFoundException ffne) {  
		          ffne.printStackTrace();  
		        }  
		    if (psStream == null) {  
		        return false;  
		    }       
		    if (services.length > 0)
		    {           String remoteAddr = httpServletRequest.getRemoteAddr();
		              List<PrinterConfiguration> printers = PrinterConfiguration.findPrinterConfigurationsByComputerAdresseEquals(remoteAddr).getResultList();
		        javax.print.PrintService myService = null;
		        if (!printers.isEmpty()) {
		        	PrinterConfiguration printer = printers.iterator().next();
		        for(javax.print.PrintService service : services) {
		            System.out.println(service.getName());
		            if(service.getName().equalsIgnoreCase(printer.getPrinterName())) {
		                myService = service;
				        System.out.println("PDF printer available.");
		                break;
		            }
		        }
		        }
		        if (myService == null) return false;
					
				
		        
		        DocPrintJob printJob = myService.createPrintJob();
		        Doc document = new SimpleDoc(psStream, flavor, null);
		        try {
		            printJob.print(document, null);
		  		  System.out.println("Printing done 2....");  

		        } catch (PrintException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		            return false ;
		        }
		    }
		    else
		    {
		        System.out.println("No PDF printer available.");
		        return false ;
		    }  
		    return true ;
	}

}
