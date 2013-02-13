package org.adorsys.adpharma.beans.pdf;

import java.awt.Color;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.LigneFacture;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.domain.TypeFacture;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;
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
import  org.adorsys.adpharma.domain.Facture ;
@Component("facturePdfDocViews")

public class FacturePdfDocViews extends   AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Facture facture = (Facture) model.get("facture");
		if(facture == null){
			document.add(new Phrase("\n\n\n aucune facture trouvee !"));
			return ;
		}
		String nom = (String) model.get("nom");

		Site site = Site.findSite(Long.valueOf(1));
		Font headerStyle = new Font(Font.COURIER,8);
		headerStyle.setStyle("bold");
		Font boddyStyle = new Font(Font.COURIER,8);
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

		PdfPCell regCell = new PdfPCell(cellBorderlessStyle);
		regCell.setPhrase(new Phrase(new Chunk("No Contribuable : "+site.getNumeroRegistre(), boddyStyle)));
		regCell.setColspan(2);
		regCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		adressTable.addCell(regCell);
		adressTable.addCell(emptyCell);// 1:2

		adressTable.setSpacingAfter(20);
		document.add(adressTable);
		PdfPTable adressTable1 = new PdfPTable(adColumnsWith);
		adressTable1.setWidthPercentage(100);

		PdfPCell customerNameCell = new PdfPCell(cellBorderlessStyle);
		customerNameCell.setPhrase(new Phrase(new Chunk("Client :", headerStyle)));
		customerNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable1.addCell(customerNameCell);// 2:1

		PdfPCell customerName = new PdfPCell(cellBorderlessStyle);
		if (StringUtils.isNotBlank(nom)) {
			customerName.setPhrase(new Phrase(new Chunk(nom.toUpperCase(), boddyStyle)));

		}else {
			customerName.setPhrase(new Phrase(new Chunk(facture.getCommande().getClient().getNomComplet(), boddyStyle)));

		}

		customerName.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable1.addCell(customerName);// 2:2
		adressTable1.addCell(emptyCell);// 2:3
		if (facture.getCommande().getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT)) {
			PdfPCell payeurNameCell = new PdfPCell(cellBorderlessStyle);
			payeurNameCell.setPhrase(new Phrase(new Chunk("Client Payeur :", headerStyle)));
			payeurNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);

			adressTable1.addCell(payeurNameCell);// 2:1

			PdfPCell payeurName = new PdfPCell(cellBorderlessStyle);

			payeurName.setPhrase(new Phrase(new Chunk(facture.getCommande().getClientPaiyeur().getNomComplet(), boddyStyle)));
			payeurName.setHorizontalAlignment(Element.ALIGN_LEFT);

			adressTable1.addCell(payeurName);// 2:2
			adressTable1.addCell(emptyCell);// 2:3
		}
		PdfPCell addressOnInvoiceCell = new PdfPCell(cellBorderlessStyle);
		addressOnInvoiceCell.setPhrase(new Phrase(new Chunk("Date Facturation :", headerStyle)));
		addressOnInvoiceCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable1.addCell(addressOnInvoiceCell);// 3:1
		PdfPCell invoiceDateCell = new PdfPCell(cellBorderlessStyle);
		invoiceDateCell.setPhrase(new Phrase(new Chunk(PharmaDateUtil.format(facture.getDateCreation(), "dd-MM-yyyy HH:mm"), boddyStyle)));
		invoiceDateCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable1.addCell(invoiceDateCell);// 3:2
		adressTable1.addCell(emptyCell);// 3:3
		PdfPCell sallerCell = new PdfPCell(cellBorderlessStyle);
		sallerCell.setPhrase(new Phrase(new Chunk("Vendeur :", headerStyle)));
		sallerCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable1.addCell(sallerCell);// 2:1

		PdfPCell sallerName = new PdfPCell(cellBorderlessStyle);

		sallerName.setPhrase(new Phrase(new Chunk(facture.getVendeur().displayName(), boddyStyle)));
		sallerName.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable1.addCell(sallerName);// 2:2
		adressTable1.addCell(emptyCell);// 2:3

		PdfPCell invoiceNumberCell = new PdfPCell(cellBorderlessStyle);
		invoiceNumberCell.setPhrase(new Phrase(new Chunk("Facture No : " , headerStyle)));
		invoiceNumberCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable1.addCell(invoiceNumberCell);// 5:1

		PdfPCell invoiceNumber = new PdfPCell(cellBorderlessStyle);
		invoiceNumber.setPhrase(new Phrase(new Chunk(facture.getFactureNumber() , boddyStyle)));
		invoiceNumber.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable1.addCell(invoiceNumber);// 5:2
		adressTable1.addCell(emptyCell);// 5:3

		document.add(adressTable1);

		PdfPTable spaceTable = new PdfPTable(1);
		spaceTable.addCell(emptyCell);
		spaceTable.setSpacingAfter(10);
		document.add(spaceTable);

		// la table d'approvisionement
		float[] colWidths = {1.5f,4.2f, 1.5f,  1.5f, 1.5f};
		PdfPTable table = new PdfPTable(colWidths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setHeaderRows(1);
		headerStyle.setColor(Color.WHITE);



		PdfPCell cipMCell = new PdfPCell(cellStyle);
		cipMCell.setPhrase(new Phrase(new Chunk("code Produit", headerStyle)));
		cipMCell.setPaddingBottom(5);
		table.addCell(cipMCell );


		PdfPCell cipCell = new PdfPCell(cellStyle);
		cipCell.setPhrase(new Phrase(new Chunk("Designation", headerStyle)));
		cipCell.setPaddingBottom(5);

		table.addCell(cipCell);

		PdfPCell desCell = new PdfPCell(cellStyle);
		desCell.setPhrase(new Phrase(new Chunk("Qte Achete", headerStyle)));
		desCell.setPaddingBottom(5);

		table.addCell(desCell);


		/*	PdfPCell datePrmCell = new PdfPCell(cellStyle);
		datePrmCell.setPhrase(new Phrase(new Chunk("Qte Retourne", headerStyle)));
		table.addCell(datePrmCell);*/

		PdfPCell pvCell = new PdfPCell(cellStyle);
		pvCell.setPhrase(new Phrase(new Chunk("Prix Unit", headerStyle)));
		pvCell.setPaddingBottom(5);

		table.addCell(pvCell);


		PdfPCell paCell = new PdfPCell(cellStyle);
		paCell.setPhrase(new Phrase(new Chunk("Prix Total", headerStyle)));
		paCell.setPaddingBottom(5);

		table.addCell(paCell);



		Set<LigneFacture> ligneFacture = facture.getLineFacture();



		for (LigneFacture line : ligneFacture) {	

			//cipm
			PdfPCell inCipm = new PdfPCell(cellBorderless);
			inCipm.setPhrase(new Phrase(new Chunk(line.getCipM(), boddyStyle)));
			inCipm.setPaddingBottom(5);

			table.addCell(inCipm);

			//cip
			PdfPCell inCipCell = new PdfPCell(cellBorderless);
			inCipCell.setPhrase(new Phrase(new Chunk(line.getDesignation().toUpperCase(), boddyStyle)));
			inCipCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			inCipCell.setPaddingBottom(5);
			table.addCell(inCipCell);

			//Designation
			PdfPCell inDesCell = new PdfPCell(cellBorderless);
			inDesCell.setPhrase(new Phrase(new Chunk(line.getQteAchete().toString(), boddyStyle)));
			inDesCell.setPaddingBottom(5);
			table.addCell(inDesCell);

			// date Peremption
			/*PdfPCell inprmCell = new PdfPCell(cellBorderless);
			inprmCell.setPhrase(new Phrase(new Chunk(line.getQteRetourne().toString(), boddyStyle)));
			table.addCell(inprmCell);
			 */
			// prix Vente
			PdfPCell inPvCell = new PdfPCell(cellBorderless);
			inPvCell.setPhrase(new Phrase(new Chunk(line.getPrixUnitaire().toString(), boddyStyle)));
			inPvCell.setPaddingBottom(5);
			inPvCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table.addCell(inPvCell);

			// prix achat
			PdfPCell inPaCell = new PdfPCell(cellBorderless);
			inPaCell.setPhrase(new Phrase(new Chunk(line.getPrixTotal().toString(), boddyStyle)));
			inPaCell.setPaddingBottom(5);
			inPaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table.addCell(inPaCell);

		}
		headerStyle.setColor(Color.BLACK);
		PdfPCell vide = new PdfPCell(cellBorderless);
		vide.setPhrase(new Phrase(new Chunk("", boddyStyle)));

		PdfPCell vide2 = new PdfPCell(cellBordertop);
		vide2.setPhrase(new Phrase(new Chunk("", boddyStyle)));

		table.addCell(vide);
		// total row
		PdfPCell totalLabel = new PdfPCell(cellBorderless);
		totalLabel.setPhrase(new Phrase(new Chunk("Montant Total Achat :", headerStyle)));
		totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalLabel.setPaddingBottom(5);

		table.addCell(totalLabel);

		table.addCell(vide);
		table.addCell(vide);

		// montant ht
		PdfPCell basicAmountCell = new PdfPCell(cellBorderless);
		basicAmountCell.setPhrase(new Phrase(new Chunk(facture.getMontantTotal().toString(), headerStyle)));
		basicAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

		table.addCell(basicAmountCell);

		table.addCell(vide);
		// total row
		PdfPCell totalRemise = new PdfPCell(cellBorderless);
		totalRemise.setPhrase(new Phrase(new Chunk("Montant Total Remise : ", headerStyle)));
		totalRemise.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalRemise.setPaddingBottom(5);

		table.addCell(totalRemise);
		table.addCell(vide);
		table.addCell(vide);

		// montant total remise 
		PdfPCell remise = new PdfPCell(cellBorderless);
		if(facture.getPrintWithReduction()){
			remise.setPhrase(new Phrase(new Chunk(facture.getMontantRemise().toString(), headerStyle)));
		}else {
			remise.setPhrase(new Phrase(new Chunk("0", headerStyle)));
		}
		remise.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(remise);
		table.addCell(vide);
		boolean isSaleCash = facture.getCommande().getTypeCommande().equals(TypeCommande.VENTE_AU_PUBLIC);
		BigInteger netPayer = facture.getNetPayer();
		BigInteger payamount = isSaleCash?BigInteger.ZERO:facture.getAvance();
		BigInteger reste = isSaleCash?facture.getNetPayer():facture.getReste();
		// total row
		PdfPCell totalAvance = new PdfPCell(cellBorderless);
		totalAvance.setPhrase(new Phrase(new Chunk("Montant Total Avance : ", headerStyle)));
		totalAvance.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalAvance.setPaddingBottom(5);

		table.addCell(totalAvance);
		table.addCell(vide);
		table.addCell(vide);

		// montant total remise 
		PdfPCell avance = new PdfPCell(cellBorderless);
		if(facture.getPrintWithReduction()){
			
			avance.setPhrase(new Phrase(new Chunk(payamount.toString(), headerStyle)));
		}else {
			avance.setPhrase(new Phrase(new Chunk(payamount.add(facture.getMontantRemise()).toString(), headerStyle)));
		}
		avance.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(avance);

		table.addCell(vide2);
		// total row
		PdfPCell total = new PdfPCell(cellBordertop);
		total.setPhrase(new Phrase(new Chunk("NET a PAYER : ", headerStyle)));
		total.setHorizontalAlignment(Element.ALIGN_RIGHT);
		total.setPaddingBottom(5);

		table.addCell(total);

		table.addCell(vide2);
		table.addCell(vide2);

		// montant total remise 
		PdfPCell motantTc= new PdfPCell(cellBordertop);
		if(facture.getPrintWithReduction()){
			motantTc.setPhrase(new Phrase(new Chunk(reste.toString(), headerStyle)));
		}else {
			if(facture.getReste().intValue()==0){
				motantTc.setPhrase(new Phrase(new Chunk(reste.toString(), headerStyle)));
			}else {
				motantTc.setPhrase(new Phrase(new Chunk(reste.add(facture.getMontantRemise()).toString(), headerStyle)));
			}

		}
		motantTc.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(motantTc);

		RuleBasedNumberFormat rbnf = new RuleBasedNumberFormat(Locale.FRANCE, RuleBasedNumberFormat.SPELLOUT);

		document.add(table);
		if (facture.getCommande().getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT)) {
			document.add(new Paragraph(new Phrase(new Chunk("Bon Commande NO: "+facture.getCommande().getNumeroBon().toUpperCase(), headerStyle))));

		}

		if(facture.getPrintWithReduction()){
			document.add(new Paragraph(new Phrase(new Chunk("Arrete la facture a la somme de  :"+rbnf.format(reste.intValue()).replace("-", " ").toUpperCase()+" FRANCS CFA", headerStyle))));
		}else {
			if(facture.getReste().intValue()==0){
				document.add(new Paragraph(new Phrase(new Chunk("Arrete la facture a la somme de  :"+rbnf.format(reste.intValue()).replace("-", " ").toUpperCase()+" FRANCS CFA", headerStyle))));
			}else {
				motantTc.setPhrase(new Phrase(new Chunk(reste.add(facture.getMontantRemise()).toString(), headerStyle)));
				document.add(new Paragraph(new Phrase(new Chunk("Arrete la facture a la somme de  :"+rbnf.format(reste.add(facture.getMontantRemise()).intValue()).replace("-", " ").toUpperCase()+" FRANCS CFA", headerStyle))));

			}

		}

	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,

			Document document, HttpServletRequest request) {
		document.setPageSize(PageSize.A5);
		document.setMargins(5, 5, 10, 10);
		Facture facture = (Facture) model.get("facture");
		Font headerStyle = new Font(Font.COURIER,10);
		headerStyle.setStyle("bold");

		HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " - Page" , headerStyle)), true);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);
		HeaderFooter header = null ;
		if (facture.getTypeFacture().equals(TypeFacture.PROFORMAT)) {
			header	= new HeaderFooter(new Phrase(new Chunk("FACTURE PROFORMAT "+facture.getCommande().getCmdNumber() , headerStyle)), false);
		}else {
			header	= new HeaderFooter(new Phrase(new Chunk("FACTURE "+facture.getCommande().getCmdNumber() , headerStyle)), false);

		}
		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);

		super.buildPdfMetadata(model, document, request);
	}

}
