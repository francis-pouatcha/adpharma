package org.adorsys.adpharma.beans.importExport;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.utils.DocumentsPath;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class LigneApprovisionementXmlImportExportService {
	
	public File export(List<LigneApprovisionement> ligneApprovisionements){
		File file = new File(DocumentsPath.ROOT_DIR+"catalogueCipm.xml");
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LigneApprovisionement.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(ligneApprovisionements, file);
			marshaller.marshal(ligneApprovisionements, System.out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return file ;
		
	}
	
	public void export(List<LigneApprovisionement> ligneApprovisionements,OutputStream os){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LigneApprovisionement.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(ligneApprovisionements, os);
			marshaller.marshal(ligneApprovisionements, System.out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
