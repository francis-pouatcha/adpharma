package org.adorsys.adpharma.beans.pdf;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.OperationCaisse;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeBon;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("disbusementPdfView")
public class DisbusementPdfView extends   AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		OperationCaisse operationCaisse= (OperationCaisse) model.get("operationCaisse");
		Site site = Site.findSite(Long.valueOf(1));
		Font headerStyle = new Font(Font.COURIER,9);
		headerStyle.setStyle("bold");
		Font boddyStyle = new Font(Font.COURIER,9);

		Font footStyle = new Font(Font.COURIER,9);

		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.5f);
		cellStyle.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cellBorderlessStyle = new PdfPCell(cellStyle);
		cellBorderlessStyle.setBorderWidth(0);

		PdfPCell cellBorderless = new PdfPCell(cellStyle);
		cellBorderless.setBorderWidthBottom(0);
		cellBorderless.setBorderWidthTop(0);

		PdfPCell cellBordertop = new PdfPCell(cellStyle);
		cellBordertop.setBorderWidthTop(0);
		cellBordertop.setHorizontalAlignment(Element.ALIGN_CENTER);

		cellBorderless.setHorizontalAlignment(Element.ALIGN_CENTER);
		PdfPCell amountStyle = new PdfPCell(cellStyle);
		amountStyle.setHorizontalAlignment(Element.ALIGN_RIGHT);

		PdfPCell amountBorderlessStyle = new PdfPCell(cellBorderlessStyle);
		amountBorderlessStyle.setHorizontalAlignment(Element.ALIGN_RIGHT);

		// addresse de la pharmacie
		float[] adColumnsWith = {3.5f};
		float[] footColumnsWith = {7f};
		float[] ColumnsWith = {1.4f, 3.4f, .6f,1f};
		float[] payColumnsWith = {2f, 2f};

		PdfPTable adressTable = new PdfPTable(adColumnsWith);
		adressTable.setWidthPercentage(100);

		PdfPTable conTable = new PdfPTable(ColumnsWith);
		conTable.setWidthPercentage(100);

		PdfPTable payTable = new PdfPTable(payColumnsWith);
		payTable.setWidthPercentage(100);

		PdfPTable footTable = new PdfPTable(footColumnsWith);
		payTable.setWidthPercentage(100);



		PdfPCell lineCell = new PdfPCell(cellBorderlessStyle);
		lineCell.setPhrase(new Phrase(new Chunk("----------------------------------", boddyStyle)));
		lineCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		lineCell.setPaddingBottom(2);

		PdfPCell titre = new PdfPCell(cellBorderlessStyle);
		titre.setPhrase(new Phrase(new Chunk("TICKET DECAISSEMENT ", headerStyle)));
		titre.setHorizontalAlignment(Element.ALIGN_CENTER);
		adressTable.addCell(titre);// 
		adressTable.addCell(lineCell);

		//head of ticket
		PdfPCell senderCell = new PdfPCell(cellBorderlessStyle);
		senderCell.setPhrase(new Phrase(new Chunk(site.getDisplayName().toUpperCase(), headerStyle)));
		senderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(senderCell);// 



		PdfPCell addCell1 = new PdfPCell(cellBorderlessStyle);
		addCell1.setPhrase(new Phrase(new Chunk(site.getSiteManager().toUpperCase(), headerStyle)));
		addCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(addCell1);
		PdfPCell addCell2 = new PdfPCell(cellBorderlessStyle);
		addCell2.setPhrase(new Phrase(new Chunk(site.getAdresse(), boddyStyle)));
		addCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(addCell2);
		PdfPCell telCell = new PdfPCell(cellBorderlessStyle);
		telCell.setPhrase(new Phrase(new Chunk("Tel: "+site.getPhone() +" Fax : "+ site.getFax(), boddyStyle)));
		telCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(telCell);

		PdfPCell emailCell = new PdfPCell(cellBorderlessStyle);
		emailCell.setPhrase(new Phrase(new Chunk("Email : "+site.getEmail(), boddyStyle)));
		emailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(emailCell);

		PdfPCell regCell = new PdfPCell(cellBorderlessStyle);
		regCell.setPhrase(new Phrase(new Chunk("No Contribuable : "+site.getNumeroRegistre(), boddyStyle)));
		regCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(regCell);

		PdfPCell emptyCell = new PdfPCell(cellBorderlessStyle);
		emptyCell.setPhrase(new Phrase(new Chunk("   ", boddyStyle)));
		emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		emptyCell.setPaddingBottom(2);
		adressTable.addCell(emptyCell);
		adressTable.addCell(lineCell);

		//ticket information
		PdfPCell tnCell = new PdfPCell(cellBorderlessStyle);
		tnCell.setPhrase(new Phrase(new Chunk("CAISSIER : "+ operationCaisse.getOperateur(), boddyStyle)));
		tnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(tnCell);// 
		PdfPCell caiCell1 = new PdfPCell(cellBorderlessStyle);
		caiCell1.setPhrase(new Phrase(new Chunk("ORDONNE PAR : "+operationCaisse.getOrderBy(),boddyStyle)));
		caiCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(caiCell1);
		PdfPCell clienCell = new PdfPCell(cellBorderlessStyle);
		clienCell.setPhrase(new Phrase(new Chunk("DESTINNE A :  "+operationCaisse.getGiveTo(), boddyStyle)));
		clienCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(clienCell);
		adressTable.addCell(emptyCell);
		adressTable.addCell(lineCell);

		PdfPCell salCell2 = new PdfPCell(cellBorderlessStyle);
		salCell2.setPhrase(new Phrase(new Chunk("MONTANT :  "+operationCaisse.getMontant().intValue()+" CFA", boddyStyle)));
		salCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(salCell2);

		PdfPCell cltcell = new PdfPCell(cellBorderlessStyle);
		cltcell.setPhrase(new Phrase(new Chunk("RAISON :"+operationCaisse.getRaisonOperation(), boddyStyle)));
		cltcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(cltcell);
		adressTable.addCell(emptyCell);
		if(StringUtils.isNotBlank(operationCaisse.getHavenumber())){
			PdfPCell avcell = new PdfPCell(cellBorderlessStyle);
			avcell.setPhrase(new Phrase(new Chunk("Bon Avoir No :"+operationCaisse.getHavenumber(), boddyStyle)));
			avcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			adressTable.addCell(avcell);
			adressTable.addCell(emptyCell);
		}
       
		PdfPCell datecell = new PdfPCell(cellBorderlessStyle);
		datecell.setPhrase(new Phrase(new Chunk("DATE OPERATION :"+PharmaDateUtil.format(operationCaisse.getDateOperation(), PharmaDateUtil.DATETIME_PATTERN_LONG), boddyStyle)));
		datecell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(datecell);
		

		PdfPCell notecell = new PdfPCell(cellBorderlessStyle);
		notecell.setPhrase(new Phrase(new Chunk("NOTE :"+operationCaisse.getNote(), boddyStyle)));
		notecell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(notecell);


		document.add(adressTable);


	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,
			Document document, HttpServletRequest request) {
		document.setPageSize(new Rectangle(0, 0, 200, 1000));
		document.setMargins(5, 5, 3, 5);
		Font boddyStyle = new Font(Font.COURIER,10);
		boddyStyle.setStyle("bold");


		/*HeaderFooter header = new HeaderFooter(new Phrase(new Chunk("BON AVOIR CLIENT " , boddyStyle)), false);
		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);*/


		super.buildPdfMetadata(model, document, request);
	}
}
