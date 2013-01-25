package org.adorsys.adpharma.domain;

import org.adorsys.adpharma.domain.DestinationMvt;
import javax.persistence.Enumerated;

public enum TypeMouvement {

   ALL, APPROVISIONEMENT, VENTE, ANNULATION, MODIFICATION ,SORTIE_TRANSFOMATION ,ENTREE_TRANSFOMATION , SORTIE_PRODUIT,SORTIE_INVENTAIRE,RETOUR_INVENTAIRE,RETOUR_PRODUIT,ANNULATION_SORTIE,
   ANNULATION_RETOUR,FUSION_CIP;

    
}
