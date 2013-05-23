package org.adorsys.adpharma.beans.pdf;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.Site;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("ficheCodeBare23")
public class FicheCodeBare23 extends   AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PdfContentByte cb = writer.getDirectContent();
		Font headerStyle = new Font();
		headerStyle.setStyle("bold");
		Font boddyStyles = new Font(Font.TIMES_ROMAN,4);
		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.1f);
      
		PdfPCell cellBorderlessStyle = new PdfPCell(cellStyle);
		cellBorderlessStyle.setBorderWidth(0);

		PdfPCell amountStyle = new PdfPCell(cellStyle);
		amountStyle.setHorizontalAlignment(Element.ALIGN_RIGHT);

		PdfPCell amountBorderlessStyle = new PdfPCell(cellBorderlessStyle);
		amountBorderlessStyle.setHorizontalAlignment(Element.ALIGN_RIGHT);

		// addresse de la pharmacie
		float[] adColumnsWith = {.5f, .5f,.5f  ,.5f  ,.5f ,.5f ,.5f,.5f};
		float[] imgColumnsWith = {.5f ,  .2f};
		int nbline = 8 ;
		Site site = Site.findSite(new Long(1));
		if(site!=null) nbline = site.getBareCodePerLine()!=null ?site.getBareCodePerLine().intValue():8;
		PdfPTable adressTable = new PdfPTable(8);
		adressTable.setWidthPercentage(100);
		adressTable.setHeaderRows(0);
		Font boddyStyle = new Font(Font.BOLD,10);
		
		boddyStyle.setStyle("bold");
		PdfPCell emptyCell = new PdfPCell(cellBorderlessStyle);
		emptyCell.setPhrase(new Phrase("\n"));
		emptyCell.setMinimumHeight(8f);
		PdfPCell empty = new PdfPCell(cellBorderlessStyle);
		//PrintEmptyCell(12, 2, adressTable, emptyCell);
		List<LigneApprovisionement>  ligne = ( List<LigneApprovisionement> ) model.get("ligneApprivisionement");
		if (!ligne.isEmpty()) {
			int size = ligne.size();
		for (LigneApprovisionement ligneApprovisionement : ligne) {
             int i = 1 ;
			int quantieEnStock = ligneApprovisionement.getQuantiteAprovisione().intValue();
		 	
		printBareCode(ligneApprovisionement, quantieEnStock, adressTable, cb);
		
		 	
		}
		
	}
		adressTable.completeRow();

			document.add(adressTable);
			document.add(new Phrase(new Chunk(".")));
	}
	
	public void PrintEmptyCell(int emptyLine,int emptyColumn,PdfPTable table,PdfPCell emptyCell){
			for (int i = 1; i <= emptyLine; i++) {
				if (i == emptyLine) {
					for (int j = 1; j <= emptyColumn; j++) {
						table.addCell(emptyCell);
					}
					
				}else {
					for (int j = 1; j <= 5; j++) {
						table.addCell(emptyCell);
					}
				}
				
			}
			
		
	}
	
	public void printBareCode(LigneApprovisionement ligneApprovisionement ,int quantieEnStock,PdfPTable table,PdfContentByte cb){
	 	   String filiale = ligneApprovisionement.getProduit().getFiliale()!=null?ligneApprovisionement.getProduit().getFiliale().getId().toString():"";
		Font boddyStyles = new Font(Font.BOLD,4);
		boddyStyles.setStyle("bold");
		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.1f);
		PdfPCell cellBorderlessStyle = new PdfPCell(cellStyle);
		cellBorderlessStyle.setBorderWidth(0);
		for (int i = 0; i < quantieEnStock ; i++) {
			Barcode128 code128 = new Barcode128();
			code128.setCodeType(code128.CODE128);
			code128.setBarHeight(26);
			code128.setCode(ligneApprovisionement.getCipMaison());
			Image imageEAN = code128.createImageWithBarcode(cb, null, null);
			PdfPCell imgCell = new PdfPCell(cellBorderlessStyle);
			PdfPCell textcell = new PdfPCell(cellBorderlessStyle);
			imgCell.setBorder(0);
			imgCell.setPaddingBottom(0);
			imgCell.setPaddingTop(0);
			imgCell.setPaddingLeft(10);
			imgCell.setPaddingRight(10);
			textcell.setBorder(0);
			imgCell.setImage(imageEAN);
			imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			String designation = ligneApprovisionement.getDesignation();
			int length = designation.length();
			if (length >20) {
				designation = designation.substring(0, 19);
			}
			
			textcell.setPhrase(new Phrase(new Chunk(designation.toUpperCase()+"\n" +filiale+ligneApprovisionement.getApprovisionement().getFounisseur().displayCodeName()+ligneApprovisionement.getPrixVenteUnitaire().longValueExact()+" FCFA", boddyStyles)));
			textcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			PdfPTable imgTable = new PdfPTable(1);
			imgTable.setWidthPercentage(100);
			imgTable.addCell(imgCell);
			imgTable.addCell(textcell);
			PdfPCell codeBareCell = new PdfPCell(imgTable);
			codeBareCell.setFixedHeight(60f);
			table.addCell(imgTable);

		}

	}
	
	
	

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,	Document document, HttpServletRequest request) {
		document.setPageSize(PageSize.A4);
		document.setMargins(0,0, 5, 0);
		super.buildPdfMetadata(model, document, request);
	}
	
}
