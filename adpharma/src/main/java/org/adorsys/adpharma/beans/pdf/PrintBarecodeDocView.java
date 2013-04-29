package org.adorsys.adpharma.beans.pdf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.beans.process.PrintBareCodeBean;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("printBarecodeDocView")

public class PrintBarecodeDocView extends   AbstractPdfView {

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
		
		PdfPTable adressTable = new PdfPTable(5);
		adressTable.setWidthPercentage(100);
		adressTable.setHeaderRows(0);
		Font boddyStyle = new Font(Font.BOLD,10);
		
		boddyStyle.setStyle("bold");
		PdfPCell emptyCell = new PdfPCell(cellBorderlessStyle);
		emptyCell.setPhrase(new Phrase("\n\n\n\n"));
		emptyCell.setMinimumHeight(6f);
		PdfPCell empty = new PdfPCell(cellBorderlessStyle);
		PrintBareCodeBean printBareCodeBean = ( PrintBareCodeBean) model.get("printBareCodeBean");
		PrintEmptyCell(printBareCodeBean.getEmptyLine(), printBareCodeBean.getEmptyColumn(), adressTable, emptyCell);

		List<LigneApprovisionement> ligne = LigneApprovisionement.findLigneApprovisionementsByCipMaisonEquals(printBareCodeBean.getBareCode()).getResultList();
		if (!ligne.isEmpty()) {
			int size = ligne.size();
		for (LigneApprovisionement ligneApprovisionement : ligne) {
 	//	adressTable.addCell(new Phrase("test"));
  
			int quantieEnStock = printBareCodeBean.getNombre().intValue();
		 	/*for (int i = 0; i < quantieEnStock ; i++) {
		 		System.out.println(i);
		 		System.out.println(adressTable.getRows());
		 		
		 		
		 		  String filiale = ligneApprovisionement.getProduit().getFiliale()!=null?ligneApprovisionement.getProduit().getFiliale().getId().toString():"";
				Barcode128 code128 = new Barcode128();
				code128.setCodeType(code128.CODE128);
				code128.setBarHeight(17);
				code128.setCode(ligneApprovisionement.getCipMaison());
				Image imageEAN = code128.createImageWithBarcode(cb, null, null);
				PdfPCell imgCell = new PdfPCell(cellBorderlessStyle);
				PdfPCell textcell = new PdfPCell(cellBorderlessStyle);
				imgCell.setBorder(0);
				textcell.setBorder(0);
				imgCell.setImage(imageEAN);
				imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				String designation = ligneApprovisionement.getDesignation();
				int length = designation.length();
				if (length >25) {
					designation = designation.substring(0, 24);
				}
				
				textcell.setPhrase(new Phrase(new Chunk(designation.toUpperCase()+"\n" +
						filiale+":LX:"+ligneApprovisionement.getPrixVenteUnitaire().longValueExact()+" F", boddyStyles)));
				textcell.setHorizontalAlignment(Element.ALIGN_CENTER);
				textcell.setPaddingBottom(2);
				PdfPTable imgTable = new PdfPTable(1);
				imgTable.setWidthPercentage(100);
				imgTable.addCell(imgCell);
				imgTable.addCell(textcell);
				adressTable.addCell(imgTable);
			    System.out.println(ligneApprovisionement.getCipMaison());
	
			}*/
		printBareCode(ligneApprovisionement, quantieEnStock, adressTable, cb);
		
		 	
		}
		
	}
		adressTable.completeRow();

			document.add(adressTable);
			document.add(new Phrase(new Chunk(".")));
	}
	
	public void PrintEmptyCell(int emptyLine,int emptyColumn,PdfPTable table,PdfPCell emptyCell){
		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.1f);

		PdfPCell cellBorderlessStyle = new PdfPCell(cellStyle);
		cellBorderlessStyle.setBorderWidth(0);
		PdfPCell emptyCell2 = new PdfPCell(cellBorderlessStyle);
		emptyCell2.setPhrase(new Phrase("\n\n\n"));
		emptyCell2.setMinimumHeight(6f);
			for (int i = 1; i <= emptyLine; i++) {
				if (i == emptyLine) {
					for (int j = 1; j <= emptyColumn; j++) {
						table.addCell(emptyCell2);
					}
					
				}else {
					for (int j = 1; j <= 8; j++) {
						table.addCell(emptyCell);
					}
				}
				
			}
			
		
	}
	
	public void printBareCode(LigneApprovisionement ligneApprovisionement ,int quantieEnStock,PdfPTable table,PdfContentByte cb){
	 	   String filiale = ligneApprovisionement.getProduit().getFiliale()!=null?ligneApprovisionement.getProduit().getFiliale().getId().toString():"";
		Font boddyStyles = new Font(Font.TIMES_ROMAN,6);
		boddyStyles.setStyle("bold");
		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.1f);
		PdfPCell cellBorderlessStyle = new PdfPCell(cellStyle);
		cellBorderlessStyle.setBorderWidth(0);
		for (int i = 0; i < quantieEnStock ; i++) {
			Barcode128 code128 = new Barcode128();
			code128.setCodeType(code128.CODE128);
			code128.setBarHeight(24);
			code128.setCode(ligneApprovisionement.getCipMaison());
			
			Image imageEAN = code128.createImageWithBarcode(cb, null, null);
			PdfPCell imgCell = new PdfPCell(cellBorderlessStyle);
			PdfPCell textcell = new PdfPCell(cellBorderlessStyle);
			imgCell.setBorder(0);
			imgCell.setPaddingLeft(7);
			imgCell.setPaddingRight(7);
			textcell.setBorder(0);
			imgCell.setImage(imageEAN);
			imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			String designation = ligneApprovisionement.getDesignation();
			int length = designation.length();
			if (length >19) {
				designation = designation.substring(0, 18);
			}
			
			textcell.setPhrase(new Phrase(new Chunk(designation.toUpperCase()+"\n" +
					filiale+ligneApprovisionement.getApprovisionement().getFounisseur().displayCodeName()+ligneApprovisionement.getPrixVenteUnitaire().longValueExact()+" FCFA", boddyStyles)));
			textcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			PdfPTable imgTable = new PdfPTable(1);
			imgTable.setWidthPercentage(100);
			imgTable.addCell(imgCell);
			imgTable.addCell(textcell);
			table.addCell(imgTable);

		}

	}
	
	
	

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,	Document document, HttpServletRequest request) {
		document.setPageSize(PageSize.LETTER);
		document.setMargins(1,1, 2, 2);
		super.buildPdfMetadata(model, document, request);
	}

}
