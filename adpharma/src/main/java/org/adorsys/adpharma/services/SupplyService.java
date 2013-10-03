package org.adorsys.adpharma.services;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.adorsys.adpharma.domain.Configuration;
import org.adorsys.adpharma.domain.LigneApprovisionement;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.RoleName;
import org.springframework.stereotype.Service;

@Service
public class SupplyService {

	public void compenserStock(BigInteger quantieEnStock, LigneApprovisionement ligneApprovisionement){
		if (quantieEnStock.intValue() > 0) {
			List<LigneApprovisionement> resultList = LigneApprovisionement.findLigneApprovisionementsByQuantieEnStockLessThanAndCipEquals(BigInteger.ZERO, ligneApprovisionement.getCip()).getResultList();
			if (!resultList.isEmpty()) {
				for (LigneApprovisionement line : resultList) {
					if(ligneApprovisionement.isCompasable(line.getQuantieEnStock().abs())){
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


	public static boolean enableCloseSupplyEdition(Configuration configuration,PharmaUser user){
		ArrayList<RoleName> enableRole = new ArrayList<RoleName>();
		enableRole.add(RoleName.ROLE_SITE_MANAGER);
		if(configuration==null || user==null)throw new IllegalArgumentException("conf or user are required !");
		if(!configuration.getEditCloseSupply()) return false;
		if(!user.hasAnyRole(enableRole)) return false ;
		return true ;
	}



}
