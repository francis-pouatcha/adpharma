package org.adorsys.adpharma.domain.virtualtable;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class ClientV {

    private String name;

    private String typeClient;

    private String employeur;

    private String payeur;
    
}
