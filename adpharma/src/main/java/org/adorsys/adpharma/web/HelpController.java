package org.adorsys.adpharma.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RequestMapping("/help")
@Controller
public class HelpController {
	
	@RequestMapping("manuel")
	public String selectHelpPage(@RequestParam(value = "page", required = false) String page){

		return "help/"+page;
	}

}
