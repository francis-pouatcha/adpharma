package org.adorsys.adpharma.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class ListingEtatDette extends AdPharmaBaseEntity {

	private Boolean toutLesClients;
	
    private Client client;
    
    private List<Client> clients;
    
	@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date date;
    
    private Boolean avoir;
    
    private List<EtatDette> EtatDettes;
   
}
