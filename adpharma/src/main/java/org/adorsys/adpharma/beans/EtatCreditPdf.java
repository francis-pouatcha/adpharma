package org.adorsys.adpharma.beans;

import java.awt.Color;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.DetteClient;
import org.adorsys.adpharma.domain.EtatCredits;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.LigneFacture;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeFacture;
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

@Component("etatCreditPdf")
public class EtatCreditPdf  extends   AbstractPdfView {
	
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
		cellBorderless.setBorderWidthBottom(0);
		cellBorderless.setBorderWidthTop(0);
		
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
		daterCell.setPhrase(new Phrase(new Chunk("A regler avant le  : "+ PharmaDateUtil.format(etatCredits.getDatePaiement(), "dd-MM-yyy HH:mm"), headerStyle)));
		daterCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(daterCell);
		adressTable.setSpacingAfter(15);
		document.add(adressTable);
				
		PdfPTable spaceTable = new PdfPTable(1);
		spaceTable.addCell(emptyCell);
		spaceTable.setSpacingAfter(5);
		document.add(spaceTable);
		
		// la table d'approvisionement
		float[] colWidths = {1.5f, 4.5f,1.7f, 2f,1.5f, 1.5f};
		PdfPTable table = new PdfPTable(colWidths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setHeaderRows(1);
		headerStyle.setColor(Color.WHITE);

		

		PdfPCell cipMCell = new PdfPCell(cellStyle);
		cipMCell.setPhrase(new Phrase(new Chunk("Bon Cmd", headerStyle)));
		cipMCell.setPaddingBottom(5);
		table.addCell(cipMCell );
		
		
		PdfPCell cipCell = new PdfPCell(cellStyle);
		cipCell.setPhrase(new Phrase(new Chunk("Code Clt", headerStyle)));
		cipCell.setPaddingBottom(5);
	
		//table.addCell(cipCell);
		
		PdfPCell desCell = new PdfPCell(cellStyle);
		desCell.setPhrase(new Phrase(new Chunk("Nom Client", headerStyle)));
		desCell.setPaddingBottom(5);
	
		table.addCell(desCell);
		
		PdfPCell pvCell = new PdfPCell(cellStyle);
		pvCell.setPhrase(new Phrase(new Chunk("Date Credit", headerStyle)));
		pvCell.setPaddingBottom(5);
	
		table.addCell(pvCell);


		PdfPCell paCell = new PdfPCell(cellStyle);
		paCell.setPhrase(new Phrase(new Chunk("Montant", headerStyle)));
		paCell.setPaddingBottom(5);
	
		table.addCell(paCell);
		
		PdfPCell napCell = new PdfPCell(cellStyle);
		napCell.setPhrase(new Phrase(new Chunk("Avance", headerStyle)));
		napCell.setPaddingBottom(5);
	
		table.addCell(napCell);
		
		PdfPCell stCell = new PdfPCell(cellStyle);
		stCell.setPhrase(new Phrase(new Chunk("Reste", headerStyle)));
		stCell.setPaddingBottom(5);
	
		table.addCell(stCell);
		
		 List<DetteClient> listeDettes = etatCredits.getListeDettes();
                
	

	for (DetteClient line : listeDettes) {	
		
		if (!line.getAnnuler()) {
			
		//bon cmd
		PdfPCell inCipm = new PdfPCell(cellBorderless);
		inCipm.setPhrase(new Phrase(new Chunk(line.getFactureNo(), boddyStyle)));
		inCipm.setPaddingBottom(2);

			table.addCell(inCipm);
		Facture next = Facture.findFacturesByFactureNumberEquals(line.getFactureNo()).getResultList().iterator().next();
         //code client
			PdfPCell inCipCell = new PdfPCell(cellBorderless);
			inCipCell.setPhrase(new Phrase(new Chunk(next.getClient().getClientNumber(), boddyStyle)));
			inCipCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			inCipCell.setPaddingBottom(2);
		//	table.addCell(inCipCell);
			
		//client Name
			PdfPCell inDesCell = new PdfPCell(cellBorderless);
			inDesCell.setPhrase(new Phrase(new Chunk(next.getClient().getNomComplet(), boddyStyle)));
			inDesCell.setPaddingBottom(2);
			inDesCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(inDesCell);
			
		// Date Credit
				PdfPCell inPvCell = new PdfPCell(cellBorderless);
				inPvCell.setPhrase(new Phrase(new Chunk(PharmaDateUtil.format(line.getDateCreation(), "dd-MM-yyyy hh:mm"), boddyStyle)));
				inPvCell.setPaddingBottom(2);
				inPvCell.setHorizontalAlignment(Element.ALIGN_LEFT);

				table.addCell(inPvCell);
				
				// montant
				PdfPCell napCell1 = new PdfPCell(cellBorderless);
				napCell1.setPhrase(new Phrase(new Chunk(line.getMontantInitial().toString(), boddyStyle)));
				napCell1.setPaddingBottom(2);
				napCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(napCell1);
				
		      // avance
				PdfPCell inPaCell = new PdfPCell(cellBorderless);
				inPaCell.setPhrase(new Phrase(new Chunk(line.getAvance().toString(), boddyStyle)));
				inPaCell.setPaddingBottom(2);
				inPaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(inPaCell);
				
				
				// Reste
				PdfPCell snapCell1 = new PdfPCell(cellBorderless);
				snapCell1.setPhrase(new Phrase(new Chunk(line.getReste().toString(), boddyStyle)));
				snapCell1.setPaddingBottom(5);
				snapCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(snapCell1);
		}	
		}
	headerStyle.setColor(Color.BLACK);
	PdfPCell vide = new PdfPCell(cellBorderless);
	vide.setPhrase(new Phrase(new Chunk("", boddyStyle)));
	
	PdfPCell vide2 = new PdfPCell(cellBordertop);
	vide2.setPhrase(new Phrase(new Chunk("", boddyStyle)));

	    // table.addCell(vide);
	     table.addCell(vide);
	     table.addCell(vide);
	     table.addCell(vide);
		// total row
		PdfPCell totalLabel = new PdfPCell(cellBorderlesstop);
		totalLabel.setPhrase(new Phrase(new Chunk("Total Brut :", headerStyle)));
		totalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
		totalLabel.setPaddingBottom(5);
		totalLabel.setColspan(2);
		table.addCell(totalLabel);
		// montant ht
		PdfPCell basicAmountCell = new PdfPCell(cellBorderlesstop);
		basicAmountCell.setPhrase(new Phrase(new Chunk(etatCredits.getMontantInitial().intValue()+"", headerStyle)));
		basicAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

		table.addCell(basicAmountCell);
		
		// table.addCell(vide);
	     table.addCell(vide);
	     table.addCell(vide);
	     table.addCell(vide);
			// total row
			PdfPCell totalAvoir = new PdfPCell(cellBorderless);
			totalAvoir.setPhrase(new Phrase(new Chunk("Total Avoir : ", headerStyle)));
			totalAvoir.setHorizontalAlignment(Element.ALIGN_LEFT);
			totalAvoir.setPaddingBottom(5);
			totalAvoir.setColspan(2);
			table.addCell(totalAvoir);
			
			// montant total avoir 
						PdfPCell avoir = new PdfPCell(cellBorderless);
						avoir.setPhrase(new Phrase(new Chunk(etatCredits.getMontantAvoir().intValue()+"", headerStyle)));
						avoir.setHorizontalAlignment(Element.ALIGN_RIGHT);
						table.addCell(avoir);

			 
			// table.addCell(vide);
		     table.addCell(vide);
		     table.addCell(vide);
		     table.addCell(vide);
				// total row
				PdfPCell totalAvance = new PdfPCell(cellBorderless);
				totalAvance.setPhrase(new Phrase(new Chunk("Total Avance : ", headerStyle)));
				totalAvance.setHorizontalAlignment(Element.ALIGN_LEFT);
				totalAvance.setPaddingBottom(5);
				totalAvance.setColspan(2);
				table.addCell(totalAvance);

			// montant total avance 
			PdfPCell remise = new PdfPCell(cellBorderless);
			remise.setPhrase(new Phrase(new Chunk(etatCredits.getAvance().intValue()+"", headerStyle)));
			remise.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(remise);

			// total row
			// table.addCell(vide2);
		     table.addCell(vide2);
		     table.addCell(vide2);
		     table.addCell(vide2);
			 
				PdfPCell total = new PdfPCell(cellBordertop);
				total.setPhrase(new Phrase(new Chunk("NET a PAYER : ", headerStyle)));
				total.setHorizontalAlignment(Element.ALIGN_LEFT);
				total.setPaddingBottom(5);
				total.setColspan(2);
				table.addCell(total);
				// montant total remise 
				PdfPCell motantTc= new PdfPCell(cellBordertop);
				motantTc.setPhrase(new Phrase(new Chunk(""+etatCredits.getReste().intValue(), headerStyle)));
				motantTc.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(motantTc);
				
				RuleBasedNumberFormat rbnf = new RuleBasedNumberFormat(Locale.FRANCE, RuleBasedNumberFormat.SPELLOUT);
				 
		document.add(table);
		document.add(new Paragraph(new Phrase(new Chunk("Arrete la facture a la somme de  :"+rbnf.format(etatCredits.getReste().intValue()).replace("-", " ").toUpperCase()+" FRANCS CFA", headerStyle))));
		
	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,
			
	    Document document, HttpServletRequest request) {
		document.setPageSize(PageSize.A4);
		document.setMargins(5, 5, 10, 10);
		EtatCredits etatCredits = (EtatCredits) model.get("etatCredits");
		Font headerStyle = new Font(Font.COURIER,8);
		headerStyle.setStyle("bold");

		HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " - Page " , headerStyle)), true);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);
		HeaderFooter header = null ;
        
       	 header	= new HeaderFooter(new Phrase(new Chunk("RELEVE DES DETTES " , headerStyle)), false);
	
		
		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);
		
		super.buildPdfMetadata(model, document, request);
	}

}
