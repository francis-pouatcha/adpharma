package org.adorsys.adpharma.domain;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@RooJavaBean
@RooToString
@RooEntity(inheritanceType = "TABLE_PER_CLASS", entityName = "PrinterConfiguration", finders = { "findPrinterConfigurationsByComputerAdresseEquals" })
public class PrinterConfiguration extends AdPharmaBaseEntity {

    @NotNull
    private String printerName;

   
    private String computerAdresse;

    @Value("true")
    private Boolean diseable;
    
    
    public void validate(BindingResult bindingResult) {
    	List<PrinterConfiguration> printerList = PrinterConfiguration.findPrinterConfigurationsByComputerAdresseEquals(computerAdresse).getResultList();
        if (!printerList.isEmpty()) {
            ObjectError error = new ObjectError("computerAdresse", "Cette Machine a deja une Imprimante ");
            bindingResult.addError(error);
        }
    }
    
    public void protectSomeField() {
        PrinterConfiguration printer = PrinterConfiguration.findPrinterConfiguration(getId());
        footPrint = printer.getFootPrint();
        computerAdresse = printer.getComputerAdresse();
    }
}
