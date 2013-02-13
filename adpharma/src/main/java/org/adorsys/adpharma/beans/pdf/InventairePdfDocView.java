package org.adorsys.adpharma.beans.pdf;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneFacture;
import org.adorsys.adpharma.domain.LigneInventaire;
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
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("inventairePdfDocView")

public class InventairePdfDocView extends   AbstractPdfView  {

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Inventaire inventaire = (Inventaire) model.get("inventaire");
		Site site = Site.findSite(new Long(1));
		Font headerStyle = new Font(Font.COURIER,8);
		headerStyle.setStyle("bold");
		Font boddyStyle = new Font(Font.COURIER,7);
		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.3f);
		cellStyle.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cellBorderlessStyle = new PdfPCell(cellStyle);
		cellBorderlessStyle.setBorderWidth(0);
		

		PdfPCell cellBorderless = new PdfPCell(cellStyle);
		cellBorderless.setBorderWidth(0);
        cellBorderless.setBorderWidthBottom(.3f);
        cellBorderless.setMinimumHeight(10);
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
		telCell.setPhrase(new Phrase(new Chunk("TEL : "+site.getPhone() + " FAX : "+site.getFax(), boddyStyle)));
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
		float[] colWidths = {1.5f,3.7f, 1.4f, 1.4f, 1.4f};
		PdfPTable table = new PdfPTable(colWidths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setHeaderRows(1);
		
		headerStyle.setColor(Color.WHITE);

		PdfPCell cipMCell = new PdfPCell(cellStyle);
		cipMCell.setPhrase(new Phrase(new Chunk("code Produit", headerStyle)));
		cipMCell.setBackgroundColor(Color.gray);
		cipMCell.setPaddingBottom(2);
		table.addCell(cipMCell );
		
		
		PdfPCell cipCell = new PdfPCell(cellStyle);
		cipCell.setPhrase(new Phrase(new Chunk("Designation", headerStyle)));
		cipCell.setBackgroundColor(Color.gray);
		cipCell.setPaddingBottom(2);

		table.addCell(cipCell);
		
		PdfPCell desCell = new PdfPCell(cellStyle);
		desCell.setPhrase(new Phrase(new Chunk("Qte Fichier", headerStyle)));
		desCell.setBackgroundColor(Color.gray);
		desCell.setPaddingBottom(2);

		table.addCell(desCell);
		

		PdfPCell datePrmCell = new PdfPCell(cellStyle);
		datePrmCell.setPhrase(new Phrase(new Chunk("Qte Reels", headerStyle)));
		datePrmCell.setBackgroundColor(Color.gray);
		datePrmCell.setPaddingBottom(2);

		table.addCell(datePrmCell);
		
		PdfPCell pvCell = new PdfPCell(cellStyle);
		pvCell.setPhrase(new Phrase(new Chunk("Ecart", headerStyle)));
		pvCell.setBackgroundColor(Color.gray);
		pvCell.setPaddingBottom(2);

		table.addCell(pvCell);


		
		
		BigInteger totalFichier = BigInteger.ZERO;
		BigInteger totalReel = BigInteger.ZERO;
		BigInteger ecart =  BigInteger.ZERO;
		BigDecimal totalPrix = BigDecimal.ZERO;

		


	 List<LigneInventaire> inventaires = LigneInventaire.findLigneInventairesByInventaire(inventaire).getResultList();

	for (LigneInventaire line : inventaires) {	
		
		//cipm
		PdfPCell inCipm = new PdfPCell(cellBorderless);
		inCipm.setPhrase(new Phrase(new Chunk(line.getProduit().getCip(), boddyStyle)));
		inCipm.setPaddingBottom(2);
			table.addCell(inCipm);
			
         //cip
			PdfPCell inCipCell = new PdfPCell(cellBorderless);
			inCipCell.setPhrase(new Phrase(new Chunk(line.getProduit().getDesignation(), boddyStyle)));
			inCipCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			inCipCell.setPaddingBottom(2);
			table.addCell(inCipCell);
			
		//Designation
			PdfPCell inDesCell = new PdfPCell(cellBorderless);
			inDesCell.setPhrase(new Phrase(new Chunk(line.getQteEnStock().toString(), boddyStyle)));
			totalFichier = totalFichier.add(line.getQteEnStock()) ;
			table.addCell(inDesCell);
			
			// date Peremption
			PdfPCell inprmCell = new PdfPCell(cellBorderless);
			inprmCell.setPhrase(new Phrase(new Chunk(line.getQteReel().toString(), boddyStyle)));
			totalReel= totalReel.add(line.getQteReel());
			inprmCell.setPaddingBottom(2);
			table.addCell(inprmCell);

				// prix Vente
				PdfPCell inPvCell = new PdfPCell(cellBorderless);
				inPvCell.setPhrase(new Phrase(new Chunk(line.getEcart().toString(), boddyStyle)));
				ecart= ecart.add(line.getEcart());
				inPvCell.setPaddingBottom(2);
				table.addCell(inPvCell);

				
		}
	cellStyle.setBorderWidth(0);
	 	PdfPCell cipMCell1 = new PdfPCell(cellStyle);
	 	cipMCell1.setPhrase(new Phrase(new Chunk("", boddyStyle)));
	 	cipMCell1.setBackgroundColor(Color.gray);
	 	cipMCell1.setPaddingBottom(2);
		
		table.addCell(cipMCell1 );
		
		
		PdfPCell cipCell2 = new PdfPCell(cellStyle);
		cipCell2.setPhrase(new Phrase(new Chunk("TOTAL :", headerStyle)));
		cipCell2.setBackgroundColor(Color.gray);
		cipCell2.setBorder(0);
		cipCell2.setPaddingBottom(2);
		cipCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cipCell2);
		
		PdfPCell desCell1 = new PdfPCell(cellStyle);
		desCell1.setPhrase(new Phrase(new Chunk(""+totalFichier, headerStyle)));
		desCell1.setBackgroundColor(Color.gray);
		desCell1.setPaddingBottom(2);

		table.addCell(desCell1);
		

		PdfPCell datePrmCell1 = new PdfPCell(cellStyle);
		datePrmCell1.setPhrase(new Phrase(new Chunk(""+totalReel, headerStyle)));
		datePrmCell1.setBackgroundColor(Color.gray);
		datePrmCell1.setPaddingBottom(2);

		table.addCell(datePrmCell1);
		
		PdfPCell pvCell1 = new PdfPCell(cellStyle);
		pvCell1.setPhrase(new Phrase(new Chunk(""+ecart, headerStyle)));
		pvCell1.setBackgroundColor(Color.gray);
		pvCell1.setPaddingBottom(2);

		table.addCell(pvCell1);


		PdfPCell paCell1 = new PdfPCell(cellStyle);
		paCell1.setPhrase(new Phrase(new Chunk(""+totalPrix.longValue(), headerStyle)));
		paCell1.setBackgroundColor(Color.gray);
		paCell1.setPaddingBottom(2);

		table.addCell(paCell1);
	     document.add(table);
		
	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,
			
	    Document document, HttpServletRequest request) {
		document.setPageSize(PageSize.A4);
		document.setMargins(50, 20, 20, 50);
		Inventaire inventaire = (Inventaire) model.get("inventaire");
		Font boddyStyle = new Font(Font.COURIER,8);
		boddyStyle.setStyle("bold");

		HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " Page" , boddyStyle)), true);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);
		HeaderFooter header = null ;
       	 header	= new HeaderFooter(new Phrase(new Chunk(""+inventaire.toString() , boddyStyle)), false);
 	
		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);
		
		super.buildPdfMetadata(model, document, request);
	}
	
}
