package org.adorsys.adpharma.tests;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.adorsys.adpharma.beans.LigneApprovisionementExcelRepresentation;
import org.adorsys.adpharma.beans.importExport.ExportLignesApprovisionnement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:META-INF/spring/applicationContext*.xml"})
@Transactional
public class ExportDataTests {
	
	@Autowired
	ExportLignesApprovisionnement exportLignesApprovisionnement;
	
	
	@Test
	public void testExport() throws IOException{
		List<LigneApprovisionementExcelRepresentation> data= new ArrayList<LigneApprovisionementExcelRepresentation>();
		LigneApprovisionementExcelRepresentation representation = new LigneApprovisionementExcelRepresentation("1200000", "450000", "Paracetamol", new BigInteger("10"), "Rayon X");
		for(int i=0; i<6; i++){
			data.add(representation);
		}
		String[] columns={"CIP", "CIPM", "DESIGNATION", "QTE EN STOCK", "EMPLACEMENT"};
		exportLignesApprovisionnement.exportData("/tools/test.xls", data, columns);
		
	}

}
