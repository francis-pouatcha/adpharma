package org.adorsys.adpharma.beans;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.adorsys.adpharma.beans.process.SessionBean;
import org.adorsys.adpharma.domain.Configuration;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.domain.RoleName;
import org.adorsys.adpharma.domain.Site;
import org.adorsys.adpharma.security.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;
/**
 * This class use some code from SavedRequestAwareAuthenticationSuccessHandler.
 * Works like @see SavedRequestAwareAuthenticationSuccessHandler
 * 
 * @author franklin
 * @author w2b
 *
 */
@Service("authentificationFilter")
public class AuthentificationFilter   extends SimpleUrlAuthenticationSuccessHandler implements LogoutSuccessHandler {
	static final Logger LOGGER = LoggerFactory.getLogger(AuthentificationFilter.class);
    private RequestCache requestCache = new HttpSessionRequestCache();
    @Resource(name="rolesToHomePageRegistry")
    RolesToHomePageRegistry rolesToHomePageRegistry ;
    
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException{
		HttpSession session = request.getSession();
		Site findSite = Site.findSite(new Long(1));
		SessionBean sessionBean = new SessionBean(findSite);
		sessionBean.setConfiguration(Configuration.findConfiguration(new Long(1)));
		if(!sessionBean.isAbleToConnect(SecurityUtil.getPharmaUser())){
			session.setAttribute("sessionBean", sessionBean) ;

		}else {
			session.setAttribute("sessionBean", sessionBean) ;
		}
	    SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) {
        	String redirectLink = getUserRedirectLink();
        	if(StringUtils.isBlank(redirectLink)) redirectLink = "";
			String userRedirectLink  = new StringBuilder().append(request.getContextPath()).append(redirectLink).toString();
        	LOGGER.warn("Redirecting User : "+userRedirectLink);
        	
			response.sendRedirect(userRedirectLink);
            return;
        }

        if (isAlwaysUseDefaultTargetUrl() || org.springframework.util.StringUtils.hasText(request.getParameter(getTargetUrlParameter()))) {
            requestCache.removeRequest(request, response);
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        clearAuthenticationAttributes(request);

        // Use the DefaultSavedRequest URL
        String targetUrl = savedRequest.getRedirectUrl();
        logger.debug("Redirecting to DefaultSavedRequest Url: " + targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	@Override
	public  void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException{
		//TODO:
		return ;
	}

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
	private String getUserRedirectLink(){
	
		return "";
	}
}
