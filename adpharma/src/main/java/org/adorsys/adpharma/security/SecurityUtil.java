package org.adorsys.adpharma.security ;

/**
 * Give different security utilities on a user.
 */
import java.util.List;

import javax.persistence.TypedQuery;

import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.RoleName;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtil {

	public static UserDetails getUserDetails(){
		SecurityContext context = SecurityContextHolder.getContext();
		if(context==null) return null;
		Authentication authentication = context.getAuthentication();
		if(authentication==null) return null;
		Object principal = authentication.getPrincipal();
		if(principal==null) return null;
		if (principal instanceof UserDetails) {
			return ((UserDetails)principal);
		} else {
			return null;
		}
	}

	public static String getUserName(){
		UserDetails userDetails = getUserDetails();
		if(userDetails!=null) return userDetails.getUsername();
		return "system";
	}

	public static PharmaUser getPharmaUser(){
		UserDetails userDetails = getUserDetails();
		if(userDetails==null) return null;
		String username = userDetails.getUsername();
		TypedQuery<PharmaUser> query = PharmaUser.findPharmaUsersByUserNameEquals(username) ;
		return query.getResultList().iterator().next();
	}

	public static PharmaUser getPharmaUser(String key){
		PharmaUser pharmaUser = null ;
		if(StringUtils.isNotBlank(key)){
			List<PharmaUser> resultList = PharmaUser.findPharmaUsersBySaleKeyEquals(key).getResultList();
			if (!resultList.isEmpty()) {
				pharmaUser = resultList.iterator().next();
			}
		}else {
			pharmaUser = getPharmaUser();
			if (pharmaUser.hasAnyRole(RoleName.ROLE_OPEN_SALE_SESSION)) {
				pharmaUser = null ;
			}
		}
		return pharmaUser ;
	}
}
