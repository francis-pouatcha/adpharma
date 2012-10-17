package org.adorsys.adpharma.beans;
import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.lang.StringUtils;
/**
 * service for print document on client side
 * @author gkc
 *
 */
public class PrintService {
	
 /**
 *  windows command for call acrord.exe
 */
private static final	String ACROD_COMMAND =  "C:\\Program Files\\Adobe\\Acrobat 9.0\\Reader\\AcroRd32.exe"   ; //"cmd.exe /C start acrord32 /P /h" ;

	/**
	 * use for printtind Pdf document 
	 * @param fileUrl
	 * @param printerName
	 * @throws Exception
	 */
	public static void PrintFile(String fileUrl,String printerName) throws Exception {  
		
		Boolean isPrinterWhithName =Boolean.FALSE ;
		
		//we are going to print "printtest.txt" file which is inside working directory  
		  File file = new File(fileUrl);  
		  InputStream is = new BufferedInputStream(new FileInputStream(file));  
		  //Discover the default print service. If you call PrintServiceLookup.lookupPrintServices  
		  //then it will return an array of print services available and you can choose a  
		  //printer from them  
		  javax.print.PrintService service = PrintServiceLookup.lookupDefaultPrintService();  
		    
		  //Doc flavor specifies the output format of the file (Mime type + Char encoding)  
		  //You can retrieve doc flavors supported by the printer, like this  
		  // List<DocFlavor> supportedFlavors = Arrays.asList(service.getSupportedDocFlavors());  
		  DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		  javax.print.PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
		  // Create the print job  
		
		  
		  DocPrintJob job = null ;//service.createPrintJob(); 
		  if(StringUtils.isNotBlank(printerName)){
			  DocPrintJob job1 =null;
			  for(javax.print.PrintService s : services) {
				  
		            System.out.println(s.getName());
		            if(s.getName().equalsIgnoreCase(printerName)) {
				        System.out.println("PDF printer available.");
				        job1 = s.createPrintJob(); 
				        isPrinterWhithName =Boolean.TRUE;
				        job=job1;
		                break;
		            }
		        }
		}
		  
		  
		  
		  if(job ==null) throw new NullPointerException("no Printer Available"); 
		  //Create the Doc. You can pass set of attributes(type of PrintRequestAttributeSet) as the   
		  //3rd parameter specifying the page setup, orientation, no. of copies, etc instead of null.   
		  Doc doc = new SimpleDoc(is, flavor, null);  
		  //Order to print, (can pass attributes instead of null)  
		  if(com.lowagie.tools.Executable.isWindows()){
			 com.lowagie.tools.Executable.printDocumentSilent(file); 
			if(isPrinterWhithName)  acroPrint(fileUrl, printerName);
		  }else {
			  job.print(doc, null);
		    }   
		
		  
		  //DocPrintJob.print() is not guaranteed to be synchronous. So it's better to wait on completion  
		  //of the print job before closing the stream. (See the link below)  
		  is.close();  
		  System.out.println("Printing done....");  
		 } 
	
	
	
	/**
	 * print pdf document in windows platform using acrord.exe command
	 * @param filePath
	 * @param printerName
	 */
	public static void acroPrint(String filePath,String printerName){		
		Map map = new HashMap();
		map.put("file", new File(filePath));
		map.put("printerName",printerName);
		CommandLine cmdLine = new CommandLine(new File(ACROD_COMMAND));
		cmdLine.addArgument("/p");
		cmdLine.addArgument("/h");
		cmdLine.addArgument("${file}");
		cmdLine.addArgument("${printerName}");
		cmdLine.setSubstitutionMap(map);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(1);
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
		executor.setWatchdog(watchdog);
		try {
			int exitValue = executor.execute(cmdLine);
		} catch (ExecuteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	      /*  try
	        {            
	        	Runtime.getRuntime().exec(ACROD_COMMAND +" "+ filePath +" "+printerName);
	        	System.out.println("acrord printing done ... ");
	        } catch (Throwable t){
	            t.printStackTrace();
	        }*/

		
	}
	
	public static boolean silentPrint( String filePath){
		
		

				File file = new File(filePath);
				
					 try {
						 Desktop.getDesktop().print(file);
						//com.lowagie.tools.Executable.printDocumentSilent(file,false);
						 return true ;
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}// TODO Auto-generated method stub
			
			
			return false ;
		
	}

}
