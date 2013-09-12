package org.adorsys.adpharma.web;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.adorsys.adpharma.beans.process.PaiementProcess;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.OperationCaisse;
import org.adorsys.adpharma.security.SecurityUtil;
import org.adorsys.adpharma.utils.LocaleUtil;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "operationcaisses", formBackingObject = OperationCaisse.class)
@RequestMapping("/operationcaisses")
@Controller
public class OperationCaisseController {
	
	@Resource(name="messageSource")
	ReloadableResourceBundleMessageSource messageSource;
	
	
	 @RequestMapping(value = "/caisseEnCour",method = RequestMethod.GET)
	    public String listCaisseEnCour(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
         Caisse caisse = PaiementProcess.getMyOpenCaisse(SecurityUtil.getPharmaUser());
         if (caisse==null) {
	            uiModel.addAttribute("apMessage", messageSource.getMessage("cash_open_warning", null, LocaleUtil.getCurrentLocale())); 
	            return "caisses/infos";

			}
		 if (page != null || size != null) {
	            int sizeNo = size == null ? 10 : size.intValue();
	            uiModel.addAttribute("operationcaisses", OperationCaisse.findOperationCaisseAndCaissierEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo,caisse));
	            float nrOfPages = (float) OperationCaisse.countOperationCaisses() / sizeNo;
	            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
	        } else {
	            uiModel.addAttribute("operationcaisses",OperationCaisse.findOperationCaissesByCaisse(caisse));
	        }
	        addDateTimeFormatPatterns(uiModel);
	        return "operationcaisses/list";
	    }
}
