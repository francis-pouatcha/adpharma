package org.adorsys.adpharma.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class EtatDette extends AdPharmaBaseEntity {
	
	@ManyToOne
    private Client client;
    
	@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateReglage;
	
	@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateCreation;
    
    private Boolean avoir;
    
    @OneToMany
    private List<DetteClient> dettes;
    
    @OneToMany
    private List<AvoirClient> avoirs;
    
    private BigDecimal totalDette;
    
    private BigDecimal totalAvoir;
    
    private BigDecimal netAPayer;
}
