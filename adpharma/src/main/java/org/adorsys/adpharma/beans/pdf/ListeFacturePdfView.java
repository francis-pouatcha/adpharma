package org.adorsys.adpharma.beans.pdf;

import java.awt.Color;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.Client;
import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.security.SecurityUtil;
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

@Component("listeFacturePdfView")

public class ListeFacturePdfView extends   AbstractPdfView  {
	
	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String clientNo =  (String) model.get("clientNo");
		Boolean solder =  (Boolean) model.get("solder");

		Font headerStyle = new Font(Font.COURIER,8);
		headerStyle.setStyle("bold");
		Font boddyStyle = new Font(Font.COURIER,8);
		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.5f);
		cellStyle.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cellBorderlessStyle = new PdfPCell(cellStyle);
		cellBorderlessStyle.setBorderWidth(0);
		

		PdfPCell cellBorderless = new PdfPCell(cellStyle);
		cellBorderless.setPaddingTop(2);
		cellBorderless.setPaddingBottom(3);
		cellBorderless.setBorderWidth(0);
        cellBorderless.setBorderWidthBottom(.5f);
        cellBorderless.setMinimumHeight(20);
        cellBorderless.setVerticalAlignment(Element.ALIGN_CENTER);
        cellBorderless.setBorderColorBottom(Color.gray);


		PdfPCell cellBordertop = new PdfPCell(cellStyle);
		cellBordertop.setBorderWidthTop(0);
		cellBordertop.setHorizontalAlignment(Element.ALIGN_CENTER);

		cellBorderless.setHorizontalAlignment(Element.ALIGN_CENTER);
		PdfPCell amountStyle = new PdfPCell(cellStyle);
		amountStyle.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		PdfPCell amountBorderlessStyle = new PdfPCell(cellBorderlessStyle);
		amountBorderlessStyle.setHorizontalAlignment(Element.ALIGN_RIGHT);
		
		// addresse de la pharmacie
		float[] adColumnsWith = {2f, 2.4f, 2f};
		PdfPTable adressTable = new PdfPTable(adColumnsWith);
		adressTable.setWidthPercentage(100);
		PdfPCell senderCell = new PdfPCell(cellBorderlessStyle);
		senderCell.setPhrase(new Phrase(new Chunk("PHARMACIE DE L'ALLIANCE ", headerStyle)));
		senderCell.setColspan(2);
		senderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell emptyCell = new PdfPCell(cellBorderlessStyle);
		adressTable.addCell(senderCell);// 1:2
		adressTable.addCell(emptyCell);// 1:1
		// Invoice company
		PdfPCell addCell1 = new PdfPCell(cellBorderlessStyle);
		addCell1.setColspan(2);
		addCell1.setPhrase(new Phrase(new Chunk("DR TIENGOUE PULCHERIE ", headerStyle)));
		addCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(addCell1);// 1:2
		adressTable.addCell(emptyCell);// 1:1
		
		PdfPCell addCell2 = new PdfPCell(cellBorderlessStyle);
		addCell2.setPhrase(new Phrase(new Chunk("Sise CAMP YABASSI B.P 11526 DLA", boddyStyle)));
		addCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		addCell2.setColspan(2)	;	
		addCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(addCell2);
		adressTable.addCell(emptyCell);// 1:2
		PdfPCell telCell = new PdfPCell(cellBorderlessStyle);
		telCell.setPhrase(new Phrase(new Chunk("Tel: (237) - 342.99.15  Fax : 343.63.13", boddyStyle)));
		telCell.setColspan(2);
		telCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(telCell);
		adressTable.addCell(emptyCell);// 1:2
		
		PdfPCell emailCell = new PdfPCell(cellBorderlessStyle);
		emailCell.setPhrase(new Phrase(new Chunk("Email : phciealliance@yahoo.ca", boddyStyle)));
		emailCell.setColspan(2);
		emailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(emailCell);
		
		PdfPCell agentCell = new PdfPCell(cellBorderlessStyle);
		agentCell.setPhrase(new Phrase(new Chunk("Imprime Le : " + PharmaDateUtil.format(new Date(), "dd/MM/yyyy HH:mm"), headerStyle)));
		agentCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(agentCell);

		adressTable.addCell(emptyCell);// 1:2
		
		document.add(adressTable);
		PdfPTable adressTable1 = new PdfPTable(adColumnsWith);
		adressTable1.setWidthPercentage(100);
		
		document.add(adressTable1);
				
		PdfPTable spaceTable = new PdfPTable(1);
		spaceTable.addCell(emptyCell);
		spaceTable.setSpacingAfter(5);
		document.add(spaceTable);
		
		// la table d'approvisionement
		float[] colWidths = {1.2f,3.5f, 1.8f, 1.2f, 1.2f, 1.2f ,1.8f};
		PdfPTable table = new PdfPTable(colWidths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setHeaderRows(1);
		headerStyle.setColor(Color.WHITE);

		PdfPCell cipMCell = new PdfPCell(cellStyle);
		cipMCell.setPhrase(new Phrase(new Chunk("No Facture ", headerStyle)));
		cipMCell.setBackgroundColor(Color.gray);
		cipMCell.setPaddingBottom(5);
		table.addCell(cipMCell );
		
		
		PdfPCell cipCell = new PdfPCell(cellStyle);
		cipCell.setPhrase(new Phrase(new Chunk("Clients", headerStyle)));
		cipCell.setBackgroundColor(Color.gray);
		cipCell.setPaddingBottom(5);

		table.addCell(cipCell);
		
		PdfPCell desCell = new PdfPCell(cellStyle);
		desCell.setPhrase(new Phrase(new Chunk("Date Edition", headerStyle)));
		desCell.setBackgroundColor(Color.gray);
		desCell.setPaddingBottom(5);

		table.addCell(desCell);
		

		PdfPCell datePrmCell = new PdfPCell(cellStyle);
		datePrmCell.setPhrase(new Phrase(new Chunk("Montant ", headerStyle)));
		datePrmCell.setBackgroundColor(Color.gray);
		datePrmCell.setPaddingBottom(5);

		table.addCell(datePrmCell);
		
		PdfPCell pvCell = new PdfPCell(cellStyle);
		pvCell.setPhrase(new Phrase(new Chunk("Avance ", headerStyle)));
		pvCell.setBackgroundColor(Color.gray);
		pvCell.setPaddingBottom(5);

		table.addCell(pvCell);


		PdfPCell paCell = new PdfPCell(cellStyle);
		paCell.setPhrase(new Phrase(new Chunk("Reste a Payer", headerStyle)));
		paCell.setBackgroundColor(Color.gray);
		paCell.setPaddingBottom(5);

		table.addCell(paCell);

		PdfPCell ddCell = new PdfPCell(cellStyle);
		ddCell.setPhrase(new Phrase(new Chunk("Date Dernier Versement", headerStyle)));
		ddCell.setBackgroundColor(Color.gray);
		ddCell.setPaddingBottom(5);
		table.addCell(ddCell);
		
		BigInteger totalMontant = BigInteger.ZERO;
		BigInteger totalAvance = BigInteger.ZERO;
		BigInteger totalReste =  BigInteger.ZERO;

		


 	List<DetteClient> detteClient = DetteClient.findDetteClientsBySolderNotAndClientNoEquals(solder , clientNo).getResultList() ;

	for (DetteClient line : detteClient) {	
		
		//cipm
		PdfPCell inCipm = new PdfPCell(cellBorderless);
		inCipm.setPhrase(new Phrase(new Chunk(line.getFactureNo(), boddyStyle)));
		inCipm.setPaddingBottom(5);
		inCipm.setHorizontalAlignment(Element.ALIGN_LEFT);

			table.addCell(inCipm);
			
         //cip
			PdfPCell inCipCell = new PdfPCell(cellBorderless);
			inCipCell.setPhrase(new Phrase(new Chunk(Facture.findFacture(line.getFactureId()).getClient().getNomComplet().toUpperCase(), boddyStyle)));
			inCipCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			inCipCell.setPaddingBottom(5);
			inCipCell.setPaddingLeft(5);

			table.addCell(inCipCell);
			
		//Designation
			PdfPCell inDesCell = new PdfPCell(cellBorderless);
			inDesCell.setPhrase(new Phrase(new Chunk(PharmaDateUtil.format(line.getDateCreation(), "dd/MM/yyyy HH:mm"), boddyStyle)));
			table.addCell(inDesCell);
			
			// date Peremption
			PdfPCell inprmCell = new PdfPCell(cellBorderless);
			inprmCell.setPhrase(new Phrase(new Chunk(line.getMontantInitial().toString(), boddyStyle)));
			totalMontant= totalMontant.add(line.getMontantInitial());
			inprmCell.setPaddingBottom(5);
			inprmCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table.addCell(inprmCell);

				// prix Vente
				PdfPCell inPvCell = new PdfPCell(cellBorderless);
				inPvCell.setPhrase(new Phrase(new Chunk(line.getAvance().toString(), boddyStyle)));
				totalAvance= totalAvance.add(line.getAvance());
				inPvCell.setPaddingBottom(5);
				inPvCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(inPvCell);

				// prix achat
				PdfPCell inPaCell = new PdfPCell(cellBorderless);
				inPaCell.setPhrase(new Phrase(new Chunk(line.getReste().toString(), boddyStyle)));
				inPaCell.setPaddingBottom(5);
				inPaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(inPaCell);
				
				totalReste= totalReste .add(line.getReste());
				
				PdfPCell inddCell = new PdfPCell(cellBorderless);
				inddCell.setPaddingBottom(5);
				inddCell.setHorizontalAlignment(Element.ALIGN_CENTER);


				if (line.getDateDernierVersement() !=null) {
					inddCell.setPhrase(new Phrase(new Chunk( PharmaDateUtil.format(line.getDateDernierVersement(), "dd/MM/yyyy HH:mm"), boddyStyle)));
				}else {
					inddCell.setPhrase(new Phrase(new Chunk("-//-", boddyStyle)));
	
				}
				table.addCell(inddCell);
				
		}
	cellStyle.setBorderWidth(0);
 	PdfPCell cipMCell1 = new PdfPCell(cellStyle);
 	cipMCell1.setPhrase(new Phrase(new Chunk("", boddyStyle)));
 	cipMCell1.setBackgroundColor(Color.gray);
 	cipMCell1.setPaddingBottom(5);
	
	table.addCell(cipMCell1 );
	
	PdfPCell cipMCell2 = new PdfPCell(cellStyle);
	cipMCell2.setPhrase(new Phrase(new Chunk("", boddyStyle)));
 	cipMCell2.setBackgroundColor(Color.gray);
 	cipMCell2.setPaddingBottom(5);
	
	table.addCell(cipMCell2 );
	
	
	PdfPCell cipCell2 = new PdfPCell(cellStyle);
	cipCell2.setPhrase(new Phrase(new Chunk("TOTAL :", headerStyle)));
	cipCell2.setBackgroundColor(Color.gray);
	cipCell2.setBorder(0);
	cipCell2.setPaddingBottom(5);
	cipCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	table.addCell(cipCell2);
	
	PdfPCell desCell1 = new PdfPCell(cellStyle);
	desCell1.setPhrase(new Phrase(new Chunk(""+totalMontant, headerStyle)));
	desCell1.setBackgroundColor(Color.gray);
	desCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

	desCell1.setPaddingBottom(5);

	table.addCell(desCell1);
	

	PdfPCell datePrmCell1 = new PdfPCell(cellStyle);
	datePrmCell1.setPhrase(new Phrase(new Chunk(""+totalAvance, headerStyle)));
	datePrmCell1.setBackgroundColor(Color.gray);
	datePrmCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

	datePrmCell1.setPaddingBottom(5);

	table.addCell(datePrmCell1);
	
	PdfPCell pvCell1 = new PdfPCell(cellStyle);
	pvCell1.setPhrase(new Phrase(new Chunk(""+totalReste, headerStyle)));
	pvCell1.setBackgroundColor(Color.gray);
	pvCell1.setPaddingBottom(5);
	pvCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

	table.addCell(pvCell1);

	PdfPCell paCell1 = new PdfPCell(cellStyle);
	paCell1.setPhrase(new Phrase(new Chunk("", headerStyle)));
	paCell1.setBackgroundColor(Color.gray);
	paCell1.setPaddingBottom(5);

	table.addCell(paCell1);
     document.add(table);
	
		
	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,
			
			
		    Document document, HttpServletRequest request) {
			document.setPageSize(PageSize.A4);
			document.setMargins(10, 10, 20, 20);
			Font boddyStyle = new Font(Font.COURIER,8);
			boddyStyle.setStyle("bold");
			String clientNo =  (String) model.get("clientNo");
			Boolean solder =  (Boolean) model.get("solder");
			String facturesolder = null ;
			if (solder) {
				facturesolder = " Non Soldees";
			}else {
				facturesolder = " Soldees";

			}

			HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " Page" , boddyStyle)), true);
			footer.setAlignment(Element.ALIGN_CENTER);
			document.setFooter(footer);
			HeaderFooter header = null ;
	       	 header	= new HeaderFooter(new Phrase(new Chunk("LISTE FACTURES "+facturesolder+" , CLient : "+Client.findClientsByClientNumberEquals(clientNo).getResultList().iterator().next() , boddyStyle)), false);
	 	
			header.setAlignment(Element.ALIGN_CENTER);
			document.setHeader(header);
			
			super.buildPdfMetadata(model, document, request);
		}

}
