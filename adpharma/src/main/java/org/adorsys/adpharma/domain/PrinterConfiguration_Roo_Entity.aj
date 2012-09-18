// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.Long;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.adorsys.adpharma.domain.PrinterConfiguration;

privileged aspect PrinterConfiguration_Roo_Entity {
    
    declare @type: PrinterConfiguration: @Entity(name = "PrinterConfiguration");
    
    declare @type: PrinterConfiguration: @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS);
    
    public static long PrinterConfiguration.countPrinterConfigurations() {
        return entityManager().createQuery("SELECT COUNT(o) FROM PrinterConfiguration o", Long.class).getSingleResult();
    }
    
    public static List<PrinterConfiguration> PrinterConfiguration.findAllPrinterConfigurations() {
        return entityManager().createQuery("SELECT o FROM PrinterConfiguration o", PrinterConfiguration.class).getResultList();
    }
    
    public static PrinterConfiguration PrinterConfiguration.findPrinterConfiguration(Long id) {
        if (id == null) return null;
        return entityManager().find(PrinterConfiguration.class, id);
    }
    
    public static List<PrinterConfiguration> PrinterConfiguration.findPrinterConfigurationEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM PrinterConfiguration o", PrinterConfiguration.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
