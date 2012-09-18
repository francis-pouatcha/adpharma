// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.lang.Long;
import java.lang.String;
import org.adorsys.adpharma.domain.PharmaUser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

privileged aspect PharmaUserController_Roo_Controller_Json {
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> PharmaUserController.showJson(@PathVariable("id") Long id) {
        PharmaUser pharmauser = PharmaUser.findPharmaUser(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/text; charset=utf-8");
        if (pharmauser == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(pharmauser.toJson(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> PharmaUserController.listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/text; charset=utf-8");
        return new ResponseEntity<String>(PharmaUser.toJsonArray(PharmaUser.findAllPharmaUsers()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> PharmaUserController.createFromJson(@RequestBody String json) {
        PharmaUser.fromJsonToPharmaUser(json).persist();
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type", "application/text");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> PharmaUserController.createFromJsonArray(@RequestBody String json) {
        for (PharmaUser pharmaUser: PharmaUser.fromJsonArrayToPharmaUsers(json)) {
            pharmaUser.persist();
        }
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type", "application/text");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> PharmaUserController.updateFromJson(@RequestBody String json) {
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type", "application/text");
        if (PharmaUser.fromJsonToPharmaUser(json).merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> PharmaUserController.updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type", "application/text");
        for (PharmaUser pharmaUser: PharmaUser.fromJsonArrayToPharmaUsers(json)) {
            if (pharmaUser.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> PharmaUserController.deleteFromJson(@PathVariable("id") Long id) {
        PharmaUser pharmauser = PharmaUser.findPharmaUser(id);
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type", "application/text");
        if (pharmauser == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        pharmauser.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=BySaleKeyEquals", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> PharmaUserController.jsonFindPharmaUsersBySaleKeyEquals(@RequestParam("saleKey") String saleKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/text; charset=utf-8");
        return new ResponseEntity<String>(PharmaUser.toJsonArray(PharmaUser.findPharmaUsersBySaleKeyEquals(saleKey).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByUserNameEquals", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> PharmaUserController.jsonFindPharmaUsersByUserNameEquals(@RequestParam("userName") String userName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/text; charset=utf-8");
        return new ResponseEntity<String>(PharmaUser.toJsonArray(PharmaUser.findPharmaUsersByUserNameEquals(userName).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByUserNameLike", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> PharmaUserController.jsonFindPharmaUsersByUserNameLike(@RequestParam("userName") String userName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/text; charset=utf-8");
        return new ResponseEntity<String>(PharmaUser.toJsonArray(PharmaUser.findPharmaUsersByUserNameLike(userName).getResultList()), headers, HttpStatus.OK);
    }
    
}
