package org.adorsys.adpharma.services.core;

import org.adorsys.adpharma.domain.Inventaire;
import org.adorsys.adpharma.domain.Produit;

public interface InventoryService {
	
	public void makeStockCorrectionFromInventoryByCip(Inventaire inventaire) ;
	public void makeStockCorrectionFromInventoryByCipm(Inventaire inventaire) ;
}
