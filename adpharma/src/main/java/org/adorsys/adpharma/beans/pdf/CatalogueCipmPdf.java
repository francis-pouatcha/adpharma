package org.adorsys.adpharma.beans.pdf;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Produit;
import org.adorsys.adpharma.domain.Site;
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

@Component("catalogueCipmPdf")
public class CatalogueCipmPdf extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		List<LigneApprovisionement>  listeProduit = (List<LigneApprovisionement> ) model.get("catalogue");
		String headTexte =  "CATALOGUE CIPM";
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
		float[] colWidths = {1.1f, 1.8f, 4f,1.1f, 1.1f, 2.1f};
		PdfPTable table = new PdfPTable(colWidths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setHeaderRows(1);

		headerStyle.setColor(Color.WHITE);

		PdfPCell cipMCell = new PdfPCell(cellStyle);
		cipMCell.setPhrase(new Phrase(new Chunk("Cip", headerStyle)));
		cipMCell.setBackgroundColor(Color.gray);
		cipMCell.setPaddingBottom(2);
		table.addCell(cipMCell );

		PdfPCell cipCell = new PdfPCell(cellStyle);
		cipCell.setPhrase(new Phrase(new Chunk("Cipm", headerStyle)));
		cipCell.setBackgroundColor(Color.gray);
		cipCell.setPaddingBottom(2);
		table.addCell(cipCell );



		PdfPCell datePrmCell = new PdfPCell(cellStyle);
		datePrmCell.setPhrase(new Phrase(new Chunk("Designation", headerStyle)));
		datePrmCell.setBackgroundColor(Color.gray);
		datePrmCell.setPaddingBottom(2);

		table.addCell(datePrmCell);


		PdfPCell paCell = new PdfPCell(cellStyle);
		paCell.setPhrase(new Phrase(new Chunk("Quantite ", headerStyle)));
		paCell.setBackgroundColor(Color.gray);
		paCell.setPaddingBottom(2);

		table.addCell(paCell);

		PdfPCell qteCell = new PdfPCell(cellStyle);
		qteCell.setPhrase(new Phrase(new Chunk("Prix ", headerStyle)));
		qteCell.setBackgroundColor(Color.gray);
		qteCell.setPaddingBottom(2);
		table.addCell(qteCell);
		
		PdfPCell desCell = new PdfPCell(cellStyle);
		desCell.setPhrase(new Phrase(new Chunk("Emplacement", headerStyle)));
		desCell.setBackgroundColor(Color.gray);
		desCell.setPaddingBottom(2);

		table.addCell(desCell);
		
		if (!listeProduit.isEmpty()) {
	
		for (LigneApprovisionement line : listeProduit) {	

			//cipm
			PdfPCell inCipm = new PdfPCell(cellBorderless);
			inCipm.setPhrase(new Phrase(new Chunk(line.getCip(), boddyStyle)));
			inCipm.setPaddingBottom(2);
			table.addCell(inCipm);

			//cip
			PdfPCell inCipCell = new PdfPCell(cellBorderless);
			inCipCell.setPhrase(new Phrase(new Chunk(line.getCipMaison(), boddyStyle)));
			inCipCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			inCipCell.setPaddingBottom(2);

			table.addCell(inCipCell);

			
			
			PdfPCell inPaCell = new PdfPCell(cellBorderless);
			inPaCell.setPhrase(new Phrase(new Chunk(""+line.getDesignation(), boddyStyle)));
			//totalAchat = totalAchat.add(line.getPrixAchatUnitaire()) ;
			inPaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			inPaCell.setPaddingBottom(2);
			table.addCell(inPaCell);

			// qte
			PdfPCell inqteCell = new PdfPCell(cellBorderless);
			inqteCell.setPhrase(new Phrase(new Chunk(""+line.getQuantieEnStock(), boddyStyle)));
			inqteCell.setPaddingBottom(2);
			inqteCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(inqteCell);
			//totalQte= totalQte.add(line.getQuantiteAprovisione());

			// prix vente
			PdfPCell inPvCell = new PdfPCell(cellBorderless);
			inPvCell.setPhrase(new Phrase(new Chunk(" "+line.getPrixVenteUnitaire(), boddyStyle)));
			inPvCell.setPaddingBottom(2);
			inPvCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table.addCell(inPvCell);
			
			//Designation
			PdfPCell inDesCell = new PdfPCell(cellBorderless);
			inDesCell.setPhrase(new Phrase(new Chunk(line.getProduit().getRayon().getName(), boddyStyle)));
			inDesCell.setPaddingBottom(2);
			inDesCell.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(inDesCell);


		}
		
	}
		
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

		super.buildPdfMetadata(model, document, request);
	}
}
