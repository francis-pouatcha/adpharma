package org.adorsys.adpharma.beans;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.Approvisionement;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
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

@Component("ficheSuivieQtePdf")
public class FicheSuivieQtePdf extends   AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		List<Produit>  listeProduit = (List<Produit> ) model.get("listeProduit");
		String headTexte =  (String) model.get("headTexte");
		Site site = Site.findSite(new Long(1));

		Font headerStyle = new Font(Font.COURIER,11);
		headerStyle.setStyle("bold");
		Font boddyStyle = new Font(Font.COURIER,9);
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
		PdfPTable adressTable = new PdfPTable(1);
		adressTable.setWidthPercentage(100);
		PdfPCell senderCell = new PdfPCell(cellBorderlessStyle);
		senderCell.setPhrase(new Phrase(new Chunk(site.getDisplayName().toUpperCase(), headerStyle)));
		senderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell emptyCell = new PdfPCell(cellBorderlessStyle);
		adressTable.addCell(senderCell);// 1:2
		// Invoice company
		PdfPCell addCell1 = new PdfPCell(cellBorderlessStyle);
		addCell1.setPhrase(new Phrase(new Chunk(site.getSiteManager(), headerStyle)));
		addCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(addCell1);// 1:2

		PdfPCell addCell2 = new PdfPCell(cellBorderlessStyle);
		addCell2.setPhrase(new Phrase(new Chunk(site.getAdresse(), boddyStyle)));
		addCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		addCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(addCell2);
		PdfPCell telCell = new PdfPCell(cellBorderlessStyle);
		telCell.setPhrase(new Phrase(new Chunk("Tel: "+site.getPhone() +   "Fax : "+site.getFax(), boddyStyle)));
		telCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(telCell);
         adressTable.addCell(emptyCell);
		PdfPCell emailCell = new PdfPCell(cellBorderlessStyle);
		emailCell.setPhrase(new Phrase(new Chunk(headTexte.toUpperCase(), headerStyle)));
		emailCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		adressTable.addCell(emailCell);

		document.add(adressTable);
		PdfPTable adressTable1 = new PdfPTable(adColumnsWith);
		adressTable1.setWidthPercentage(100);

		document.add(adressTable1);

		PdfPTable spaceTable = new PdfPTable(1);
		spaceTable.addCell(emptyCell);
		spaceTable.setSpacingAfter(5);
		document.add(spaceTable);

		// la table d'approvisionement
		float[] colWidths = {1.3f,4.5f, 1.1f, 1.1f, 1.1f, 1.4f, 1.4f};
		PdfPTable table = new PdfPTable(colWidths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setHeaderRows(1);

		headerStyle.setColor(Color.WHITE);

		PdfPCell cipMCell = new PdfPCell(cellStyle);
		cipMCell.setPhrase(new Phrase(new Chunk("C.I.P", headerStyle)));
		cipMCell.setBackgroundColor(Color.gray);
		cipMCell.setPaddingBottom(2);
		table.addCell(cipMCell );

		PdfPCell cipCell = new PdfPCell(cellStyle);
		cipCell.setPhrase(new Phrase(new Chunk("DESIGNATION", headerStyle)));
		cipCell.setBackgroundColor(Color.gray);
		cipCell.setPaddingBottom(2);
		table.addCell(cipCell );



		PdfPCell datePrmCell = new PdfPCell(cellStyle);
		datePrmCell.setPhrase(new Phrase(new Chunk("QTE STOCK", headerStyle)));
		datePrmCell.setBackgroundColor(Color.gray);
		datePrmCell.setPaddingBottom(2);

		table.addCell(datePrmCell);


		PdfPCell paCell = new PdfPCell(cellStyle);
		paCell.setPhrase(new Phrase(new Chunk("QTE VENDUE ", headerStyle)));
		paCell.setBackgroundColor(Color.gray);
		paCell.setPaddingBottom(2);

		table.addCell(paCell);

		PdfPCell qteCell = new PdfPCell(cellStyle);
		qteCell.setPhrase(new Phrase(new Chunk("QTE CMD ", headerStyle)));
		qteCell.setBackgroundColor(Color.gray);
		qteCell.setPaddingBottom(2);
		table.addCell(qteCell);
		
		PdfPCell desCell = new PdfPCell(cellStyle);
		desCell.setPhrase(new Phrase(new Chunk("RAYON", headerStyle)));
		desCell.setBackgroundColor(Color.gray);
		desCell.setPaddingBottom(2);

		table.addCell(desCell);
		
		PdfPCell filCell = new PdfPCell(cellStyle);
		filCell.setPhrase(new Phrase(new Chunk("FILIALE", headerStyle)));
		filCell.setBackgroundColor(Color.gray);
		filCell.setPaddingBottom(2);

		table.addCell(filCell);

/*
		PdfPCell pvCell = new PdfPCell(cellStyle);
		pvCell.setPhrase(new Phrase(new Chunk("PV Unit ", headerStyle)));
		pvCell.setBackgroundColor(Color.gray);
		pvCell.setPaddingBottom(2);

		table.addCell(pvCell);


		PdfPCell ptCell = new PdfPCell(cellStyle);
		ptCell.setPhrase(new Phrase(new Chunk("PA Total ", headerStyle)));
		ptCell.setBackgroundColor(Color.gray);
		ptCell.setPaddingBottom(2);
		table.addCell(ptCell);*/


		/*BigDecimal totalVente = BigDecimal.ZERO;
		BigDecimal totalAchat = BigDecimal.ZERO;
		BigInteger totalQte =  BigInteger.ZERO;
		BigDecimal totalPrix = BigDecimal.ZERO;*/

		if (!listeProduit.isEmpty()) {
	
		for (Produit line : listeProduit) {	

			//cipm
			PdfPCell inCipm = new PdfPCell(cellBorderless);
			inCipm.setPhrase(new Phrase(new Chunk(line.getCip(), boddyStyle)));
			inCipm.setPaddingBottom(2);
			table.addCell(inCipm);

			//cip
			PdfPCell inCipCell = new PdfPCell(cellBorderless);
			inCipCell.setPhrase(new Phrase(new Chunk(line.getDesignation(), boddyStyle)));
			inCipCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			inCipCell.setPaddingBottom(2);

			table.addCell(inCipCell);

			
			
			PdfPCell inPaCell = new PdfPCell(cellBorderless);
			inPaCell.setPhrase(new Phrase(new Chunk(""+line.getQuantiteEnStock(), boddyStyle)));
			//totalAchat = totalAchat.add(line.getPrixAchatUnitaire()) ;
			inPaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			inPaCell.setPaddingBottom(2);
			table.addCell(inPaCell);

			// qte
			PdfPCell inqteCell = new PdfPCell(cellBorderless);
			inqteCell.setPhrase(new Phrase(new Chunk(""+line.getQtevendu(), boddyStyle)));
			inqteCell.setPaddingBottom(2);
			inqteCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(inqteCell);
			//totalQte= totalQte.add(line.getQuantiteAprovisione());

			// prix vente
			PdfPCell inPvCell = new PdfPCell(cellBorderless);
			inPvCell.setPhrase(new Phrase(new Chunk(" ", boddyStyle)));
			inPvCell.setPaddingBottom(2);
			inPvCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table.addCell(inPvCell);
			
			//Designation
			PdfPCell inDesCell = new PdfPCell(cellBorderless);
			inDesCell.setPhrase(new Phrase(new Chunk(line.getRayon().getCodeRayon(), boddyStyle)));
			inDesCell.setPaddingBottom(2);
			inDesCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(inDesCell);

			//Designation
			PdfPCell infilCell = new PdfPCell(cellBorderless);
			infilCell.setPhrase(new Phrase(new Chunk(line.getFiliale().getLibelle(), boddyStyle)));
			infilCell.setPaddingBottom(2);
			infilCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(infilCell);

			//totalVente= totalVente .add(line.getPrixVenteUnitaire());

			// prix total
			/*PdfPCell inPtCell = new PdfPCell(cellBorderless);
			inPtCell.setPhrase(new Phrase(new Chunk(""+line.getPrixAchatTotal().longValue(), boddyStyle)));
			inPtCell.setPaddingBottom(2);
			inPtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table.addCell(inPtCell);
			totalPrix= totalPrix .add(line.getPrixAchatTotal());*/


		}
		
	}
		/*cellStyle.setBorderWidth(0);
		PdfPCell cipMCell1 = new PdfPCell(cellStyle);
		cipMCell1.setPhrase(new Phrase(new Chunk("", boddyStyle)));
		cipMCell1.setBackgroundColor(Color.gray);
		cipMCell1.setPaddingBottom(2);
		table.addCell(cipMCell1 );

		PdfPCell videCell1 = new PdfPCell(cellStyle);
		videCell1.setPhrase(new Phrase(new Chunk("", boddyStyle)));
		videCell1.setBackgroundColor(Color.gray);
		videCell1.setPaddingBottom(2);
		table.addCell(videCell1 );


		PdfPCell videCell2 = new PdfPCell(cellStyle);
		videCell2.setPhrase(new Phrase(new Chunk("", boddyStyle)));
		videCell2.setBackgroundColor(Color.gray);
		videCell2.setPaddingBottom(2);
		table.addCell(videCell2 );



		PdfPCell cipCell2 = new PdfPCell(cellStyle);
		cipCell2.setPhrase(new Phrase(new Chunk("TOTAUX :", headerStyle)));
		cipCell2.setBackgroundColor(Color.gray);
		cipCell2.setBorder(0);
		cipCell2.setPaddingBottom(2);
		cipCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cipCell2);

		PdfPCell desCell1 = new PdfPCell(cellStyle);
		desCell1.setPhrase(new Phrase(new Chunk(""+totalAchat.longValue(), headerStyle)));
		desCell1.setBackgroundColor(Color.gray);
		desCell1.setPaddingBottom(2);
		desCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);


		table.addCell(desCell1);


		PdfPCell datePrmCell1 = new PdfPCell(cellStyle);
		datePrmCell1.setPhrase(new Phrase(new Chunk(""+totalQte, headerStyle)));
		datePrmCell1.setBackgroundColor(Color.gray);
		datePrmCell1.setPaddingBottom(2);

		table.addCell(datePrmCell1);

		PdfPCell pvCell1 = new PdfPCell(cellStyle);
		pvCell1.setPhrase(new Phrase(new Chunk(""+totalVente.longValue(), headerStyle)));
		pvCell1.setBackgroundColor(Color.gray);
		pvCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

		pvCell1.setPaddingBottom(2);

		table.addCell(pvCell1);


		PdfPCell paCell1 = new PdfPCell(cellStyle);
		paCell1.setPhrase(new Phrase(new Chunk(""+totalPrix.longValue(), headerStyle)));
		paCell1.setBackgroundColor(Color.gray);
		paCell1.setPaddingBottom(2);

		table.addCell(paCell1);*/
		document.add(table);

	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,

			Document document, HttpServletRequest request) {
		document.setPageSize(PageSize.A4);
		document.setMargins(10, 10, 5, 5);
		Font boddyStyle = new Font(Font.COURIER,8);

		HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " Page" , boddyStyle)), true);
		footer.setAlignment(Element.ALIGN_CENTER);
		//document.setFooter(footer);

		super.buildPdfMetadata(model, document, request);
	}

	
	

}
