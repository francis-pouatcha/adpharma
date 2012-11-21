package org.adorsys.adpharma.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.domain.LicenceEntity;
import org.adorsys.adpharma.domain.Site;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoginPathInterceptor  extends HandlerInterceptorAdapter {
 
	@Override
    public boolean preHandle(HttpServletRequest request,
    HttpServletResponse response, Object handler) throws Exception {
		 List<Site> listOfSite  = Site.findAllSites();
		 request.setAttribute("sites", listOfSite);
        return true;        
    }
}
