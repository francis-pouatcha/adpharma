package org.adorsys.adpharma.beans.pdf;

import java.awt.Color;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.beans.process.ReclamationBean;
import org.adorsys.adpharma.domain.Fournisseur;
import org.adorsys.adpharma.domain.LigneCmdFournisseur;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.ibm.icu.text.DateFormat;
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



@Component("reclamationsPdfView")
public class ReclamationsPdfView  extends   AbstractPdfView{

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		   // Data of model
		      Site site = Site.findSite(Long.valueOf(1));
		      Fournisseur fournisseur= (Fournisseur)model.get("fournisseur");
		      List<ReclamationBean> reclamations= (List<ReclamationBean>)model.get("reclamations");
		      
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
				
				PdfPCell editCell = new PdfPCell(cellBorderlessStyle);
				editCell.setPhrase(new Phrase(new Chunk("Edite le : " +PharmaDateUtil.format(new Date(), PharmaDateUtil.DATETIME_PATTERN_LONG), boddyStyle)));
				editCell.setColspan(2);
				editCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				adressTable.addCell(editCell);
				adressTable.addCell(emptyCell);// 1:2
				
				PdfPCell userCell = new PdfPCell(cellBorderlessStyle);
				userCell.setPhrase(new Phrase(new Chunk("Edite par : "+SecurityUtil.getPharmaUser().getFullName(), boddyStyle)));
				userCell.setColspan(2);
				userCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				adressTable.addCell(userCell);
				adressTable.addCell(emptyCell);// 1:2
				
				document.add(adressTable);
				PdfPTable adressTable1 = new PdfPTable(adColumnsWith);
				adressTable1.setWidthPercentage(100);
				document.add(adressTable1);
						
				PdfPTable spaceTable = new PdfPTable(1);
				spaceTable.addCell(emptyCell);
				spaceTable.setSpacingAfter(5);
				document.add(spaceTable);
				
				// la table de reclamations
				float[] colWidths = {1.5f, 1.2f, 1.6f, 3.5f, 1.3f, 1.3f, 1.5f, 2f};
				PdfPTable table = new PdfPTable(colWidths);
				table.setWidthPercentage(100);
				table.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.setHeaderRows(1);
				headerStyle.setColor(Color.WHITE);

		
			/* Alignement des colonnes */	
				PdfPCell bordCell = new PdfPCell(cellStyle);
				bordCell.setPhrase(new Phrase(new Chunk("No Bordereau ", headerStyle)));
				bordCell.setBackgroundColor(Color.gray);
				bordCell.setPaddingBottom(5);
				table.addCell(bordCell);
				
				PdfPCell approCell = new PdfPCell(cellStyle);
				approCell.setPhrase(new Phrase(new Chunk("No Appro", headerStyle)));
				approCell.setBackgroundColor(Color.gray);
				approCell.setPaddingBottom(5);
				table.addCell(approCell);
				
				PdfPCell lotCell = new PdfPCell(cellStyle);
				lotCell.setPhrase(new Phrase(new Chunk("Cipm ", headerStyle)));
				lotCell.setBackgroundColor(Color.gray);
				lotCell.setPaddingBottom(5);
				table.addCell(lotCell);
				
				PdfPCell desCell = new PdfPCell(cellStyle);
				desCell.setPhrase(new Phrase(new Chunk("Designation ", headerStyle)));
				desCell.setBackgroundColor(Color.gray);
				desCell.setPaddingBottom(5);
				table.addCell(desCell);
				
				PdfPCell qteAppCell = new PdfPCell(cellStyle);
				qteAppCell.setPhrase(new Phrase(new Chunk("Qte En Stock ", headerStyle)));
				qteAppCell.setBackgroundColor(Color.gray);
				qteAppCell.setPaddingBottom(5);
				table.addCell(qteAppCell);

				PdfPCell reclamCell = new PdfPCell(cellStyle);
				reclamCell.setPhrase(new Phrase(new Chunk("Qte Reclamee ", headerStyle)));
				reclamCell.setBackgroundColor(Color.gray);
				reclamCell.setPaddingBottom(5);
				table.addCell(reclamCell);
				
				PdfPCell paCell = new PdfPCell(cellStyle);
				paCell.setPhrase(new Phrase(new Chunk("P.A. Unitaire ", headerStyle)));
				paCell.setBackgroundColor(Color.gray);
				paCell.setPaddingBottom(5);
				table.addCell(paCell);
				
				PdfPCell dateCell = new PdfPCell(cellStyle);
				dateCell.setPhrase(new Phrase(new Chunk("Date Reclamation ", headerStyle)));
				dateCell.setBackgroundColor(Color.gray);
				dateCell.setPaddingBottom(5);
				table.addCell(dateCell);
				
			// Ajout des lignes de reclamations	
				for (ReclamationBean line : reclamations) {	
			            //Numero de bordereau
						PdfPCell inBordCell = new PdfPCell(cellBorderless);
						inBordCell.setPhrase(new Phrase(new Chunk(line.getBordereauNumber(), boddyStyle)));
						inBordCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						inBordCell.setPaddingBottom(5);
						table.addCell(inBordCell);
						
					    //Numero d'approvisionement
						PdfPCell inAppCell = new PdfPCell(cellBorderless);
						inAppCell.setPhrase(new Phrase(new Chunk(line.getApproNumber(), boddyStyle)));
						inAppCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						inAppCell.setPaddingBottom(5);
						table.addCell(inAppCell);
						
						// Numero  de Lot
						PdfPCell inLotCell = new PdfPCell(cellBorderless);
						inLotCell.setPhrase(new Phrase(new Chunk(""+line.getCipm(), boddyStyle)));
						inLotCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						inLotCell.setPaddingBottom(5);
						table.addCell(inLotCell);

						// Designation
						PdfPCell inDesCell = new PdfPCell(cellBorderless);
						inDesCell.setPhrase(new Phrase(new Chunk(""+line.getDesignation(), boddyStyle)));
						inDesCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						inDesCell.setPaddingBottom(5);
						table.addCell(inDesCell);
						
						// Qte En stock
						PdfPCell inQteAppCell = new PdfPCell(cellBorderless);
						inQteAppCell.setPhrase(new Phrase(new Chunk(""+line.getQteStock(), boddyStyle)));
						inQteAppCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						inQteAppCell.setPaddingBottom(5);
						table.addCell(inQteAppCell);
								
						// Quantite reclamee
						PdfPCell inRecCell = new PdfPCell(cellBorderless);
						inRecCell.setPhrase(new Phrase(new Chunk(""+line.getReclamQuantity().intValue(), boddyStyle)));
						inRecCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						inRecCell.setPaddingBottom(5);
						table.addCell(inRecCell);
						
						// Prix Achat Unitaire
						PdfPCell inPaCell = new PdfPCell(cellBorderless);
						inPaCell.setPhrase(new Phrase(new Chunk(""+line.getPrixAchat().longValue(), boddyStyle)));
						inPaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						inPaCell.setPaddingBottom(5);
						table.addCell(inPaCell);
						
						// Date reclamation
						PdfPCell inDatCell = new PdfPCell(cellBorderless);
						inDatCell.setPhrase(new Phrase(new Chunk(""+line.getReclamationDate().toString(), boddyStyle)));
						inDatCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						inDatCell.setPaddingBottom(5);
						table.addCell(inDatCell);
					}
				
				document.add(table);
		
	}
	
	
	@Override
	protected void buildPdfMetadata(Map<String, Object> model,
			Document document, HttpServletRequest request) {
		
		Fournisseur fournisseur= (Fournisseur)model.get("fournisseur");
		Date dateMin= (Date)model.get("dateMin");
	    Date dateMax= (Date)model.get("dateMax");
		
		document.setPageSize(PageSize.A4);
		document.setMargins(30, 30, 50, 50);
		Font boddyStyle = new Font(Font.COURIER,8);
		
		Font headerStyle= new Font(Font.COURIER, 10);
		headerStyle.setStyle(Font.BOLD);

		HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " Page" , boddyStyle)), true);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);
		
		HeaderFooter header = null ;
       	header	= new HeaderFooter(new Phrase(new Chunk("FICHE DE RECLAMATIONS DU FOURNISSEUR: "+fournisseur.getName()+" ENTRE: "+PharmaDateUtil.format(dateMin, PharmaDateUtil.DATE_PATTERN_LONG)+" ET:"+PharmaDateUtil.format(dateMax, PharmaDateUtil.DATE_PATTERN_LONG) , headerStyle)), false);
		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);
		
		super.buildPdfMetadata(model, document, request);
		
	}
	
	

}
