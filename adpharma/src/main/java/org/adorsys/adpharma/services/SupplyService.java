package org.adorsys.adpharma.services;

import java.math.BigInteger;
import java.util.List;

import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.springframework.stereotype.Service;

@Service
public class SupplyService {
	
	public void compenserStock(BigInteger quantieEnStock, LigneApprovisionement ligneApprovisionement){
		
		if (quantieEnStock.intValue() > 0) {
			
			List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockLessThanAndCipEquals(BigInteger.ZERO, ligneApprovisionement.getCip()).getResultList();
			
			if (!resultList.isEmpty()) {
				
				for (LigneApprovisionement line : resultList) {
					
					
					if(ligneApprovisionement.canCompencse(line.getQuantieEnStock().abs())){
						ligneApprovisionement.setQuantiteSortie(ligneApprovisionement.getQuantiteSortie().add(line.getQuantieEnStock().abs()));
						line.setQuantiteVendu(line.getQuantiteAprovisione());
						line.setQuantiteSortie(BigInteger.ZERO);
						line.CalculeQteEnStock();
					}else {
						if (quantieEnStock.intValue() > 0) {
							ligneApprovisionement.setQuantiteSortie(ligneApprovisionement.getQuantiteSortie().add(quantieEnStock));
							line.setQuantiteVendu(line.getQuantiteVendu().subtract(quantieEnStock));
							line.CalculeQteEnStock();
							break;

						}

					}
					line.merge();
				}
				
				ligneApprovisionement.CalculeQteEnStock();
				ligneApprovisionement.merge();

			}
		}

	}

}
