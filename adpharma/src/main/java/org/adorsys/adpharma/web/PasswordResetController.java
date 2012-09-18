package org.adorsys.adpharma.web ;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.PasswordReset;
import org.adorsys.adpharma.domain.PharmaUser;
import org.adorsys.adpharma.security.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/passwordresets")
@Controller
public class PasswordResetController {

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid PasswordReset passwordReset, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if(!passwordReset.passwordsEqual()){
        	ObjectError error = new ObjectError("newPassword","Both password entered are not identical");
			bindingResult.addError(error);
        }
        PharmaUser user = SecurityUtil.getPharmaUser();

        if(!user.checkExistingPasword(passwordReset.getCurrentPassword())){
        	ObjectError error = new ObjectError("currentPassword","Current password not matching our record.");
			bindingResult.addError(error);
        }
    	if (bindingResult.hasErrors()) {
    		passwordReset.setUserName(user.getUserName());// doesn't come back
            uiModel.addAttribute("passwordReset", passwordReset);
            return "passwordresets/update";
        }
        uiModel.asMap().clear();
        user.changePassword(passwordReset.getNewPassword());
        user.merge();
        	
        return "passwordresets/confirm";
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String updateForm(Model uiModel) {
    	String userName = SecurityUtil.getUserName();
    	PasswordReset passwordReset = new PasswordReset();
    	passwordReset.setUserName(userName);
        uiModel.addAttribute("passwordReset", passwordReset);
        return "passwordresets/update";
    }
    
    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        }
        catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
}

