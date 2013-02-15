package org.adorsys.adpharma.beans.process;

import java.util.Comparator;

import org.adorsys.adpharma.domain.DetteClient;

public class DetteComparator  implements Comparator<DetteClient> {
	 public int compare(DetteClient d1, DetteClient d2) {
		 if (d1.getAssurer()!=null && d2.getAssurer()!=null) {
			 return d1.getAssurer().compareTo(d2.getAssurer());
		}else {
			return -1 ;
		}
        
     }
}
