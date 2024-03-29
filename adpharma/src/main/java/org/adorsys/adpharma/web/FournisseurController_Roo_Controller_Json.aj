// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.web;

import java.lang.Long;
import java.lang.String;
import org.adorsys.adpharma.domain.Fournisseur;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

privileged aspect FournisseurController_Roo_Controller_Json {
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> FournisseurController.showJson(@PathVariable("id") Long id) {
        Fournisseur fournisseur = Fournisseur.findFournisseur(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/text; charset=utf-8");
        if (fournisseur == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(fournisseur.toJson(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> FournisseurController.listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/text; charset=utf-8");
        return new ResponseEntity<String>(Fournisseur.toJsonArray(Fournisseur.findAllFournisseurs()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> FournisseurController.createFromJson(@RequestBody String json) {
        Fournisseur.fromJsonToFournisseur(json).persist();
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type", "application/text");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> FournisseurController.createFromJsonArray(@RequestBody String json) {
        for (Fournisseur fournisseur: Fournisseur.fromJsonArrayToFournisseurs(json)) {
            fournisseur.persist();
        }
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type", "application/text");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> FournisseurController.updateFromJson(@RequestBody String json) {
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type", "application/text");
        if (Fournisseur.fromJsonToFournisseur(json).merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> FournisseurController.updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type", "application/text");
        for (Fournisseur fournisseur: Fournisseur.fromJsonArrayToFournisseurs(json)) {
            if (fournisseur.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> FournisseurController.deleteFromJson(@PathVariable("id") Long id) {
        Fournisseur fournisseur = Fournisseur.findFournisseur(id);
        HttpHeaders headers= new HttpHeaders();
        headers.add("Content-Type", "application/text");
        if (fournisseur == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        fournisseur.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByNameLike", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> FournisseurController.jsonFindFournisseursByNameLike(@RequestParam("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/text; charset=utf-8");
        return new ResponseEntity<String>(Fournisseur.toJsonArray(Fournisseur.findFournisseursByNameLike(name).getResultList()), headers, HttpStatus.OK);
    }
    
}
