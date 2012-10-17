package org.adorsys.adpharma.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@RequestMapping("/manuels")
@Controller
public class ManuelController {
	
	@RequestMapping(value="/{pageToget}",method=RequestMethod.GET)
	public String getManualPage( @PathVariable("pageToget") String pageToget){
		return "manuels/"+pageToget;
	}

}
