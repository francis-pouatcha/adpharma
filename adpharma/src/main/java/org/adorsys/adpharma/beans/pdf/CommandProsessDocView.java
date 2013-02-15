package org.adorsys.adpharma.beans.pdf;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.CommandeFournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.Site;
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

@Component("commandProsessDocView")
public class CommandProsessDocView extends   AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		CommandeFournisseur commande = (CommandeFournisseur) model.get("commande");
		Site site = Site.findSite(Long.valueOf(1));
		Font headerStyle = new Font(Font.COURIER,8);
		headerStyle.setStyle("bold");
		Font boddyStyle = new Font(Font.COURIER,8);
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
		float[] adColumnsWith = {2f, 2.4f, 2f};
		PdfPTable adressTable = new PdfPTable(adColumnsWith);
		adressTable.setWidthPercentage(100);
		PdfPCell senderCell = new PdfPCell(cellBorderlessStyle);
		senderCell.setPhrase(new Phrase(new Chunk(site.getDisplayName().toUpperCase(), headerStyle)));
		senderCell.setColspan(2);
		senderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell emptyCell = new PdfPCell(cellBorderlessStyle);
		adressTable.addCell(senderCell);// 1:2
		adressTable.addCell(emptyCell);// 1:1
		// Invoice company
		PdfPCell addCell1 = new PdfPCell(cellBorderlessStyle);
		addCell1.setColspan(2);
		addCell1.setPhrase(new Phrase(new Chunk(site.getSiteManager().toUpperCase(), headerStyle)));
		addCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(addCell1);// 1:2
		adressTable.addCell(emptyCell);// 1:1
		
		PdfPCell addCell2 = new PdfPCell(cellBorderlessStyle);
		addCell2.setPhrase(new Phrase(new Chunk(site.getAdresse(), boddyStyle)));
		addCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		addCell2.setColspan(2)	;	
		addCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(addCell2);
		adressTable.addCell(emptyCell);// 1:2
		PdfPCell telCell = new PdfPCell(cellBorderlessStyle);
		telCell.setPhrase(new Phrase(new Chunk("Tel: "+site.getPhone() +" Fax : "+ site.getFax(), boddyStyle)));
		telCell.setColspan(2);
		telCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(telCell);
		adressTable.addCell(emptyCell);// 1:2
		
		PdfPCell emailCell = new PdfPCell(cellBorderlessStyle);
		emailCell.setPhrase(new Phrase(new Chunk("Email : "+site.getEmail(), boddyStyle)));
		emailCell.setColspan(2);
		emailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(emailCell);
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
		float[] colWidths = {1.4f,3.7f, 1.4f, 1.4f, 1.4f};
		PdfPTable table = new PdfPTable(colWidths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setHeaderRows(1);
		
		headerStyle.setColor(Color.WHITE);

		
		PdfPCell cipCell = new PdfPCell(cellStyle);
		cipCell.setPhrase(new Phrase(new Chunk("C.I.P ", headerStyle)));
		cipCell.setBackgroundColor(Color.gray);
		cipCell.setPaddingBottom(5);
		table.addCell(cipCell );
	
		
		PdfPCell desCell = new PdfPCell(cellStyle);
		desCell.setPhrase(new Phrase(new Chunk("Designation", headerStyle)));
		desCell.setBackgroundColor(Color.gray);
		desCell.setPaddingBottom(5);

		table.addCell(desCell);
		
		PdfPCell paCell = new PdfPCell(cellStyle);
		paCell.setPhrase(new Phrase(new Chunk("PA Unit ", headerStyle)));
		paCell.setBackgroundColor(Color.gray);
		paCell.setPaddingBottom(5);
		table.addCell(paCell);
		
		PdfPCell qteCell = new PdfPCell(cellStyle);
		qteCell.setPhrase(new Phrase(new Chunk("Quantite ", headerStyle)));
		qteCell.setBackgroundColor(Color.gray);
		qteCell.setPaddingBottom(5);
		table.addCell(qteCell);

		PdfPCell ptCell = new PdfPCell(cellStyle);
		ptCell.setPhrase(new Phrase(new Chunk("PA Total ", headerStyle)));
		ptCell.setBackgroundColor(Color.gray);
		ptCell.setPaddingBottom(5);
		table.addCell(ptCell);
		
		
		BigInteger totalQte =  BigInteger.ZERO;
		BigDecimal totalPrix = BigDecimal.ZERO;

		
Set<LigneCmdFournisseur> ligneCommande = commande.getLigneCommande();
	

	for (LigneCmdFournisseur line : ligneCommande) {	
			
         //cip
			PdfPCell inCipCell = new PdfPCell(cellBorderless);
			inCipCell.setPhrase(new Phrase(new Chunk(line.getCip(), boddyStyle)));
			inCipCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			inCipCell.setPaddingBottom(5);
			table.addCell(inCipCell);
			
		//Designation
			PdfPCell inDesCell = new PdfPCell(cellBorderless);
			inDesCell.setPhrase(new Phrase(new Chunk(line.getDesignation(), boddyStyle)));
			table.addCell(inDesCell);
			
		
				// prix Achat
				PdfPCell inPaCell = new PdfPCell(cellBorderless);
				inPaCell.setPhrase(new Phrase(new Chunk(""+line.getPrixAchatMin().longValue(), boddyStyle)));
				inPaCell.setPaddingBottom(5);
				table.addCell(inPaCell);

				// qte
				PdfPCell inqteCell = new PdfPCell(cellBorderless);
				inqteCell.setPhrase(new Phrase(new Chunk(""+line.getQuantiteCommande(), boddyStyle)));
				inqteCell.setPaddingBottom(5);
				table.addCell(inqteCell);
				totalQte= totalQte.add(line.getQuantiteCommande());
					
				// prix total
				PdfPCell inPtCell = new PdfPCell(cellBorderless);
				inPtCell.setPhrase(new Phrase(new Chunk(""+line.getPrixAchatTotal().longValue(), boddyStyle)));
				inPtCell.setPaddingBottom(5);
				table.addCell(inPtCell);
				totalPrix= totalPrix .add(line.getPrixAchatTotal());
			
				
		}

	
		
	cellStyle.setBorderWidth(0);

	PdfPCell videCell2 = new PdfPCell(cellStyle);
	videCell2.setPhrase(new Phrase(new Chunk("", boddyStyle)));
	videCell2.setBackgroundColor(Color.gray);
	videCell2.setPaddingBottom(5);
	table.addCell(videCell2 );
	
	
	
	PdfPCell cipCell2 = new PdfPCell(cellStyle);
	cipCell2.setPhrase(new Phrase(new Chunk("TOTAUX :", headerStyle)));
	cipCell2.setBackgroundColor(Color.gray);
	cipCell2.setBorder(0);
	cipCell2.setPaddingBottom(5);
	cipCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	table.addCell(cipCell2);
	

	PdfPCell pvCell1 = new PdfPCell(cellStyle);
	pvCell1.setPhrase(new Phrase(new Chunk("", headerStyle)));
	pvCell1.setBackgroundColor(Color.gray);
	pvCell1.setPaddingBottom(5);

	table.addCell(pvCell1);
	
	PdfPCell datePrmCell1 = new PdfPCell(cellStyle);
	datePrmCell1.setPhrase(new Phrase(new Chunk(""+totalQte, headerStyle)));
	datePrmCell1.setBackgroundColor(Color.gray);
	datePrmCell1.setPaddingBottom(5);

	table.addCell(datePrmCell1);
	
	PdfPCell paCell1 = new PdfPCell(cellStyle);
	paCell1.setPhrase(new Phrase(new Chunk(""+totalPrix.longValue(), headerStyle)));
	paCell1.setBackgroundColor(Color.gray);
	paCell1.setPaddingBottom(5);

	table.addCell(paCell1);
     document.add(table);
		
		
	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,
			Document document, HttpServletRequest request) {
		CommandeFournisseur commande = (CommandeFournisseur) model.get("commande");

		document.setPageSize(PageSize.A4);
		document.setMargins(30, 30, 50, 50);
		Font boddyStyle = new Font(Font.COURIER,8);

		HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " Page" , boddyStyle)), true);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);
		HeaderFooter header = null ;
       	 header	= new HeaderFooter(new Phrase(new Chunk("BON "+commande, boddyStyle)), false);
 	
		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);
		
		super.buildPdfMetadata(model, document, request);	}
	}
