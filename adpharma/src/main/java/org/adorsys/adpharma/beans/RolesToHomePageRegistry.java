/**
 * 
 */
package org.adorsys.adpharma.beans;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.adorsys.adpharma.domain.RoleName;
import org.springframework.stereotype.Repository;

/**
 * @author w2b
 *
 */
@Repository("rolesToHomePageRegistry")
public class RolesToHomePageRegistry {
	
	private Map<String, List<RoleName>> registry = new LinkedHashMap<String, List<RoleName>>();
	private Map<RoleName, String> roleToHomeUrlRegistry = new LinkedHashMap<RoleName, String>();
	private Map<RoleName, List<RoleName>> rolesWithSameHomeUrl = new LinkedHashMap<RoleName, List<RoleName>>();

	public RolesToHomePageRegistry() {
	}
	
	@PostConstruct
	public void postConstruct(){
		roleToHomeUrlRegistry.put(RoleName.ROLE_VENDEUR, "/saleprocess/newPublicCmd");
		roleToHomeUrlRegistry.put(RoleName.ROLE_CASHIER, "/paiementprocess/encaisser?form");
		roleToHomeUrlRegistry.put(RoleName.ROLE_ETATS, "");
		roleToHomeUrlRegistry.put(RoleName.ROLE_INVENTAIRE, "");
		roleToHomeUrlRegistry.put(RoleName.ROLE_STOCKER, "");
		roleToHomeUrlRegistry.put(RoleName.ROLE_FOURNISSEUR, "");
		roleToHomeUrlRegistry.put(RoleName.ROLE_MOUVEMENTS, "");
		roleToHomeUrlRegistry.put(RoleName.ROLE_RAYON, "");
		roleToHomeUrlRegistry.put(RoleName.ROLE_STOCKER, "");
		roleToHomeUrlRegistry.put(RoleName.ROLE_SUPER_ADMIN, "");
	}
	
	public String getHomeUrl(RoleName roleName){
		String homeUrl = null;
		if(this.roleToHomeUrlRegistry.containsKey(roleName)){
			homeUrl = this.roleToHomeUrlRegistry.get(roleName);
		}else {
			Set<RoleName> keySet = roleToHomeUrlRegistry.keySet();
			for (RoleName r : keySet) {
				List<RoleName> list = rolesWithSameHomeUrl.get(r);
				if(rolesWithSameHomeUrl.containsKey(r) && list.contains(roleName)){
					homeUrl = this.roleToHomeUrlRegistry.get(r);
					break ;
				}
			}
		}
		return homeUrl ;
	}
}


