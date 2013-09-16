package org.adorsys.adpharma.beans.importExport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;



/**
 * Used to export any type of data excel file
 * @author hsimo
 *
 * @param <T>
 */
public interface ExportDataService<T> {
	
	// The method that export data
	void exportData(String fileName, List<T> data, String[] headerColumns) throws IOException, FileNotFoundException;
	

}
