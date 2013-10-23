package org.adorsys.adpharma.security ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.RoleName;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * This class is a custom implementation of spring it's UserDetailsService.
 */

@Service
@Transactional
public class AdPharmaUserDetailsService implements UserDetailsService {
	
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		
		try {
			// check context
			if(StringUtils.isBlank(username)){
				throw new UsernameNotFoundException("Estate user with email not found");
			}
			TypedQuery<PharmaUser> typedQuery = PharmaUser.findNotHiddenPharmaUsersByUserNameEquals(username);
			PharmaUser  user = typedQuery.getSingleResult();
			
			if ( user == null)
				throw new UsernameNotFoundException("Estate user with email not found");
			return createUserDetails(user);
		} finally {
			
		}
	}

	public static UserDetails createUserDetails(PharmaUser  user)
			throws UsernameNotFoundException, DataAccessException 
	{
		String password = user.getPassword();
		boolean enabled = !user.isDisableLogin();
		boolean accountNonExpired = true ;
		if (user.getAccountExpiration()!=null) {
			accountNonExpired = new Date().before(user.getAccountExpiration());	
		}
		boolean credentialsNonExpired = true ;
		if (user.getCredentialExpiration()!= null) {
			credentialsNonExpired = new Date().before(user.getCredentialExpiration());
		}
		boolean accountNonLocked =!user.isAccountLocked();
		
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		Set<RoleName> roles = user.getRoleNames();
		for (RoleName roleName : roles) {
			GrantedAuthorityImpl ga = new GrantedAuthorityImpl(
					roleName.name());
			authorities.add(ga);
		}
	
		UserDetails userDetails = new User(user.getUserName(), password, enabled,
				accountNonExpired, credentialsNonExpired, accountNonLocked,authorities);
		return userDetails;
	}
}
