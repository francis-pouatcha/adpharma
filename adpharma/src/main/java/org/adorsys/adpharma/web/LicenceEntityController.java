package org.adorsys.adpharma.web;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.adorsys.adpharma.domain.LicenceEntity;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RooWebScaffold(path = "licenceentitys", formBackingObject = LicenceEntity.class)
@RequestMapping("/licenceentitys")
@Controller
public class LicenceEntityController {

	@RequestMapping(method = RequestMethod.PUT)
	public String update(@Valid LicenceEntity licenceEntity, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		licenceEntity.initDate();
		licenceEntity.validate(bindingResult);
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("licenceEntity", licenceEntity);
			return "licenceentitys/update";
		}
		uiModel.asMap().clear();
		if (licenceEntity.getId()!=null) {
			licenceEntity.merge();
		}else {
			licenceEntity.persist();
		}

		return "redirect:/licenceentitys/" + encodeUrlPathSegment(licenceEntity.getId().toString(), httpServletRequest);
	}

	@RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
	public String  updateForm(@PathVariable("id") Long id, Model uiModel) {
		LicenceEntity licenceEntity = LicenceEntity.findLicenceEntity(id);
		licenceEntity = licenceEntity == null ? new LicenceEntity():licenceEntity ;
		uiModel.addAttribute("licenceEntity", licenceEntity);
		return "licenceentitys/update";
	}

	@RequestMapping(value = "/checkLicence", method = RequestMethod.GET)
	public String  checkLicence( Model uiModel) {
		LicenceEntity licenceEntity = LicenceEntity.findLicenceEntity(new Long(1));
		licenceEntity = licenceEntity == null ? new LicenceEntity():licenceEntity ;
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("Votre Licence Est Expiree depuis Le : "+PharmaDateUtil.format(licenceEntity.getTranstientEnd(), PharmaDateUtil.DATETIME_PATTERN_LONG));
		arrayList.add("Veuillez appeler l'un des Numeros Suivants : (+237) 33 42 32 44 / 99 93 99 48  ");	
		arrayList.add("Votre Cle Secrete est : "+licenceEntity.getGenerateKey());	
		uiModel.addAttribute("appMessages", arrayList);
		return "licenceentitys/infos";
	}

	@RequestMapping(value = "/setLicence", method = RequestMethod.GET)
	public String  setLicence(@RequestParam("licenceKey") String licenceKey , Model uiModel) {
		LicenceEntity licenceEntity = LicenceEntity.findLicenceEntity(new Long(1));
		licenceEntity = licenceEntity == null ? new LicenceEntity():licenceEntity ;
		if (licenceEntity.isValidKey(licenceKey)) {
			String[] listString = licenceKey.split("-");
			licenceEntity.setBeginDate(PharmaDateUtil.format(new Date(), PharmaDateUtil.DATETIME_PATTERN_LONG));
			licenceEntity.setEndDate(PharmaDateUtil.format(DateUtils.addMinutes(new Date(), NumberUtils.toInt(licenceEntity.decodeString(listString[3]))), PharmaDateUtil.DATETIME_PATTERN_LONG));
			licenceEntity.initTransientDate();
			licenceEntity.setGenerateKey(" ");
			licenceEntity.merge();
			ArrayList<String> arrayList = new ArrayList<String>();
			arrayList.add("Votre Licence A ete Mis A jour et Prendra fin Le  : "+PharmaDateUtil.format(licenceEntity.getTranstientEnd(), PharmaDateUtil.DATETIME_PATTERN_LONG));
			arrayList.add("Vous pouvez Maintenant Vous Connecter ! ");
			arrayList.add("ADORSYS S.A Phones : (+237) 33 42 32 44 / 99 93 99 48  ");	
			arrayList.add("Merci de Nous faire Confiance  ! ");	
			uiModel.addAttribute("appMessages", arrayList);
		}else {
			ArrayList<String> arrayList = new ArrayList<String>();
			arrayList.add("La cle  : \'"+licenceKey +"\'  est Invalide ");
			arrayList.add("Veuillez appeler l'un des Numeros Suivants : (+237) 33 42 32 44 / 99 93 99 48 ");	
			arrayList.add("Votre Cle Secrete est : "+licenceEntity.getGenerateKey());	
			uiModel.addAttribute("appMessages", arrayList);
			uiModel.addAttribute("licenceKey", licenceKey);
		}

		return "licenceentitys/infos";
	}




}
