// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.adorsys.adpharma.domain;

import java.lang.String;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.adorsys.adpharma.domain.PrinterConfiguration;

privileged aspect PrinterConfiguration_Roo_Finder {
    
    public static TypedQuery<PrinterConfiguration> PrinterConfiguration.findPrinterConfigurationsByComputerAdresseEquals(String computerAdresse) {
        if (computerAdresse == null || computerAdresse.length() == 0) throw new IllegalArgumentException("The computerAdresse argument is required");
        EntityManager em = PrinterConfiguration.entityManager();
        TypedQuery<PrinterConfiguration> q = em.createQuery("SELECT o FROM PrinterConfiguration AS o WHERE o.computerAdresse = :computerAdresse", PrinterConfiguration.class);
        q.setParameter("computerAdresse", computerAdresse);
        return q;
    }
    
}
