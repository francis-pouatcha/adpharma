package org.adorsys.adpharma.beans.pdf;

import java.awt.Color;
import java.awt.Desktop;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.soap.SOAPBinding.Style;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.LigneInventaire;
import org.adorsys.adpharma.domain.OperationCaisse;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.RoleName;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeOpCaisse;
import org.adorsys.adpharma.domain.TypePaiement;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
@Component("bordereauCaissePdfDocView")
public class BordereauCaissePdfDocView extends   AbstractPdfView {
	BigDecimal fondgbl  ;
	BigDecimal totalencgbl ;
    BigDecimal remglbs  ;
	BigDecimal cashgbl  ;
	BigDecimal cashdgbl  ;
	BigDecimal cartecreditgbl  ;
	BigDecimal chequegbl  ;
	BigDecimal retraitgbl  ;
	BigDecimal boncmdgbl  ;
	BigDecimal boncltgbl  ;
	BigDecimal soldegbl  ;
	BigDecimal tauxChiffreAffaire  ;

	
	
	public void initGlobalAmount(){
		PharmaUser pharmaUser = SecurityUtil.getPharmaUser();
		Site site = Site.findSite(Long.valueOf(1));
		if(pharmaUser.hasAnyRole(RoleName.ROLE_CHIFFRE_AFFAIRE_VARIABLE)){
			BigDecimal taux = site.getTauxChiffreAffaire();
			System.out.println("taux : "+taux);
			if(taux!=null ) tauxChiffreAffaire = taux.divide(BigDecimal.valueOf(100));
			System.out.println("taux : "+tauxChiffreAffaire);
		}else {
			tauxChiffreAffaire = BigDecimal.ONE ;
			System.out.println("taux : "+tauxChiffreAffaire);
		}
		 fondgbl = BigDecimal.ZERO ;
		 totalencgbl = BigDecimal.ZERO ;
	     remglbs = BigDecimal.ZERO ;
		 cashgbl = BigDecimal.ZERO ;
		 cashdgbl = BigDecimal.ZERO ;
		 cartecreditgbl = BigDecimal.ZERO ;
		 chequegbl = BigDecimal.ZERO ;
		 retraitgbl = BigDecimal.ZERO ;
		 boncmdgbl = BigDecimal.ZERO ;
		 boncltgbl = BigDecimal.ZERO ;
		 soldegbl = BigDecimal.ZERO ;
	}
	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Site site = Site.findSite(Long.valueOf(1));
		
		Caisse caisse = (Caisse) model.get("caisse");
		List<Caisse> caisses = (List<Caisse>) model.get("caisses");
		if (caisse != null) {
			initGlobalAmount();
			addTexteToDocument(document, caisse, site);
			addGlobalAmountToDocument(document) ;

		}
		
		
		if (caisses != null) {
			initGlobalAmount();
			for (Caisse caisse2 : caisses) {
				addTexteToDocument(document, caisse2, site);
			}
			addGlobalAmountToDocument(document) ;
		}
		
	}

	
	public final void addTexteToDocument(Document document,Caisse caisse,Site site){

		Font headerStyle = new Font(Font.COURIER,7);
		headerStyle.setStyle("bold");
		Font boddyStyle = new Font(Font.COURIER,7);
		
		Font headerStyles = new Font(Font.COURIER,7);
		headerStyles.setStyle("bold");
		
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
		float[] adColumnsWith = {1.7f, 5.3f, 4f};
		PdfPTable adressTable = new PdfPTable(adColumnsWith);
		adressTable.setWidthPercentage(100);
		PdfPCell senderCell = new PdfPCell(cellBorderlessStyle);
		PdfPCell caisseCell = new PdfPCell(cellBorderlessStyle);
		senderCell.setPhrase(new Phrase(new Chunk(site.getDisplayName().toUpperCase(), headerStyle)));
		senderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		caisseCell.setPhrase(new Phrase(new Chunk("CAISSE NO : ", headerStyle)));
		caisseCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell caissenoCell = new PdfPCell(cellBorderlessStyle);
		caissenoCell.setPhrase(new Phrase(new Chunk(""+caisse.getCaisseNumber().toUpperCase(), headerStyle)));
		caissenoCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		PdfPCell emptyCell = new PdfPCell(cellBorderlessStyle);
		adressTable.addCell(caisseCell);// 1:2
		adressTable.addCell(caissenoCell);// 1:1
		adressTable.addCell(senderCell);// 1:1

		// Invoice company
		PdfPCell addCell1 = new PdfPCell(cellBorderlessStyle);
		addCell1.setPhrase(new Phrase(new Chunk(site.getSiteManager().toUpperCase(), headerStyle)));
		addCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell caissierCell1 = new PdfPCell(cellBorderlessStyle);
		caissierCell1.setPhrase(new Phrase(new Chunk("CAISSIER :", headerStyle)));
		caissierCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell caissiernameCell1 = new PdfPCell(cellBorderlessStyle);
		caissiernameCell1.setPhrase(new Phrase(new Chunk( caisse.getCaissier().displayName(), boddyStyle)));
		caissiernameCell1.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable.addCell(caissierCell1);// 1:2
		adressTable.addCell(caissiernameCell1);// 1:1
		adressTable.addCell(addCell1);// 1:1


		PdfPCell addCell2 = new PdfPCell(cellBorderlessStyle);
		addCell2.setPhrase(new Phrase(new Chunk(site.getAdresse(), boddyStyle)));
		addCell2.setHorizontalAlignment(Element.ALIGN_LEFT);

		PdfPCell ouCell2 = new PdfPCell(cellBorderlessStyle);
		ouCell2.setPhrase(new Phrase(new Chunk("DATE OUVERTURE :", headerStyle)));
		ouCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell oudCell2 = new PdfPCell(cellBorderlessStyle);
		oudCell2.setPhrase(new Phrase(new Chunk( PharmaDateUtil.format(caisse.getDateOuverture(), "dd-MM-yyyy HH:mm"), boddyStyle)));
		oudCell2.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable.addCell(ouCell2);
		adressTable.addCell(oudCell2);// 1:2
		adressTable.addCell(addCell2);// 1:2

		PdfPCell telCell = new PdfPCell(cellBorderlessStyle);
		telCell.setPhrase(new Phrase(new Chunk("Tel: "+site.getPhone() +" Fax : "+ site.getFax(), boddyStyle)));
		telCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		PdfPCell ferCell = new PdfPCell(cellBorderlessStyle);
		ferCell.setPhrase(new Phrase(new Chunk("DATE FERMETURE", headerStyle)));
		ferCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell fedrCell = new PdfPCell(cellBorderlessStyle);
		adressTable.addCell(ferCell);
		if (caisse.getDateFemeture()!=null) {
			fedrCell.setPhrase(new Phrase(new Chunk(PharmaDateUtil.format(caisse.getDateFemeture(), "dd-MM-yyyy HH:mm"), boddyStyle)));
			fedrCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			adressTable.addCell(fedrCell);// 1:2

		}else {
			adressTable.addCell(emptyCell);// 1:2

		}
		adressTable.addCell(telCell);// 1:2


		PdfPCell emailCell = new PdfPCell(cellBorderlessStyle);
		emailCell.setPhrase(new Phrase(new Chunk("Email : "+site.getEmail(), boddyStyle)));
		emailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell encCell = new PdfPCell(cellBorderlessStyle);
		encCell.setPhrase(new Phrase(new Chunk("FERME PAR :", headerStyle)));
		encCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell encmCell = new PdfPCell(cellBorderlessStyle);
		encmCell.setPhrase(new Phrase(new Chunk(""+caisse.getFermerPar(), boddyStyle)));
		encmCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable.addCell(encCell);
		adressTable.addCell(encmCell);// 1:2
		adressTable.addCell(emailCell);// 1:2

		PdfPCell decCell = new PdfPCell(cellBorderlessStyle);
		decCell.setPhrase(new Phrase(new Chunk("CAISSE OUVERTE :", headerStyle)));
		decCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell decmCell = new PdfPCell(cellBorderlessStyle);
		decmCell.setPhrase(new Phrase(new Chunk(caisse.getCaisseOuverte()?"OUI":"NON", boddyStyle)));
		decmCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		adressTable.addCell(decCell);
		adressTable.addCell(decmCell);// 1:2
		adressTable.addCell(emptyCell);// 1:2


		try {
			document.add(adressTable);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PdfPTable adressTable1 = new PdfPTable(adColumnsWith);
		adressTable1.setWidthPercentage(100);

		try {
			document.add(adressTable1);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PdfPTable spaceTable = new PdfPTable(1);
		spaceTable.addCell(emptyCell);
		spaceTable.setSpacingAfter(5);
		try {
			document.add(spaceTable);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		float[] colWidths = {1.5f,1.5f, 1.5f, 1.5f ,1.5f,1.5f};
		PdfPTable table = new PdfPTable(colWidths);
		table.setWidthPercentage(60);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.setHeaderRows(1);

		headerStyle.setColor(Color.WHITE);

		PdfPCell cipMCell = new PdfPCell(cellStyle);
		cipMCell.setPhrase(new Phrase(new Chunk("FILIALES", headerStyle)));
		cipMCell.setBackgroundColor(Color.gray);
		cipMCell.setPaddingBottom(5);
		table.addCell(cipMCell );


		PdfPCell cipCell = new PdfPCell(cellStyle);
		cipCell.setPhrase(new Phrase(new Chunk("Nb CLIENTS", headerStyle)));
		cipCell.setBackgroundColor(Color.gray);
		cipCell.setPaddingBottom(5);

		table.addCell(cipCell);
		
		PdfPCell paCell = new PdfPCell(cellStyle);
		paCell.setPhrase(new Phrase(new Chunk(" ACHAT", headerStyle)));
		paCell.setBackgroundColor(Color.gray);
		paCell.setPaddingBottom(5);

		table.addCell(paCell);

		PdfPCell desCell = new PdfPCell(cellStyle);
		desCell.setPhrase(new Phrase(new Chunk(" VENTE", headerStyle)));
		desCell.setBackgroundColor(Color.gray);
		desCell.setPaddingBottom(5);

		table.addCell(desCell);


		PdfPCell datePrmCell = new PdfPCell(cellStyle);
		datePrmCell.setPhrase(new Phrase(new Chunk(" REMISE", headerStyle)));
		datePrmCell.setBackgroundColor(Color.gray);
		datePrmCell.setPaddingBottom(5);

		table.addCell(datePrmCell);

		PdfPCell pvCell = new PdfPCell(cellStyle);
		pvCell.setPhrase(new Phrase(new Chunk("MARGE", headerStyle)));
		pvCell.setBackgroundColor(Color.gray);
		pvCell.setPaddingBottom(5);
		table.addCell(pvCell);
		

		List<Object[]> etatCaisse = Caisse.findEtatCaisse(caisse.getCaisseNumber());
		 String marqueur = "" ;
		 BigInteger  totalAchat = BigInteger.ZERO ;
         BigInteger  totalVente = BigInteger.ZERO ;
         BigInteger  totalremise = BigInteger.ZERO ;
         BigDecimal  totalmarge = BigDecimal.ZERO;
         Long  totalclient = new Long(0) ;
		for (Object[] info : etatCaisse) {
                     String filale = (String) info[0];
                     Long  nbClient = (tauxChiffreAffaire.multiply(new BigDecimal((Long) info[1]))).longValue();
                     BigInteger  montantAchat = (tauxChiffreAffaire.multiply(new BigDecimal((BigInteger) info[2]))).toBigInteger();
                     BigInteger  montantVente =(tauxChiffreAffaire.multiply(new BigDecimal((BigInteger) info[3]))).toBigInteger();
                     BigInteger  remise = (tauxChiffreAffaire.multiply(new BigDecimal((BigInteger) info[4]))).toBigInteger();
                     BigDecimal  marge =  BigDecimal.valueOf(montantVente.longValue()).subtract(BigDecimal.valueOf(montantAchat.longValue()));
                   //marge = marge.divide(BigDecimal.valueOf(montantAchat.longValue()));
                     totalAchat = totalAchat.add(montantAchat); 
                     totalVente = totalVente.add(montantVente);
                     totalremise = totalremise.add(remise);
                     totalclient = totalclient + nbClient;
                     totalmarge = totalmarge.add(marge);
                     
				

				//date op
				PdfPCell inCipCell = new PdfPCell(cellBorderless);
				inCipCell.setPhrase(new Phrase(new Chunk(filale.toUpperCase(), boddyStyle)));
				inCipCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				inCipCell.setPaddingBottom(5);
				table.addCell(inCipCell);
				
				//op number
				PdfPCell inCipm = new PdfPCell(cellBorderless);
				inCipm.setPhrase(new Phrase(new Chunk(""+nbClient, boddyStyle)));
				inCipm.setPaddingBottom(5);
				inCipm.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(inCipm);

				//type op
				PdfPCell inDesCell = new PdfPCell(cellBorderless);
				inDesCell.setPhrase(new Phrase(new Chunk(montantAchat.toString(), boddyStyle)));
				inDesCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(inDesCell);

				// mode paiement
				PdfPCell inprmCell = new PdfPCell(cellBorderless);
				inprmCell.setPhrase(new Phrase(new Chunk(montantVente.toString(), boddyStyle)));
				inprmCell.setPaddingBottom(5);
				inprmCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(inprmCell);

				// montant paiement
				PdfPCell inPvCell = new PdfPCell(cellBorderless);
				inPvCell.setPhrase(new Phrase(new Chunk(""+remise, boddyStyle)));
				inPvCell.setPaddingBottom(5);
				inPvCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(inPvCell);

				// raison Op
				PdfPCell inPaCell = new PdfPCell(cellBorderless);
				inPaCell.setPhrase(new Phrase(new Chunk(""+marge, boddyStyle)));
				inPaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table.addCell(inPaCell);
				inPaCell.setPaddingBottom(5);

			//}
		}

		cellStyle.setBorderWidth(0);
		PdfPCell cipMCell1 = new PdfPCell(cellStyle);
		cipMCell1.setPhrase(new Phrase(new Chunk("TOTAL :", headerStyles)));
		cipMCell1.setPaddingBottom(5);
		cipMCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

		table.addCell(cipMCell1 );


		PdfPCell cipCell2 = new PdfPCell(cellStyle);
		cipCell2.setPhrase(new Phrase(new Chunk(""+totalclient, headerStyles)));
		cipCell2.setBorder(0);
		cipCell2.setPaddingBottom(5);
		cipCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cipCell2);

		PdfPCell desCell1 = new PdfPCell(cellStyle);
		desCell1.setPhrase(new Phrase(new Chunk(""+totalAchat, headerStyles)));
		desCell1.setPaddingBottom(5);
		desCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

		table.addCell(desCell1);


		PdfPCell datePrmCell1 = new PdfPCell(cellStyle);
		datePrmCell1.setPhrase(new Phrase(new Chunk(""+totalVente, headerStyles)));
		datePrmCell1.setPaddingBottom(5);
		datePrmCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

		table.addCell(datePrmCell1);

		PdfPCell pvCell1 = new PdfPCell(cellStyle);
		pvCell1.setPhrase(new Phrase(new Chunk(""+totalremise, headerStyles)));
		pvCell1.setPaddingBottom(5);
		pvCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

		table.addCell(pvCell1);


		PdfPCell paCell1 = new PdfPCell(cellStyle);
		paCell1.setPhrase(new Phrase(new Chunk(""+totalmarge, headerStyles)));
		paCell1.setPaddingBottom(5);
		paCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

		table.addCell(paCell1);
		try {
			document.add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
// table detail de la caisse header		
		float[] colWidthsDetail = {1.5f,1.5f, 1.5f, 1.5f ,1.5f,1.5f,1.5f,1.5f};

		PdfPTable tableDetail = new PdfPTable(11);
		tableDetail.setWidthPercentage(100);
		tableDetail.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableDetail.setHeaderRows(1);
		tableDetail.setSpacingAfter(10);
		tableDetail.setSpacingBefore(5);
		headerStyle.setColor(Color.WHITE);

		PdfPCell font = new PdfPCell(cellStyle);
		font.setPhrase(new Phrase(new Chunk("Fond", headerStyle)));
		font.setBackgroundColor(Color.gray);
		font.setPaddingBottom(5);
		tableDetail.addCell(font );


		PdfPCell enc = new PdfPCell(cellStyle);
		enc.setPhrase(new Phrase(new Chunk("Encaissement", headerStyle)));
		enc.setBackgroundColor(Color.gray);
		enc.setPaddingBottom(5);

		tableDetail.addCell(enc);

		PdfPCell retrait = new PdfPCell(cellStyle);
		retrait.setPhrase(new Phrase(new Chunk("Remise GLB", headerStyle)));
		retrait.setBackgroundColor(Color.gray);
		retrait.setPaddingBottom(5);

		tableDetail.addCell(retrait);


		PdfPCell cash = new PdfPCell(cellStyle);
		cash.setPhrase(new Phrase(new Chunk("Cash", headerStyle)));
		cash.setBackgroundColor(Color.gray);
		cash.setPaddingBottom(5);
		tableDetail.addCell(cash);
		
		PdfPCell cashdette = new PdfPCell(cellStyle);
		cashdette.setPhrase(new Phrase(new Chunk("Cash dette", headerStyle)));
		cashdette.setBackgroundColor(Color.gray);
		cashdette.setPaddingBottom(5);
		tableDetail.addCell(cashdette);

		PdfPCell cb = new PdfPCell(cellStyle);
		cb.setPhrase(new Phrase(new Chunk("Carte Bq", headerStyle)));
		cb.setBackgroundColor(Color.gray);
		cb.setPaddingBottom(5);
		tableDetail.addCell(cb);
		
		PdfPCell cheque = new PdfPCell(cellStyle);
		cheque.setPhrase(new Phrase(new Chunk("Cheque", headerStyle)));
		cheque.setBackgroundColor(Color.gray);
		cheque.setPaddingBottom(5);

		tableDetail.addCell(cheque);
		
		PdfPCell credit = new PdfPCell(cellStyle);
		credit.setPhrase(new Phrase(new Chunk("Decaissement", headerStyle)));
		credit.setBackgroundColor(Color.gray);
		credit.setPaddingBottom(5);

		tableDetail.addCell(credit);
		
		PdfPCell bc = new PdfPCell(cellStyle);
		bc.setPhrase(new Phrase(new Chunk("Bon-Medi", headerStyle)));
		bc.setBackgroundColor(Color.gray);
		bc.setPaddingBottom(5);

		tableDetail.addCell(bc);
		
		
		PdfPCell bclt = new PdfPCell(cellStyle);
		bclt.setPhrase(new Phrase(new Chunk("Bon-Avoir", headerStyle)));
		bclt.setBackgroundColor(Color.gray);
		bclt.setPaddingBottom(5);

		tableDetail.addCell(bclt);
		

		PdfPCell bcp = new PdfPCell(cellStyle);
		bcp.setPhrase(new Phrase(new Chunk("Solde", headerStyle)));
		bcp.setBackgroundColor(Color.gray);
		bcp.setPaddingBottom(5);

		tableDetail.addCell(bcp);
		
	// contenu
		PdfPCell fontc = new PdfPCell(cellStyle);
		BigDecimal fond = caisse.getFondCaisse()==null?BigDecimal.ZERO:caisse.getFondCaisse();
		fondgbl = fondgbl.add(fond);
		fontc.setPhrase(new Phrase(new Chunk(""+fond.intValue(), headerStyles)));
		fontc.setPaddingBottom(5);
		tableDetail.addCell(fontc );


		PdfPCell encv = new PdfPCell(cellStyle);
		totalencgbl=totalencgbl.add(caisse.getTotalEncaissement());
		encv.setPhrase(new Phrase(new Chunk(""+caisse.getTotalEncaissement().intValue(), headerStyles)));
		encv.setPaddingBottom(5);

		tableDetail.addCell(encv);
	    BigDecimal remglb = caisse.getTotalProformat().subtract(BigDecimal.valueOf(totalremise.longValue()));
	    remglbs= remglbs.add(remglb) ;
		PdfPCell retraitv = new PdfPCell(cellStyle);
		retraitv.setPhrase(new Phrase(new Chunk(""+remglb.intValue(), headerStyles)));
		retraitv.setPaddingBottom(5);

		tableDetail.addCell(retraitv);


		PdfPCell cashv = new PdfPCell(cellStyle);
		cashgbl=cashgbl.add(caisse.getTotalCash());
		cashv.setPhrase(new Phrase(new Chunk(""+caisse.getTotalCash().intValue(), headerStyles)));
		cashv.setPaddingBottom(5);
		tableDetail.addCell(cashv);
		
		PdfPCell cashdv = new PdfPCell(cellStyle);
		cashdgbl=cashdgbl.add(caisse.getTotalCashDette()==null?BigDecimal.ZERO:caisse.getTotalCashDette());
		cashdv.setPhrase(new Phrase(new Chunk(""+(caisse.getTotalCashDette()==null?BigDecimal.ZERO.intValue():caisse.getTotalCashDette().intValue()), headerStyles)));
		cashdv.setPaddingBottom(5);
		tableDetail.addCell(cashdv);

		PdfPCell cbv = new PdfPCell(cellStyle);
		cartecreditgbl=cartecreditgbl.add(caisse.getTotalCarteCredit());
		cbv.setPhrase(new Phrase(new Chunk(""+caisse.getTotalCarteCredit().intValue(), headerStyles)));
		cbv.setPaddingBottom(5);

		tableDetail.addCell(cbv);
		
		PdfPCell chequev = new PdfPCell(cellStyle);
		chequegbl=chequegbl.add(caisse.getTotalCheque());
		chequev.setPhrase(new Phrase(new Chunk(""+caisse.getTotalCheque().intValue(), headerStyles)));
		chequev.setPaddingBottom(5);

		tableDetail.addCell(chequev);
		
		PdfPCell creditv = new PdfPCell(cellStyle);
		retraitgbl=retraitgbl.add(caisse.getTotalRetrait());
		creditv.setPhrase(new Phrase(new Chunk(""+caisse.getTotalRetrait().intValue(), headerStyles)));
		creditv.setPaddingBottom(5);

		tableDetail.addCell(creditv);
		

		PdfPCell tbc = new PdfPCell(cellStyle);
		boncmdgbl=boncmdgbl.add(caisse.getTotalBonCmd());
		tbc.setPhrase(new Phrase(new Chunk(""+caisse.getTotalBonCmd().intValue(), headerStyles)));
		tbc.setPaddingBottom(5);

		tableDetail.addCell(tbc);
		

		

		PdfPCell tbclt = new PdfPCell(cellStyle);
		boncltgbl=boncltgbl.add(caisse.getTotalBonClient());
		tbclt.setPhrase(new Phrase(new Chunk(""+caisse.getTotalBonClient().intValue(), headerStyles)));
		tbclt.setPaddingBottom(5);

		tableDetail.addCell(tbclt);
		

		PdfPCell tbsolde = new PdfPCell(cellStyle);
		soldegbl=soldegbl.add(caisse.calculateSolde());
		tbsolde.setPhrase(new Phrase(new Chunk(""+caisse.calculateSolde().intValue(), headerStyles)));
		tbsolde.setPaddingBottom(5);

		tableDetail.addCell(tbsolde);
		
		
		try {
			document.add(tableDetail);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			document.add(new Paragraph(new  Phrase(new Chunk("*****************************************************************************************************************************************", boddyStyle))));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}
	
	public void addGlobalAmountToDocument(Document document ){
		Font boddyStyle = new Font(Font.COURIER,8);
		boddyStyle.setColor(Color.BLUE) ;
		Font headerStyle = new Font(Font.COURIER,7);
		headerStyle.setStyle("bold");
		Font headerStyles = new Font(Font.COURIER,7);
		headerStyles.setStyle("bold");
		
		PdfPCell cellStyle = new PdfPCell();
		cellStyle.setPadding(.5f);
		cellStyle.setHorizontalAlignment(Element.ALIGN_CENTER);
		try {
			document.add(new Paragraph(new  Phrase(new Chunk(" ==================================================================DEBUT RESUME=========================================", boddyStyle))));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PdfPTable tablegbl = new PdfPTable(11); 
		tablegbl.setWidthPercentage(100);
		tablegbl.setHorizontalAlignment(Element.ALIGN_CENTER);
		tablegbl.setHeaderRows(1);
		tablegbl.setSpacingAfter(10);
		tablegbl.setSpacingBefore(5);
		headerStyle.setColor(Color.WHITE);

		PdfPCell font = new PdfPCell(cellStyle);
		
		font.setPhrase(new Phrase(new Chunk("Fond", headerStyle)));
		font.setBackgroundColor(Color.gray);
		font.setPaddingBottom(5);
		tablegbl.addCell(font );


		PdfPCell enc = new PdfPCell(cellStyle);
		enc.setPhrase(new Phrase(new Chunk("Encaissement", headerStyle)));
		enc.setBackgroundColor(Color.gray);
		enc.setPaddingBottom(5);

		tablegbl.addCell(enc);

		PdfPCell retrait = new PdfPCell(cellStyle);
		retrait.setPhrase(new Phrase(new Chunk("Remise GLB", headerStyle)));
		retrait.setBackgroundColor(Color.gray);
		retrait.setPaddingBottom(5);

		tablegbl.addCell(retrait);


		PdfPCell cash = new PdfPCell(cellStyle);
		cash.setPhrase(new Phrase(new Chunk("Cash", headerStyle)));
		cash.setBackgroundColor(Color.gray);
		cash.setPaddingBottom(5);
		tablegbl.addCell(cash);
		
		PdfPCell cashd = new PdfPCell(cellStyle);
		cashd.setPhrase(new Phrase(new Chunk("Cash dette", headerStyle)));
		cashd.setBackgroundColor(Color.gray);
		cashd.setPaddingBottom(5);
		tablegbl.addCell(cashd);

		PdfPCell cb = new PdfPCell(cellStyle);
		cb.setPhrase(new Phrase(new Chunk("Carte B", headerStyle)));
		cb.setBackgroundColor(Color.gray);
		cb.setPaddingBottom(5);

		tablegbl.addCell(cb);
		PdfPCell cheque = new PdfPCell(cellStyle);
		cheque.setPhrase(new Phrase(new Chunk("Cheque", headerStyle)));
		cheque.setBackgroundColor(Color.gray);
		cheque.setPaddingBottom(5);

		tablegbl.addCell(cheque);
		
		PdfPCell credit = new PdfPCell(cellStyle);
		credit.setPhrase(new Phrase(new Chunk("Decaissement", headerStyle)));
		credit.setBackgroundColor(Color.gray);
		credit.setPaddingBottom(5);

		tablegbl.addCell(credit);
		
		PdfPCell bc = new PdfPCell(cellStyle);
		bc.setPhrase(new Phrase(new Chunk("Bon-Medi", headerStyle)));
		bc.setBackgroundColor(Color.gray);
		bc.setPaddingBottom(5);

		tablegbl.addCell(bc);
		
		
		PdfPCell bclt = new PdfPCell(cellStyle);
		bclt.setPhrase(new Phrase(new Chunk("Bon-Avoir", headerStyle)));
		bclt.setBackgroundColor(Color.gray);
		bclt.setPaddingBottom(5);

		tablegbl.addCell(bclt);
		

		PdfPCell bcp = new PdfPCell(cellStyle);
		bcp.setPhrase(new Phrase(new Chunk("Solde", headerStyle)));
		bcp.setBackgroundColor(Color.gray);
		bcp.setPaddingBottom(5);

		tablegbl.addCell(bcp);
		
	// contenu
		PdfPCell fontc = new PdfPCell(cellStyle);
		fontc.setPhrase(new Phrase(new Chunk(""+fondgbl.intValue(), headerStyles)));
		fontc.setPaddingBottom(5);
		tablegbl.addCell(fontc );


		PdfPCell encv = new PdfPCell(cellStyle);	
		encv.setPhrase(new Phrase(new Chunk(""+totalencgbl.intValue(), headerStyles)));
		encv.setPaddingBottom(5);

		tablegbl.addCell(encv);

		PdfPCell retraitv = new PdfPCell(cellStyle);
		retraitv.setPhrase(new Phrase(new Chunk(""+remglbs.intValue(), headerStyles)));
		retraitv.setPaddingBottom(5);

		tablegbl.addCell(retraitv);


		PdfPCell cashv = new PdfPCell(cellStyle);
		cashv.setPhrase(new Phrase(new Chunk(""+cashgbl.intValue(), headerStyles)));
		cashv.setPaddingBottom(5);

		tablegbl.addCell(cashv);
		
		PdfPCell cashdv = new PdfPCell(cellStyle);
		cashdv.setPhrase(new Phrase(new Chunk(""+cashdgbl.intValue(), headerStyles)));
		cashdv.setPaddingBottom(5);

		tablegbl.addCell(cashdv);

		PdfPCell cbv = new PdfPCell(cellStyle);
		cbv.setPhrase(new Phrase(new Chunk(""+cartecreditgbl.intValue(), headerStyles)));
		cbv.setPaddingBottom(5);

		tablegbl.addCell(cbv);
		
		PdfPCell chequev = new PdfPCell(cellStyle);
		chequev.setPhrase(new Phrase(new Chunk(""+chequegbl.intValue(), headerStyles)));
		chequev.setPaddingBottom(5);

		tablegbl.addCell(chequev);
		
		PdfPCell creditv = new PdfPCell(cellStyle);
		creditv.setPhrase(new Phrase(new Chunk(""+retraitgbl.intValue(), headerStyles)));
		creditv.setPaddingBottom(5);

		tablegbl.addCell(creditv);
		PdfPCell tbc = new PdfPCell(cellStyle);
		tbc.setPhrase(new Phrase(new Chunk(""+boncmdgbl.intValue(), headerStyles)));
		tbc.setPaddingBottom(5);
		
		tablegbl.addCell(tbc);
		PdfPCell tbclt = new PdfPCell(cellStyle);
		tbclt.setPhrase(new Phrase(new Chunk(""+boncltgbl.intValue(), headerStyles)));
		tbclt.setPaddingBottom(5);

		tablegbl.addCell(tbclt);
		PdfPCell tbsolde = new PdfPCell(cellStyle);
		tbsolde.setPhrase(new Phrase(new Chunk(""+soldegbl.intValue(), headerStyles)));
		tbsolde.setPaddingBottom(5);

		tablegbl.addCell(tbsolde);
		
		
		try {
			document.add(tablegbl);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			document.add(new Paragraph(new  Phrase(new Chunk("=================================================================FIN RESUME=========================================", boddyStyle))));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resetVariables();
		
	}
	
	
	
// Reinitialisation des variables du component 
	public void resetVariables(){
		fondgbl= BigDecimal.ZERO;
		totalencgbl = BigDecimal.ZERO ;
		remglbs = BigDecimal.ZERO ;
		cashgbl = BigDecimal.ZERO ;
		cashdgbl = BigDecimal.ZERO ;
		cartecreditgbl = BigDecimal.ZERO ;
		chequegbl = BigDecimal.ZERO ;
		retraitgbl = BigDecimal.ZERO ;
		boncmdgbl = BigDecimal.ZERO ;
		boncltgbl = BigDecimal.ZERO ;
		soldegbl = BigDecimal.ZERO ;
	}

	@Override
	protected void buildPdfMetadata(Map<String, Object> model,

			Document document, HttpServletRequest request) {
		Caisse caisse = (Caisse) model.get("caisse");
		String periode = (String) model.get("periode");
		List<Caisse> caisses = (List<Caisse>) model.get("caisses");
		
		document.setPageSize(PageSize.A4);
		document.setMargins(5, 5, 25, 5);
		Font boddyStyle = new Font(Font.COURIER,6);
		Font headerStyle = new Font(Font.COURIER,14);
		headerStyle.setStyle(Font.BOLD);


		HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  " Edite Par : "+SecurityUtil.getUserName()+" LE : "+PharmaDateUtil.format(new Date(), PharmaDateUtil.DATE_PATTERN_LONG_LIT) , boddyStyle)), true);
		footer.setAlignment(Element.ALIGN_CENTER);
		document.setFooter(footer);
		HeaderFooter header = null ;
		if (caisse !=null) {
			header	= new HeaderFooter(new Phrase(new Chunk("ETAT DE CAISSE ", headerStyle)), false);

		}else {
			header	= new HeaderFooter(new Phrase(new Chunk("ETAT DES CAISSES DU "+periode, headerStyle)), false);

		}

		header.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header);
		//Desktop.getDesktop().print(document.)
		super.buildPdfMetadata(model, document, request);
	}
}
