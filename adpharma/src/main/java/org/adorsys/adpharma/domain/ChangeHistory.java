package org.adorsys.adpharma.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.adorsys.adpharma.domain.FootPrint;
import javax.persistence.Embedded;

@RooJavaBean
@RooToString
@RooEntity
public class ChangeHistory {

    @Embedded
    private FootPrint footPrint;

	public FootPrint getFootPrint() {
		return footPrint;
	}

	public void setFootPrint(FootPrint footPrint) {
		this.footPrint = footPrint;
	}

}
