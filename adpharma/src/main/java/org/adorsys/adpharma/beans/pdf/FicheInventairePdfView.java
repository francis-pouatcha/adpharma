package org.adorsys.adpharma.beans.pdf;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Rayon;
import org.adorsys.adpharma.domain.Site;
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


@Component("ficheInventairePdfView")
public class FicheInventairePdfView  extends   AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Site site = Site.findSite(new Long(1));

		Font headerStyle = new Font(Font.COURIER,8);
		headerStyle.setStyle("bold");
		Font boddyStyle = new Font(Font.COURIER,7);
		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.1f);
		cellStyle.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cellBorderlessStyle = new PdfPCell(cellStyle);
		cellBorderlessStyle.setBorderWidth(0);
		

		PdfPCell cellBorderless = new PdfPCell(cellStyle);
		cellBorderless.setBorderWidth(0);
        cellBorderless.setBorderWidthBottom(.2f);
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
		float[] adColumnsWith = {2f, 2.4f, 3.2f};
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
		telCell.setPhrase(new Phrase(new Chunk("Tel: "+site.getPhone()+  "Fax :" +site.getFax(), boddyStyle)));
		telCell.setColspan(2);
		telCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(telCell);
		adressTable.addCell(emptyCell);// 1:2
		
		PdfPCell emailCell = new PdfPCell(cellBorderlessStyle);
		emailCell.setPhrase(new Phrase(new Chunk("Email : "+site.getEmail(), boddyStyle)));
		emailCell.setColspan(2);
		emailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(emailCell);
		
		PdfPCell agentCell = new PdfPCell(cellBorderlessStyle);
		agentCell.setPhrase(new Phrase("Imprime Par : "+SecurityUtil.getPharmaUser().getFullName(), headerStyle));
		agentCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

		adressTable.addCell(agentCell);// 1:2
		
		document.add(adressTable);
		PdfPTable adressTable1 = new PdfPTable(adColumnsWith);
		adressTable1.setWidthPercentage(100);
		
		document.add(adressTable1);
				
		PdfPTable spaceTable = new PdfPTable(1);
		spaceTable.addCell(emptyCell);
		spaceTable.setSpacingAfter(2);
		document.add(spaceTable);
		
		// la table d'approvisionement
		float[] colWidths = {1.5f,4f, 1.5f, 1.5f, 1.5f};
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

		
		 List<Produit> produits  = ( List<Produit> ) model.get("produits");


	for (Produit line : produits) {	
		
		//cip
		PdfPCell inCipm = new PdfPCell(cellBorderless);
		inCipm.setPhrase(new Phrase(new Chunk(line.getCip(), boddyStyle)));
		inCipm.setPaddingBottom(2);
			table.addCell(inCipm);
			
         //designation
			PdfPCell inCipCell = new PdfPCell(cellBorderless);
			inCipCell.setPhrase(new Phrase(new Chunk(line.getDesignation().toUpperCase(), boddyStyle)));
			inCipCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			inCipCell.setPaddingBottom(2);
			cipCell.setPaddingLeft(10);
		
			
			table.addCell(inCipCell);
			
		//qte en stock
			PdfPCell inDesCell = new PdfPCell(cellBorderless);
			inDesCell.setPhrase(new Phrase(new Chunk(line.getQuantiteEnStock().toString(), boddyStyle)));
			totalFichier = totalFichier.add(line.getQuantiteEnStock()) ;
			table.addCell(inDesCell);
			
			// 
			PdfPCell inprmCell = new PdfPCell(cellBorderless);
			inprmCell.setPhrase(new Phrase(new Chunk("", boddyStyle)));
			inprmCell.setPaddingBottom(2);
			table.addCell(inprmCell);

				// prix Vente
				PdfPCell inPvCell = new PdfPCell(cellBorderless);
				inPvCell.setPhrase(new Phrase("", boddyStyle));
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
		datePrmCell1.setPhrase(new Phrase(new Chunk("--", headerStyle)));
		datePrmCell1.setBackgroundColor(Color.gray);
		datePrmCell1.setPaddingBottom(2);

		table.addCell(datePrmCell1);
		
		PdfPCell pvCell1 = new PdfPCell(cellStyle);
		pvCell1.setPhrase(new Phrase(new Chunk("--", headerStyle)));
		pvCell1.setBackgroundColor(Color.gray);
		pvCell1.setPaddingBottom(2);

		table.addCell(pvCell1);


	     document.add(table);
		
	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,
			
	    Document document, HttpServletRequest request) {
		document.setPageSize(PageSize.A4);
		document.setMargins(50, 30,30, 50);
		Long rayonId = (Long) model.get("rayonId");
		Font boddyStyle = new Font(Font.COURIER,8);
		boddyStyle.setStyle("bold");

		HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " Page" , boddyStyle)), true);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);
		HeaderFooter header = null ;
		if (rayonId!= null) {
	       	 header	= new HeaderFooter(new Phrase(new Chunk("FICHE INVENTAIRE RAYON :"+Rayon.findRayon(rayonId).toString().toUpperCase() +" Du :"+ PharmaDateUtil.format(new Date(), "dd-MM-yyyy HH:mm"), boddyStyle)), false);

		}else {
	       	 header	= new HeaderFooter(new Phrase(new Chunk("FICHE INVENTAIRE  Du :"+ PharmaDateUtil.format(new Date(), "dd-MM-yyyy HH:mm"), boddyStyle)), false);

		}
 	
		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);
		
		super.buildPdfMetadata(model, document, request);
	}
}
