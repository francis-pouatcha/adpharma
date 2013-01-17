package org.adorsys.adpharma.beans;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.AvoirClient;
import org.adorsys.adpharma.domain.CommandeClient;
import org.adorsys.adpharma.domain.Facture;
import org.adorsys.adpharma.domain.LigneFacture;
import org.adorsys.adpharma.domain.Paiement;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.domain.TypeBon;
import org.adorsys.adpharma.domain.TypeCommande;
import org.adorsys.adpharma.domain.TypePaiement;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("ticketPdfDocView")

public class TicketPdfDocView  extends   AbstractPdfView {
	//le contenu est a revoir 

		@Override
		protected void buildPdfDocument(Map<String, Object> model,
				Document document, PdfWriter writer, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			Paiement pay= (Paiement) model.get("paiement");
			if(pay == null) {
				document.add(new Phrase("\n\n\n aucun ticket trouve !"));
				return ;
			}
			Site site = Site.findSite(Long.valueOf(1));
          //  writer.addJavaScript("this.print(true);",false); 
			Facture facture = pay.getFacture();
			CommandeClient commande = pay.getFacture().getCommande();

			Font headerStyle = new Font(Font.TIMES_ROMAN,10);
			headerStyle.setStyle("bold");
			Font boddyStyle = new Font(Font.TIMES_ROMAN,10);
			
			Font footStyle = new Font(Font.TIMES_ROMAN,10);
			
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
			float[] ColumnsWith = {1.7f, 3.3f, .5f,.9f};
			float[] payColumnsWith = {1.4f, 2f};

			PdfPTable adressTable = new PdfPTable(1);
			adressTable.setWidthPercentage(100);
			
			PdfPTable bonTable = new PdfPTable(1);
			bonTable.setWidthPercentage(100);
			
			
			PdfPTable conTable = new PdfPTable(ColumnsWith);
			conTable.setWidthPercentage(100);
			
			PdfPTable payTable = new PdfPTable(payColumnsWith);
			payTable.setWidthPercentage(100);
			
			PdfPTable footTable = new PdfPTable(1);
			payTable.setWidthPercentage(100);
			
			//head of ticket

			PdfPCell headCell = new PdfPCell(cellBorderlessStyle);
			headCell.setPhrase(new Phrase(new Chunk("TICKET DE CAISSE "+pay.getFacture().getCommande().getCmdNumber().toUpperCase() ,headerStyle)));
			headCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			adressTable.addCell(headCell);// 
			
			PdfPCell lineCell = new PdfPCell(cellBorderlessStyle);
			lineCell.setPhrase(new Phrase(new Chunk("-------------------------------------------", boddyStyle)));
			lineCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			lineCell.setPaddingBottom(2);
			adressTable.addCell(lineCell);  
			
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
			
			PdfPCell lineCellh = new PdfPCell(cellBorderlessStyle);
			lineCellh.setPhrase(new Phrase(new Chunk("-----------------------------------------------", boddyStyle)));
			lineCellh.setHorizontalAlignment(Element.ALIGN_CENTER);
			lineCellh.setPaddingBottom(2);
			adressTable.addCell(lineCellh);
			
			//ticket information
			PdfPCell tnCell = new PdfPCell(cellBorderlessStyle);
			tnCell.setPhrase(new Phrase(new Chunk("TICKET: "+ pay.getPaiementNumber()+" du "+PharmaDateUtil.format(pay.getDateSaisie(),"dd-MM-yyyy hh:mm"), boddyStyle)));
			tnCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			adressTable.addCell(tnCell);// 
			PdfPCell caiCell1 = new PdfPCell(cellBorderlessStyle);
			caiCell1.setPhrase(new Phrase(new Chunk("CAISSIER: "+pay.getCassier().getDisplayName(), boddyStyle)));
			caiCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			adressTable.addCell(caiCell1);
			PdfPCell salCell2 = new PdfPCell(cellBorderlessStyle);
			salCell2.setPhrase(new Phrase(new Chunk("VENDEUR: "+pay.getFacture().getVendeur().getUserName(), boddyStyle)));
			salCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			adressTable.addCell(salCell2);
		
			PdfPCell clienCell = new PdfPCell(cellBorderlessStyle);
			clienCell.setPhrase(new Phrase(new Chunk("CLIENT : "+commande.getClient().displayName(), boddyStyle)));
			clienCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			adressTable.addCell(clienCell);
			if (commande.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT) && !commande.getVentePartiel()) {
				PdfPCell payeurCell = new PdfPCell(cellBorderlessStyle);
				payeurCell.setPhrase(new Phrase(new Chunk("PAYEUR : "+commande.getClientPaiyeur().displayName(), boddyStyle)));
				payeurCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				adressTable.addCell(payeurCell);

			}
			adressTable.addCell(lineCell);
			document.add(adressTable);
			
			
			
			
			PdfPCell cipMCell = new PdfPCell(cellBorderlessStyle);
			cipMCell.setPhrase(new Phrase(new Chunk("CODE", headerStyle)));
			conTable.addCell(cipMCell );
			
			
			PdfPCell cipCell = new PdfPCell(cellBorderlessStyle);
			cipCell.setPhrase(new Phrase(new Chunk("DESIGNATION", headerStyle)));
			conTable.addCell(cipCell);
			
			PdfPCell desCell = new PdfPCell(cellBorderlessStyle);
			desCell.setPhrase(new Phrase(new Chunk("QT", headerStyle)));
			conTable.addCell(desCell);
			

			PdfPCell ptCell = new PdfPCell(cellBorderlessStyle);
			ptCell.setPhrase(new Phrase(new Chunk("PT", headerStyle)));
			conTable.addCell(ptCell);
			Font conStyle = new Font(Font.TIMES_ROMAN,9);


		
			Set<LigneFacture> ligneFacture = facture.getLineFacture();


if (!ligneFacture.isEmpty()) {
	

		for (LigneFacture line : ligneFacture) {	
			
			PdfPCell cipM1Cell = new PdfPCell(cellBorderlessStyle);
			cipM1Cell.setPhrase(new Phrase(new Chunk(line.getCipM(), conStyle)));
			conTable.addCell(cipM1Cell );
			
			
			PdfPCell cipCell1 = new PdfPCell(cellBorderlessStyle);
		     int lengt =	line.getDesignation().length(); 
		     if (lengt >21) {
					cipCell1.setPhrase(new Phrase(new Chunk(line.getDesignation().substring(0, 20).toUpperCase(), conStyle)));

			}else {
				cipCell1.setPhrase(new Phrase(new Chunk(line.getDesignation().toUpperCase(), conStyle)));

			}
			cipCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			conTable.addCell(cipCell1);
			
			PdfPCell desCell1 = new PdfPCell(cellBorderlessStyle);
			desCell1.setPhrase(new Phrase(new Chunk(line.getQteAchete().toString(), conStyle)));
			desCell1.setHorizontalAlignment(Element.ALIGN_CENTER);

			conTable.addCell(desCell1);
			

			PdfPCell ptCell1 = new PdfPCell(cellBorderlessStyle);
			ptCell1.setPhrase(new Phrase(new Chunk(line.getPrixTotal().toString(), conStyle)));
			ptCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);

			conTable.addCell(ptCell1);
					
			}
}


		PdfPCell lineCell2 = new PdfPCell(cellBorderlessStyle);
		lineCell2.setPhrase(new Phrase(new Chunk("-------------------------------------------", boddyStyle)));
		lineCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		lineCell2.setPaddingBottom(2);
		lineCell2.setColspan(4);
		conTable.addCell(lineCell2);
		document.add(conTable);
	
		//amounttable	
		PdfPCell payCell = new PdfPCell(cellBorderlessStyle);
		payCell.setPhrase(new Phrase(new Chunk("MODE PAIEMENT: ", boddyStyle)));
		payCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		payTable.addCell(payCell); 
		String text = "" ;
		String typePaiement = pay.getTypePaiement().equals(TypePaiement.BON_CMD_PARTIEL)?TypePaiement.BON_CMD.name():pay.getTypePaiement().name() ;
		if (pay.getTypePaiement().equals(TypePaiement.BON_CMD_PARTIEL)) {
			text =" NO:"+ commande.getNumeroBon();
		}
		if (pay.getTypePaiement().equals(TypePaiement.BON_AVOIR_CLIENT)) {
			text =" NO:"+ pay.getNumeroBon();
		}
		
		PdfPCell Cell1 = new PdfPCell(cellBorderlessStyle);
		Cell1.setPhrase(new Phrase(new Chunk(pay.getTypePaiement()+ "" + text, headerStyle)));
		Cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		payTable.addCell(Cell1);

		PdfPCell mtCell1 = new PdfPCell(cellBorderlessStyle);
		mtCell1.setPhrase(new Phrase(new Chunk("MONTANT Total: ", boddyStyle)));
		mtCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		payTable.addCell(mtCell1);

		PdfPCell Cell2 = new PdfPCell(cellBorderlessStyle);
		Cell2.setPhrase(new Phrase(new Chunk(facture.getMontantTotal().toString(), headerStyle)));
		Cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
		payTable.addCell(Cell2);
		PdfPCell remlCell2 = new PdfPCell(cellBorderlessStyle);
		remlCell2.setPhrase(new Phrase(new Chunk("REMISE : ", boddyStyle)));
		remlCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		payTable.addCell(remlCell2);
		
		PdfPCell Cell = new PdfPCell(cellBorderlessStyle);
		if (!pay.getReduction()) {
			Cell.setPhrase(new Phrase(new Chunk(BigDecimal.ZERO.toString(), headerStyle)));
		}else {
			Cell.setPhrase(new Phrase(new Chunk(facture.getMontantRemise().toString(), headerStyle)));

		}
		Cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		payTable.addCell(Cell);
		PdfPCell netCell = new PdfPCell(cellBorderlessStyle);
		netCell.setPhrase(new Phrase(new Chunk("NET A PAYER : ", boddyStyle)));
		netCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		payTable.addCell(netCell);
		
		
		PdfPCell netCell1 = new PdfPCell(cellBorderlessStyle);
		if (!pay.getReduction()) {
			netCell1.setPhrase(new Phrase(new Chunk(facture.getMontantTotal().toString(), headerStyle)));
		}else {
			netCell1.setPhrase(new Phrase(new Chunk(facture.getNetPayer().toString(), headerStyle)));

		}
		netCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		payTable.addCell(netCell1);
		
if (pay.getTypePaiement().equals(TypePaiement.BON_AVOIR_CLIENT)) {
	 BigDecimal soldeBon = BigDecimal.ZERO ;
	 if (pay.getMontantBon().intValue()>= pay.getMontant().intValue()) {
		soldeBon = pay.getMontantBon().subtract(pay.getMontant());
	  }
         	
			PdfPCell soldeCell = new PdfPCell(cellBorderlessStyle);
        	soldeCell.setPhrase(new Phrase(new Chunk("MONTANT Avoir: ", boddyStyle)));
        	soldeCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    		payTable.addCell(soldeCell);

    		PdfPCell solde = new PdfPCell(cellBorderlessStyle);
    		solde.setPhrase(new Phrase(new Chunk(""+pay.getMontantBon().intValue(), headerStyle)));
    		solde.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		payTable.addCell(solde);
    		
    		PdfPCell monCell = new PdfPCell(cellBorderlessStyle);
    		monCell.setPhrase(new Phrase(new Chunk("SOLDE AVOIR : ", boddyStyle)));
    		monCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    		payTable.addCell(monCell);

    		PdfPCell mont = new PdfPCell(cellBorderlessStyle);
    		mont.setPhrase(new Phrase(new Chunk(""+soldeBon.intValue(), headerStyle)));
    		mont.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		payTable.addCell(mont);
		
    		
    		
        
        }
		
     
		if (commande.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT) && commande.getVentePartiel().booleanValue()) {
        	
			PdfPCell soldeCell = new PdfPCell(cellBorderlessStyle);
        	soldeCell.setPhrase(new Phrase(new Chunk("MONTANT DU BON ", boddyStyle)));
        	soldeCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    		payTable.addCell(soldeCell);

    		PdfPCell solde = new PdfPCell(cellBorderlessStyle);
    		solde.setPhrase(new Phrase(new Chunk(""+facture.getReste(), headerStyle)));
    		solde.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		payTable.addCell(solde);
    		
    		PdfPCell monCell = new PdfPCell(cellBorderlessStyle);
    		monCell.setPhrase(new Phrase(new Chunk("TAUX DU BON  ", boddyStyle)));
    		monCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    		payTable.addCell(monCell);

    		PdfPCell mont = new PdfPCell(cellBorderlessStyle);
    		mont.setPhrase(new Phrase(new Chunk(""+commande.getTauxCouverture()+ "%", headerStyle)));
    		mont.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		payTable.addCell(mont);
		
    		
    		PdfPCell somCell = new PdfPCell(cellBorderlessStyle);
    		somCell.setPhrase(new Phrase(new Chunk("RESTE A PAYER  ", boddyStyle)));
    		somCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    		payTable.addCell(somCell);
    		

    		PdfPCell somCell1 = new PdfPCell(cellBorderlessStyle);
    		somCell1.setPhrase(new Phrase(new Chunk(""+pay.getMontant().intValue(), headerStyle)));
    		somCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		payTable.addCell(somCell1);
    		
    		
    		PdfPCell somrCell = new PdfPCell(cellBorderlessStyle);
    		somrCell.setPhrase(new Phrase(new Chunk("SOMME RECUE : ", boddyStyle)));
    		somrCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    		payTable.addCell(somrCell);
    		
    		
    		PdfPCell somrCell2 = new PdfPCell(cellBorderlessStyle);
    		somrCell2.setPhrase(new Phrase(new Chunk(""+pay.getSommeRecue().intValue(), headerStyle)));
    		somrCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		payTable.addCell(somrCell2);  
    		
    		PdfPCell diffCell1 = new PdfPCell(cellBorderlessStyle);
    		diffCell1.setPhrase(new Phrase(new Chunk("DIFFERENCE : ", boddyStyle)));
    		diffCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
    		payTable.addCell(diffCell1);
    		
    		
    		PdfPCell diffCell2 = new PdfPCell(cellBorderlessStyle);
    		diffCell2.setPhrase(new Phrase(new Chunk(""+pay.getDifference().intValue(), headerStyle)));
    		diffCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		payTable.addCell(diffCell2);
        
        }
		
if (commande.getTypeCommande().equals(TypeCommande.VENTE_A_CREDIT) && !commande.getVentePartiel().booleanValue()) {
    		
    		PdfPCell somrCell = new PdfPCell(cellBorderlessStyle);
    		somrCell.setPhrase(new Phrase(new Chunk("SOMME RECUE : ", boddyStyle)));
    		somrCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    		payTable.addCell(somrCell);
    		
    		
    		PdfPCell somrCell2 = new PdfPCell(cellBorderlessStyle);
    		somrCell2.setPhrase(new Phrase(new Chunk(""+pay.getAvancePartClient(), headerStyle)));
    		somrCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		payTable.addCell(somrCell2);  
    		
    		PdfPCell diffCell1 = new PdfPCell(cellBorderlessStyle);
    		diffCell1.setPhrase(new Phrase(new Chunk("RESTE A PAYER : ", boddyStyle)));
    		diffCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
    		payTable.addCell(diffCell1);
    		PdfPCell diffCell2 = new PdfPCell(cellBorderlessStyle);
    		diffCell2.setPhrase(new Phrase(new Chunk(""+pay.getRestePartClient(), headerStyle)));
    		diffCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		payTable.addCell(diffCell2);
    		
        
        }

if (commande.getTypeCommande().equals(TypeCommande.VENTE_AU_PUBLIC) ) {
	
	PdfPCell somrCell = new PdfPCell(cellBorderlessStyle);
	somrCell.setPhrase(new Phrase(new Chunk("SOMME RECUE : ", boddyStyle)));
	somrCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	payTable.addCell(somrCell);
	
	
	PdfPCell somrCell2 = new PdfPCell(cellBorderlessStyle);
	somrCell2.setPhrase(new Phrase(new Chunk(""+pay.getSommeRecue().intValue(), headerStyle)));
	somrCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	payTable.addCell(somrCell2);  
	
	PdfPCell diffCell1 = new PdfPCell(cellBorderlessStyle);
	diffCell1.setPhrase(new Phrase(new Chunk("DIFFERENCE : ", boddyStyle)));
	diffCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
	payTable.addCell(diffCell1);
	
	
	PdfPCell diffCell2 = new PdfPCell(cellBorderlessStyle);
	if (!pay.getReduction()) {
		diffCell2.setPhrase(new Phrase(new Chunk(pay.getSommeRecue().subtract(BigDecimal.valueOf(facture.getMontantTotal().longValue())).toBigInteger().toString(), headerStyle)));
	}else {
		diffCell2.setPhrase(new Phrase(new Chunk(""+pay.getDifference().intValue(), headerStyle)));

	}
	diffCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	payTable.addCell(diffCell2);
	

}
		
		PdfPCell lineCell3 = new PdfPCell(cellBorderlessStyle);
		lineCell3.setPhrase(new Phrase(new Chunk("---------------------------------------------", boddyStyle)));
		lineCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		lineCell3.setPaddingBottom(2);
		lineCell3.setColspan(2);
		payTable.addCell(lineCell3);
		document.add(payTable);
		
		//footer table
		/*PdfPCell msgCell = new PdfPCell(cellBorderlessStyle);
		msgCell.setPhrase(new Phrase(new Chunk("Votre Pharmacien a votre ecoute : ", footStyle)));
		msgCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		footTable.addCell(msgCell);// 
		
		PdfPCell msgCell1 = new PdfPCell(cellBorderlessStyle);
		msgCell1.setPhrase(new Phrase(new Chunk("MERCI DE VOTRE CONFIANCE... BONNE JOURNEE !", footStyle)));
		msgCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		footTable.addCell(msgCell1);
		
		PdfPCell msgCell2 = new PdfPCell(cellBorderlessStyle);
		msgCell2.setPhrase(new Phrase(new Chunk("BONNE GUERISON ", headerStyle)));
		msgCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		footTable.addCell(msgCell2);
		
		PdfPCell msgCell3 = new PdfPCell(cellBorderlessStyle);
		msgCell3.setPhrase(new Phrase(new Chunk("Les Produits Vendus ne sont ni CHANGES ni RETOURNES !", boddyStyle)));
		msgCell3.setHorizontalAlignment(Element.ALIGN_LEFT);
		footTable.addCell(msgCell3);*/
		 String[] split = site.getMessageTiket().split(":") ;
		 for (String mesg : split) {
				document.add(new Paragraph(new  Phrase(new Chunk(mesg, boddyStyle))));

		}
		 
		 if (pay.getTypePaiement().equals(TypePaiement.BON_AVOIR_CLIENT)) {
			bonTable.addCell(lineCell);
			AvoirClient bonAvoir= AvoirClient.findAvoirClientsByNumeroEquals(pay.getNumeroBon()).getResultList().iterator().next();
			
			// addresse de la pharmacie
			//float[] ColumnsWiths = {1.4f, 3.4f, .6f,1f};
			//float[] payColumnsWith = {2f, 2f};
			
			
			PdfPCell titre = new PdfPCell(cellBorderlessStyle);
			titre.setPhrase(new Phrase(new Chunk("BON AVOIR CLIENT ", headerStyle)));
			titre.setHorizontalAlignment(Element.ALIGN_CENTER);
			bonTable.addCell(titre);// 
			bonTable.addCell(lineCell);

			//head of ticket
			bonTable.addCell(senderCell);// 
			
			
		
			bonTable.addCell(addCell1);
			bonTable.addCell(addCell2);
			bonTable.addCell(telCell);
			
			bonTable.addCell(emailCell);
			
			bonTable.addCell(regCell);
			
			PdfPCell emptyCell = new PdfPCell(cellBorderlessStyle);
			emptyCell.setPhrase(new Phrase(new Chunk("   ", boddyStyle)));
			emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			emptyCell.setPaddingBottom(2);
			bonTable.addCell(emptyCell);
			bonTable.addCell(lineCell);
			
			//ticket information
			PdfPCell agCell = new PdfPCell(cellBorderlessStyle);
			agCell.setPhrase(new Phrase(new Chunk("Agent : "+ bonAvoir.getAgent(), boddyStyle)));
			agCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			bonTable.addCell(agCell);// 
			PdfPCell nbCell1 = new PdfPCell(cellBorderlessStyle);
			nbCell1.setPhrase(new Phrase(new Chunk("NUMERO DU BON : "+bonAvoir.getNumero(),boddyStyle)));
			nbCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			bonTable.addCell(nbCell1);
			PdfPCell mbCell = new PdfPCell(cellBorderlessStyle);
			if (bonAvoir.getMontant()!=null) {
				mbCell.setPhrase(new Phrase(new Chunk("MONTANT DU BON  : "+bonAvoir.getReste().intValue()+" FCFA", boddyStyle)));

			}else {
				mbCell.setPhrase(new Phrase(new Chunk("MONTANT DU BON  :  0 FCFA", boddyStyle)));

			}
			mbCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			bonTable.addCell(mbCell);
			bonTable.addCell(emptyCell);
			bonTable.addCell(lineCell);
			
			PdfPCell typCell2 = new PdfPCell(cellBorderlessStyle);
			typCell2.setPhrase(new Phrase(new Chunk("TYPE DU BON :"+TypeBon.RETOUR.name(), boddyStyle)));
			typCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
			bonTable.addCell(typCell2);
			
			PdfPCell cltcell = new PdfPCell(cellBorderlessStyle);
			cltcell.setPhrase(new Phrase(new Chunk("CLIENT :"+bonAvoir.getClientNumber()+", "+bonAvoir.getClientName(), boddyStyle)));
			cltcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			bonTable.addCell(cltcell);
			bonTable.addCell(emptyCell);
			PdfPCell datecell = new PdfPCell(cellBorderlessStyle);
			datecell.setPhrase(new Phrase(new Chunk("DATE EMISSION :"+PharmaDateUtil.format(bonAvoir.getDateEdition(), PharmaDateUtil.DATETIME_PATTERN_LONG), boddyStyle)));
			datecell.setHorizontalAlignment(Element.ALIGN_LEFT);
			bonTable.addCell(datecell);
			document.add(bonTable);
		}
		/*	document.add(new Paragraph(new  Phrase(new Chunk("Votre Pharmacien a votre ecoute : ", boddyStyle))));
		document.add(new Paragraph(new Phrase(new Chunk("MERCI DE VOTRE CONFIANCE... BONNE JOURNEE ! ", boddyStyle))));
		document.add(new Paragraph(new Phrase(new Chunk("BONNE GUERISON ", headerStyle))));
		document.add(new Paragraph(new Phrase(new Chunk("Les Produits Vendus ne sont ni CHANGES ni RETOURNES !", boddyStyle))));
*/
		}

		@Override
		protected void buildPdfMetadata(Map<String, Object> model,
				Document document, HttpServletRequest request) {
			document.setPageSize(new Rectangle(0, 0, 220, 1000));
			document.setMargins(5, 5, 5, 5);
			Font boddyStyle = new Font(Font.COURIER,10);
			boddyStyle.setStyle("bold");
			Paiement pay= (Paiement) model.get("paiement");
			/*HeaderFooter footer = new HeaderFooter(new Phrase(new Chunk(  "Page" , boddyStyle)), true);
			footer.setAlignment(Element.ALIGN_CENTER);
			document.setFooter(footer);*/

			/*HeaderFooter header = new HeaderFooter(new Phrase(new Chunk("TICKET DE CAISSE "+pay.getFacture().getCommande().getCmdNumber().toUpperCase() , boddyStyle)), false);
			header.setAlignment(Element.ALIGN_CENTER);
			document.setHeader(header);*/
		
			super.buildPdfMetadata(model, document, request);
			
		}

}
