package org.adorsys.adpharma.beans;

import java.awt.Color;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.EtatCredits;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("facturePayeur")
public class FacturePayeur extends   AbstractPdfView {


	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		EtatCredits etatCredits = (EtatCredits) model.get("etatCredits");
		Site site = Site.findSite(Long.valueOf(1));
		Font headerStyle = new Font(Font.COURIER,9);
		headerStyle.setStyle("bold");
		Font boddyStyle = new Font(Font.COURIER,9);
		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.5f);
		cellStyle.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cellBorderlessStyle = new PdfPCell(cellStyle);
		cellBorderlessStyle.setBorderWidth(0);
		

		PdfPCell cellBorderless = new PdfPCell(cellStyle);
		//cellBorderless.setBorderWidthBottom(0);
		//cellBorderless.setBorderWidthTop(0);
		
		PdfPCell cellBorderlesstop = new PdfPCell(cellStyle);
		cellBorderlesstop.setBorderWidthBottom(0);

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
		PdfPTable adressTable = new PdfPTable(2);
		adressTable.setWidthPercentage(100);
		PdfPCell senderCell = new PdfPCell(cellBorderlessStyle);
		senderCell.setPhrase(new Phrase(new Chunk(site.getDisplayName().toUpperCase(), headerStyle)));
		senderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell emptyCell = new PdfPCell(cellBorderlessStyle);
		adressTable.addCell(senderCell);// 1:2
		adressTable.addCell(emptyCell);// 1:1
		// Invoice company
		PdfPCell addCell1 = new PdfPCell(cellBorderlessStyle);
		addCell1.setPhrase(new Phrase(new Chunk(site.getSiteManager().toUpperCase(), headerStyle)));
		addCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(addCell1);// 1:2
		
		PdfPCell etatNumberCell = new PdfPCell(cellBorderlessStyle);
		etatNumberCell.setPhrase(new Phrase(new Chunk("RELEVE NO :"+etatCredits, headerStyle)));
		etatNumberCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(etatNumberCell);// 1:2
		PdfPCell addCell2 = new PdfPCell(cellBorderlessStyle);
		
		addCell2.setPhrase(new Phrase(new Chunk(site.getAdresse(), boddyStyle)));
		addCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(addCell2);
		adressTable.addCell(emptyCell);// 1:2
		
		PdfPCell telCell = new PdfPCell(cellBorderlessStyle);
		telCell.setPhrase(new Phrase(new Chunk("Tel: "+site.getPhone() +" Fax : "+ site.getFax(), boddyStyle)));
		telCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(telCell);
		
		PdfPCell clientCell = new PdfPCell(cellBorderlessStyle);
		clientCell.setPhrase(new Phrase(new Chunk("Doit : "+ etatCredits.getClient().toString(), headerStyle)));
		clientCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(clientCell);
		
		PdfPCell emailCell = new PdfPCell(cellBorderlessStyle);
		emailCell.setPhrase(new Phrase(new Chunk("Email : "+site.getEmail(), boddyStyle)));
		emailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(emailCell);
		adressTable.addCell(emptyCell);// 1:2
		
		PdfPCell regCell = new PdfPCell(cellBorderlessStyle);
		regCell.setPhrase(new Phrase(new Chunk("No Contribuable : "+site.getNumeroRegistre(), boddyStyle)));
		regCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(regCell);
		
		PdfPCell daterCell = new PdfPCell(cellBorderlessStyle);
		daterCell.setPhrase(new Phrase(new Chunk("", headerStyle)));
		daterCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(daterCell);
		adressTable.setSpacingAfter(15);
		document.add(adressTable);
				
		PdfPTable spaceTable = new PdfPTable(1);
		spaceTable.addCell(emptyCell);
		spaceTable.setSpacingAfter(5);
		document.add(spaceTable);
		
		// la table d'approvisionement
		float[] colWidths = {4.5f, 2f,1.7f, 1.5f,1.5f, 1f};
		PdfPTable table = new PdfPTable(colWidths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setHeaderRows(1);
		headerStyle.setColor(Color.BLUE);

		

		PdfPCell cipMCell = new PdfPCell(cellStyle);
		cipMCell.setPhrase(new Phrase(new Chunk("Employes", headerStyle)));
		cipMCell.setPaddingBottom(5);
		table.addCell(cipMCell );
		
		
		PdfPCell cipCell = new PdfPCell(cellStyle);
		cipCell.setPhrase(new Phrase(new Chunk("Date", headerStyle)));
		cipCell.setPaddingBottom(5);
	
		table.addCell(cipCell);
		
		PdfPCell desCell = new PdfPCell(cellStyle);
		desCell.setPhrase(new Phrase(new Chunk("Part Assure", headerStyle)));
		desCell.setPaddingBottom(5);
	
		table.addCell(desCell);
		
		PdfPCell pvCell = new PdfPCell(cellStyle);
		pvCell.setPhrase(new Phrase(new Chunk("Part Assureur", headerStyle)));
		pvCell.setPaddingBottom(5);
	
		table.addCell(pvCell);


		PdfPCell paCell = new PdfPCell(cellStyle);
		paCell.setPhrase(new Phrase(new Chunk("Total", headerStyle)));
		paCell.setPaddingBottom(5);
	
		table.addCell(paCell);
		
		PdfPCell napCell = new PdfPCell(cellStyle);
		napCell.setPhrase(new Phrase(new Chunk("Taux", headerStyle)));
		napCell.setPaddingBottom(5);
	
		table.addCell(napCell);
		
		 List<DetteClient> listeDettes = etatCredits.getListeDettes();
                
	

	for (DetteClient line : listeDettes) {	
		
		if (!line.getAnnuler()) {
			
		//nom et prenom
		PdfPCell inCipm = new PdfPCell(cellBorderless);
		inCipm.setPhrase(new Phrase(new Chunk(line.getAssurer(), boddyStyle)));
		inCipm.setPaddingBottom(2);
		inCipm.setHorizontalAlignment(Element.ALIGN_LEFT);

			table.addCell(inCipm);
         	
		//Numero du bon 
			PdfPCell inDesCell = new PdfPCell(cellBorderless);
			inDesCell.setPhrase(new Phrase(new Chunk(PharmaDateUtil.format(line.getDateCreation(), PharmaDateUtil.DATETIME_PATTERN_LONG), boddyStyle)));
			inDesCell.setPaddingBottom(2);
			inDesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(inDesCell);
			
		// taux
				PdfPCell inPvCell = new PdfPCell(cellBorderless);
				inPvCell.setPhrase(new Phrase(new Chunk(line.getPartAssure().toString(), boddyStyle)));
				inPvCell.setPaddingBottom(2);
				inPvCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(inPvCell);
				
		// sous total
				
				PdfPCell napCell1 = new PdfPCell(cellBorderless);
				napCell1.setPhrase(new Phrase(new Chunk(line.getMontantInitial().toString(), boddyStyle)));
				napCell1.setPaddingBottom(2);
				napCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(napCell1);
				
		 // total
				PdfPCell inPaCell = new PdfPCell(cellBorderless);
				inPaCell.setPhrase(new Phrase(new Chunk(line.getSousTotal().toString(), boddyStyle)));
				inPaCell.setPaddingBottom(2);
				inPaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(inPaCell);
				
				
				// net
				PdfPCell snapCell1 = new PdfPCell(cellBorderless);
				snapCell1.setPhrase(new Phrase(new Chunk(""+line.getTauxAssure()+"%", boddyStyle)));
				snapCell1.setPaddingBottom(5);
				snapCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(snapCell1);
		}	
		}
		
				RuleBasedNumberFormat rbnf = new RuleBasedNumberFormat(Locale.FRANCE, RuleBasedNumberFormat.SPELLOUT);
				 
		document.add(table);
		headerStyle.setColor(Color.black);
		document.add(new Paragraph(new Phrase(new Chunk("\n\n Arrete la facture a la somme de  :"+rbnf.format(etatCredits.getReste().intValue()).replace("-", " ").toUpperCase()+" FRANCS CFA", headerStyle))));
		
	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,
			
	    Document document, HttpServletRequest request) {
		document.setPageSize(PageSize.A4);
		document.setMargins(10,10, 10, 10);
		EtatCredits etatCredits = (EtatCredits) model.get("etatCredits");
		Font headerStyle = new Font(Font.COURIER,8);
		headerStyle.setStyle("bold");
		HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " - Page " , headerStyle)), true);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);
		HeaderFooter header = null ;
        
       	 header	= new HeaderFooter(new Phrase(new Chunk("FACTURE GLOBALE " , headerStyle)), false);
	
		
		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);
		
		super.buildPdfMetadata(model, document, request);
	}
}
