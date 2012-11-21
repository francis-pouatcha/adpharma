package org.adorsys.adpharma.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.adorsys.adpharma.domain.Configuration;
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
    		 //check the valididy of licence 
    		 LicenceEntity licence = LicenceEntity.findLicenceEntity(new Long(1));
    		 Configuration configuration = Configuration.findConfiguration(new Long(1));
    		 if(configuration.getEnableLicence()){
    			 if(!licence.isValidLicence()) {
    				 request.getSession().invalidate();
    				 request.getRequestDispatcher("/licenceentitys/checkLicence").forward(request, response);
    			 }
    		 }
        }
        	
        return true;        
    }

}
