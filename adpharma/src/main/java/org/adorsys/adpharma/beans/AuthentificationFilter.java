package org.adorsys.adpharma.beans;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.adorsys.adpharma.domain.Configuration;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.security.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;

@Service("authentificationFilter")
public class AuthentificationFilter  extends SavedRequestAwareAuthenticationSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException{
		HttpSession session = request.getSession();
		//String siteId = request.getParameter("j_site");
		//System.out.println(siteId);
		Site findSite = Site.findSite(new Long(1));
		SessionBean sessionBean = new SessionBean(findSite);
		if(!sessionBean.isAbleToConnect(SecurityUtil.getPharmaUser())){
			//session.invalidate();
			session.setAttribute("sessionBean", sessionBean) ;

		}else {
			
		}
		super.onAuthenticationSuccess(request, response, authentication);
		return;
	}

	@Override
	public  void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException{
		return ;
	}

}
