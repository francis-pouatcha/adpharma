package org.adorsys.adpharma.beans.pdf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.LigneCmdClient;
import org.adorsys.adpharma.domain.OperationCaisse;
import org.adorsys.adpharma.domain.Ordonnancier;
import org.adorsys.adpharma.domain.TypePaiement;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("ordonnancierPdfDocView")

public class OrdonnancierPdfDocView extends   AbstractPdfView {
	@Override
	protected void buildPdfDocument(Map<String, Object> model,Document document, PdfWriter writer, HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		Ordonnancier ordonnancier = (Ordonnancier) model.get("ordonnancier");
		Font headerStyle = new Font(Font.COURIER,6);
		headerStyle.setStyle("bold");
		Font boddyStyle = new Font(Font.COURIER,6);
		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.5f);
		cellStyle.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cellBorderlessStyle = new PdfPCell(cellStyle);
		cellBorderlessStyle.setBorderWidth(0);
		

		PdfPCell cellBorderless = new PdfPCell(cellStyle);
		cellBorderless.setBorderWidth(0);


		PdfPCell cellBordertop = new PdfPCell(cellStyle);
		cellBordertop.setBorderWidthTop(0);
		cellBordertop.setHorizontalAlignment(Element.ALIGN_CENTER);

		cellBorderless.setHorizontalAlignment(Element.ALIGN_CENTER);
		PdfPCell amountStyle = new PdfPCell(cellStyle);
		amountStyle.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		PdfPCell amountBorderlessStyle = new PdfPCell(cellBorderlessStyle);
		amountBorderlessStyle.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		// addresse de la pharmacie
		float[] adColumnsWith = {2.2f, 5f, 4.6f};
		PdfPTable adressTable = new PdfPTable(adColumnsWith);
		adressTable.setWidthPercentage(100);
		PdfPCell senderCell = new PdfPCell(cellBorderlessStyle);
		PdfPCell caisseCell = new PdfPCell(cellBorderlessStyle);
		senderCell.setPhrase(new Phrase(new Chunk("PHARMACIE DE L'ALLIANCE ", headerStyle)));
		senderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		caisseCell.setPhrase(new Phrase(new Chunk("ORD NO : ", headerStyle)));
		caisseCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell caissenoCell = new PdfPCell(cellBorderlessStyle);
		caissenoCell.setPhrase(new Phrase(new Chunk(""+ordonnancier.getOrdNumber(), boddyStyle)));
		caissenoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		PdfPCell emptyCell = new PdfPCell(cellBorderlessStyle);
		adressTable.addCell(caisseCell);// 1:2
		adressTable.addCell(caissenoCell);// 1:1
		adressTable.addCell(senderCell);// 1:1

		// Invoice company
		PdfPCell addCell1 = new PdfPCell(cellBorderlessStyle);
		addCell1.setPhrase(new Phrase(new Chunk("DR TIENGOUE PULCHERIE ", headerStyle)));
		addCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell caissierCell1 = new PdfPCell(cellBorderlessStyle);
		caissierCell1.setPhrase(new Phrase(new Chunk("Prescripteur :", headerStyle)));
		caissierCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell caissiernameCell1 = new PdfPCell(cellBorderlessStyle);
		caissiernameCell1.setPhrase(new Phrase(new Chunk( ordonnancier.getPrescripteur(), boddyStyle)));
		caissiernameCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		adressTable.addCell(caissierCell1);// 1:2
		adressTable.addCell(caissiernameCell1);// 1:1
		adressTable.addCell(addCell1);// 1:1

		
		PdfPCell addCell2 = new PdfPCell(cellBorderlessStyle);
		addCell2.setPhrase(new Phrase(new Chunk("Sise CAMP YABASSI B.P 11526 DLA", boddyStyle)));
		addCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		if (ordonnancier.getDatePrescription() != null) {
			
		PdfPCell ouCell2 = new PdfPCell(cellBorderlessStyle);
		ouCell2.setPhrase(new Phrase(new Chunk("Date Pres :", headerStyle)));
		ouCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell oudCell2 = new PdfPCell(cellBorderlessStyle);
		oudCell2.setPhrase(new Phrase(new Chunk( PharmaDateUtil.format(ordonnancier.getDatePrescription(), "dd-MM-yyyy HH:mm"), boddyStyle)));
		oudCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		adressTable.addCell(ouCell2);
		adressTable.addCell(oudCell2);// 1:2
		adressTable.addCell(addCell2);// 1:2
		
		}
		PdfPCell telCell = new PdfPCell(cellBorderlessStyle);
		telCell.setPhrase(new Phrase(new Chunk("Tel: (237) - 342.99.15  Fax : 343.63.13", boddyStyle)));
		telCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		
		PdfPCell ferCell = new PdfPCell(cellBorderlessStyle);
		ferCell.setPhrase(new Phrase(new Chunk("Patient :", headerStyle)));
		ferCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell fedrCell = new PdfPCell(cellBorderlessStyle);
		adressTable.addCell(ferCell);
		if (ordonnancier.getNomDuPatient()!=null) {
			fedrCell.setPhrase(new Phrase(ordonnancier.getNomDuPatient(), boddyStyle));
			fedrCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			adressTable.addCell(fedrCell);// 1:2

		}else {
			adressTable.addCell(emptyCell);// 1:2

		}
		adressTable.addCell(telCell);// 1:2

		
		PdfPCell emailCell = new PdfPCell(cellBorderlessStyle);
		emailCell.setPhrase(new Phrase(new Chunk("Email : phciealliance@yahoo.ca", boddyStyle)));
		emailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell encCell = new PdfPCell(cellBorderlessStyle);
		encCell.setPhrase(new Phrase(new Chunk("HOSPITAL :", headerStyle)));
		encCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell encmCell = new PdfPCell(cellBorderlessStyle);
		encmCell.setPhrase(new Phrase(new Chunk(""+ordonnancier.getHospital(), boddyStyle)));
		encmCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	
		adressTable.addCell(encCell);
		adressTable.addCell(encmCell);// 1:2
		adressTable.addCell(emailCell);// 1:2
		
		document.add(adressTable);
		PdfPTable adressTable1 = new PdfPTable(adColumnsWith);
		adressTable1.setWidthPercentage(100);
		
		document.add(adressTable1);
				
		PdfPTable spaceTable = new PdfPTable(1);
		spaceTable.addCell(emptyCell);
		spaceTable.setSpacingAfter(5);
		document.add(spaceTable);

		// la table d'approvisionement
		float[] colWidths = {1.3f,3.2f, 1.3f, };
		PdfPTable table = new PdfPTable(colWidths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setHeaderRows(1);
		
		headerStyle.setColor(Color.WHITE);

		PdfPCell cipMCell = new PdfPCell(cellStyle);
		cipMCell.setPhrase(new Phrase(new Chunk("CODE PRODUIT", headerStyle)));
		cipMCell.setBackgroundColor(Color.gray);
		cipMCell.setPaddingBottom(5);
		table.addCell(cipMCell );
		
		
		PdfPCell cipCell = new PdfPCell(cellStyle);
		cipCell.setPhrase(new Phrase(new Chunk("DESIGNATION", headerStyle)));
		cipCell.setBackgroundColor(Color.gray);
		cipCell.setPaddingBottom(5);

		table.addCell(cipCell);
		
		PdfPCell desCell = new PdfPCell(cellStyle);
		desCell.setPhrase(new Phrase(new Chunk("QTE ACHETEE ", headerStyle)));
		desCell.setBackgroundColor(Color.gray);
		desCell.setPaddingBottom(5);

		table.addCell(desCell);
		
		Set<LigneCmdClient> lineCommande = ordonnancier.getCommande().getLineCommande();
         

if (lineCommande.iterator().hasNext()) {
	for (LigneCmdClient line : lineCommande) {	
		
		//code
		PdfPCell inCipm = new PdfPCell(cellBorderless);
		inCipm.setPhrase(new Phrase(new Chunk(line.getCipM(), boddyStyle)));
		inCipm.setPaddingBottom(5);
			table.addCell(inCipm);

         //designation
			PdfPCell inCipCell = new PdfPCell(cellBorderless);
			inCipCell.setPhrase(new Phrase( line.getDesignation(), boddyStyle));
			inCipCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			inCipCell.setPaddingBottom(5);
			table.addCell(inCipCell);

		//qte commandee
			PdfPCell inDesCell = new PdfPCell(cellBorderless);
			inDesCell.setPhrase(new Phrase(new Chunk(line.getQuantiteCommande().toString(), boddyStyle)));
			table.addCell(inDesCell);

		}
}

	     document.add(table);
	
	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,
			
	    Document document, HttpServletRequest request) {
		document.setPageSize(PageSize.A6);
		document.setMargins(5, 5, 10, 10);
		Font boddyStyle = new Font(Font.COURIER,8);
		Font headerStyle = new Font(Font.COURIER,15);
		headerStyle.setStyle(Font.BOLD);


		HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " Page" , boddyStyle)), true);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);
		HeaderFooter header = null ;
       	 header	= new HeaderFooter(new Phrase(new Chunk("ORDONNANCIER  ", headerStyle)), false);
 	
		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);
		
		super.buildPdfMetadata(model, document, request);
	}
}
