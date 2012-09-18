package org.adorsys.adpharma.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.adorsys.adpharma.domain.LicenceEntity;
import org.adorsys.adpharma.domain.Site;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CheckIsValideLicenceInterceptor extends HandlerInterceptorAdapter {
	@Override
    public boolean preHandle(HttpServletRequest request,
    HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = request.getRequestURI().substring(request.getContextPath().length());
        if ("/login".equals(requestUrl)) {
        	 List<Site> listOfSite  = Site.findAllSites();
    		 request.setAttribute("sites", listOfSite);
        }
        	
        return true;        
    }

}
