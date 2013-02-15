package org.adorsys.adpharma.web;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.adorsys.adpharma.beans.CourbeApprovisionnement;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Periode;
import org.adorsys.adpharma.domain.Statistic;
import org.adorsys.adpharma.domain.TypeCourbeGraphique;
import org.adorsys.adpharma.domain.TypePaiement;
import org.adorsys.adpharma.utils.DateConfig;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.apache.commons.lang.text.StrBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/statistics")
@Controller
public class StatisticsController {
	
	 @RequestMapping(params = "form", method = RequestMethod.GET)
	    public String createForm(Model uiModel) {
	       uiModel.addAttribute("statistic", new Statistic());

	        return "statistics/statisticspage";
	    }
	 
	 @RequestMapping(value="/new", params = "form", method = RequestMethod.GET)
	    public String createStatistics(Model uiModel) {
	       uiModel.addAttribute("statistic", new Statistic());
	       return "statistics/courbes";
	    }
	 
	 
	 
	 @RequestMapping(method = RequestMethod.GET)
	    public String saleStatistics(Statistic statistic, Model uiModel) {
		 
		 StringBuilder graph1= new StringBuilder().append("[[0, 0]");
		 StringBuilder graph2= new StringBuilder().append("[[0, 0]");
		  Date dateDebut = statistic.getDateDebut();
		  Date dateFin = statistic.getDateFin();
		  Date nextDay = statistic.getNextDate(dateDebut);  ;
		  int i = 1;
		  while (nextDay.before(dateFin)) {
    		  System.out.println(nextDay);
    		  System.out.println(dateFin);

			  List<Object[]> chiffreVente = Caisse.findChiffreVente(dateDebut, nextDay);
			  for (Object[] chiffre : chiffreVente) {
				  BigInteger  montantAchat = (BigInteger) chiffre[0];
                  BigInteger  montantVente = (BigInteger) chiffre[1];
                  graph1.append(",["+i+","+montantAchat+"]");
                  graph2.append(",["+i+","+montantVente+"]");
                /*  if (i>=10) {
                	  graph1.append(",[20"+i+","+montantAchat+"]");
                      graph2.append(",[20"+i+","+montantVente+"]");
				}else {
					graph1.append(",[200"+i+","+montantAchat+"]");
                    graph2.append(",[200"+i+","+montantVente+"]");
				}*/
                	  
				
				
                 
        		  System.out.println(graph1.toString());

			}
			  dateDebut = nextDay;
			  nextDay =statistic.getNextDate(nextDay); 
			 i++;
		}
		 uiModel.addAttribute("graph1", graph1.append("]").toString());
		 uiModel.addAttribute("graph2", graph2.append("]").toString());
	       uiModel.addAttribute("statistic", statistic);

	        return "statistics/statisticspage";
	        
	 
	 }
	 
	 @RequestMapping( value ="/etatvente",params = "form", method = RequestMethod.GET)
	    public String etatventeForm(Model uiModel) {
	       uiModel.addAttribute("statistic", new Statistic());
	        return "statistics/etatvente";
	    }
	 @RequestMapping( value ="/courbeventecip",params = "form", method = RequestMethod.GET)
	    public String etatventecipForm(Model uiModel) {
	       uiModel.addAttribute("statistic", new Statistic());
	        return "statistics/courbeventecip";
	    }
	 
	 @RequestMapping( value ="/courbeventecip", method = RequestMethod.GET)
	    public String etatventecip(Statistic statistic, Model uiModel) {
		 
		 StringBuilder graph1= new StringBuilder().append("[[0, 0]");
		 StringBuilder graph2= new StringBuilder().append("[[0, 0]");
		  Date dateDebut = statistic.getDateDebut();
		  Date dateFin = statistic.getDateFin();
		  Date nextDay = statistic.getNextDate(dateDebut);  ;
		  int i = 1;
		  while (nextDay.before(dateFin)) {
 		  System.out.println(nextDay);
 		  System.out.println(dateFin);

			  List<Object[]> chiffreVente = Caisse.getNbClientAndSaleAmountByDate(dateDebut, nextDay);
			  for (Object[] chiffre : chiffreVente) {
				  Long  nbClient = (Long) chiffre[0];
               BigInteger  montantVente = (BigInteger) chiffre[1];
               BigInteger panierMoyen = nbClient.intValue()==0? BigInteger.ZERO :montantVente.divide(BigInteger.valueOf(nbClient));
               graph1.append(",["+i+","+nbClient+"]");
               graph2.append(",["+i+","+panierMoyen+"]");
            
             	  
				
				
              
     		  System.out.println(graph1.toString());

			}
			  dateDebut = nextDay;
			  nextDay =statistic.getNextDate(nextDay); 
			 i++;
		}
		 uiModel.addAttribute("graph1", graph1.append("]").toString());
		 uiModel.addAttribute("graph2", graph2.append("]").toString());
	       uiModel.addAttribute("statistic", statistic);

	        return "statistics/courbeventecip";
	        
	 
	 }
	 
	 @RequestMapping( value ="/etatvente", method = RequestMethod.GET)
	    public String etatvente( Statistic statistic, Model uiModel) {
		 ArrayList<MouvementStock> mvts = new ArrayList<MouvementStock>();
		 List<Object[]> etatVente = MouvementStock.getEtatVente(statistic.getDateDebut(), statistic.getDateFin());
		 System.out.println(etatVente.size());
		 if (!etatVente.isEmpty()) {
			for (Object[] obj : etatVente) {
				MouvementStock mvt = new MouvementStock();
				mvt.setDesignation((String)obj[0]);
				mvt.setQteDeplace((BigInteger)obj[1]);
				mvt.setPAchatTotal((BigInteger)obj[2]);
				mvt.setPVenteTotal((BigInteger)obj[3]);
				mvt.setRemiseTotal((BigInteger)obj[4]);
				mvts.add(mvt);
			}
		
			
		}
	       uiModel.addAttribute("result", mvts);
	       uiModel.addAttribute("statistic", statistic);
	        return "statistics/etatvente";
	    }
	 
	 @RequestMapping(value="/courbes", method=RequestMethod.GET)
	 public String etatsGraphiques(@RequestParam String debut, @RequestParam String fin, @RequestParam String periode, 
	   @RequestParam String type, Model uiModel){
		 System.out.println("Entree ds le controlleur" );
		 List<Object[]> courbeApprovisionement= new ArrayList<Object[]>();
		 List<CourbeApprovisionnement> datas = new ArrayList<CourbeApprovisionnement>();
		 Integer[] mois= {1,2,3,4,5,6,7,8,9,10,11,12};
		 List<Integer> define= Arrays.asList(mois);
		 List<Integer> current= new ArrayList<Integer>();
		 
			 System.out.println("ok");
			 courbeApprovisionement = MouvementStock.courbeApprovisionement(debut, fin, periode);
			 System.out.println("courbe: "+courbeApprovisionement);
		 	 
			 for(Object[] objet: courbeApprovisionement){
				 CourbeApprovisionnement point= new CourbeApprovisionnement((BigInteger)objet[0], (Integer)objet[1], (Integer)objet[2]);
				 datas.add(point);
				 current.add((Integer)objet[1]);
			 }
			 System.out.println("liste des mois courrants: "+current);
		 uiModel.addAttribute("Points", datas);
		 return "statistics/courbes";
	 }
	 
	 
	 
	    @ModelAttribute("periodes")
	    public Collection<Periode> populatePeriodes() {
	        return Arrays.asList(Periode.class.getEnumConstants());
	    }
	    
	    @ModelAttribute("typescourbes")
	    public Collection<TypeCourbeGraphique> populateTypesCourbes(){
	    	return Arrays.asList(TypeCourbeGraphique.class.getEnumConstants());
	    }
}
