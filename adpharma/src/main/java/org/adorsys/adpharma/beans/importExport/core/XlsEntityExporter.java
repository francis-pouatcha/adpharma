package org.adorsys.adpharma.beans.importExport.core;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.swing.text.html.parser.Entity;

import org.adorsys.adpharma.domain.CommandeClient;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
/**
 * @author adorsys-GKC
 *
 */
public class XlsEntityExporter<T> {
	public  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	public Class<T> entityClass ;
	public GeneriqueFieldConverter fieldConverter = new GeneriqueFieldConverter();

	public ByteArrayOutputStream exportEntityList(List<T> entityList, String... fieldsToExport) throws Exception {
		if(hasFieldInEntity( fieldsToExport)){
			WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
			WritableSheet sheet = workbook.createSheet(entityClass.getName(),0);
			for (int i = 0; i < fieldsToExport.length; i++) {
				Label ciph= new Label(i, 0,fieldsToExport[i] );
				sheet.addCell(ciph);
			}
			if(!entityList.isEmpty()){
				int size = entityList.size();
				for (int i = 1; i <= size; i++) {
					for (int j = 0; j < fieldsToExport.length; j++) {
						//entityClass.get
						//Label ciph= new Label(j, i,fieldsToExport[i] );
						//sheet.addCell(ciph);
					}
					
				}
				
			}
		}
		return outputStream ;
		
	}
	
	public boolean hasFieldInEntity(String... fieldsToExport) throws NotMatchFieldException{
		if(fieldsToExport.length == 0 ) return false ;
		for (int i = 0; i < fieldsToExport.length; i++) {
			try {
				if(entityClass.getField(fieldsToExport[i]) == null) {
					System.out.println(entityClass.getField(fieldsToExport[i]));
					throw new NotMatchFieldException(entityClass, fieldsToExport[i]) ;
				}
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return true ;
	}

}
