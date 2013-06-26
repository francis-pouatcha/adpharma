package org.adorsys.adpharma.web;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.adorsys.adpharma.beans.CourbeApprovisionnement;
import org.adorsys.adpharma.beans.chart.ChartDataViewModel;
import org.adorsys.adpharma.beans.chart.XYChartData;
import org.adorsys.adpharma.beans.chart.XYChartDataProvider;
import org.adorsys.adpharma.domain.Caisse;
import org.adorsys.adpharma.domain.MouvementStock;
import org.adorsys.adpharma.domain.Periode;
import org.adorsys.adpharma.domain.Statistic;
import org.adorsys.adpharma.domain.TypeCourbeGraphique;
import org.adorsys.adpharma.utils.PharmaDateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/statistics")
@Controller
public class StatisticsController {

	@Resource
	private  XYChartDataProvider xyChartDataProvider ;
	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(Model uiModel) {
		uiModel.addAttribute("statistic", new Statistic());
		return "statistics/statisticspage";
	}
	@RequestMapping(value="/new", params = "form", method = RequestMethod.GET)
	public String createStatistics(Model uiModel) {
		uiModel.addAttribute("statistic", new Statistic());
		uiModel.addAttribute("currentDate", PharmaDateUtil.format(new Date(),PharmaDateUtil.DATE_PATTERN_YEAR));
		return "statistics/courbes";
	}
	@RequestMapping(value="/displayChart", method = RequestMethod.GET)
	@ResponseBody
	public String displayChart( ChartDataViewModel dataViewModel,Model uiModel,HttpServletResponse response){
		if(!dataViewModel.isOnlyForOneCip()){
			if(dataViewModel.isForSameYears()){
				List<XYChartData>  chartDatas = new ArrayList<XYChartData>();
				chartDatas.add(xyChartDataProvider.findChartByMonthAndYear(dataViewModel.getFirstYear(), dataViewModel.getMinMonth(), dataViewModel.getTypeOfChart()));
				chartDatas.add(xyChartDataProvider.findChartByMonthAndYear(dataViewModel.getFirstYear(), dataViewModel.getMaxMonth(), dataViewModel.getTypeOfChart()));
				dataViewModel.generateFirstSerieValues(chartDatas);
			}else {
				List<XYChartData> firstSerie = xyChartDataProvider.findChartByYearsAndMonthBetween(dataViewModel.getFirstYear(), dataViewModel.getMinMonth(), dataViewModel.getMaxMonth(), dataViewModel.getTypeOfChart());
				List<XYChartData> secondSerie = xyChartDataProvider.findChartByYearsAndMonthBetween(dataViewModel.getSecondYear(), dataViewModel.getMinMonth(), dataViewModel.getMaxMonth(), dataViewModel.getTypeOfChart());
				dataViewModel.generateFirstSerieValues(firstSerie);
				dataViewModel.generateSecondSerieValues(secondSerie);
			}
		}else {
			if(dataViewModel.isForSameYears()){
				List<XYChartData>  chartDatas = new ArrayList<XYChartData>();
				chartDatas.add(xyChartDataProvider.findChartByYearsAnCipAndMonth(dataViewModel.getFirstYear(), dataViewModel.getMinMonth(), dataViewModel.getProductCip(),dataViewModel.getTypeOfChart()));
				chartDatas.add(xyChartDataProvider.findChartByYearsAnCipAndMonth(dataViewModel.getFirstYear(), dataViewModel.getMaxMonth(), dataViewModel.getProductCip(),dataViewModel.getTypeOfChart()));
				dataViewModel.generateFirstSerieValues(chartDatas);
			}else {
				List<XYChartData> firstSerie = xyChartDataProvider.findChartByYearsAndCipAndMonthBetween(dataViewModel.getFirstYear(), dataViewModel.getMinMonth(), dataViewModel.getMaxMonth(), dataViewModel.getProductCip(),dataViewModel.getTypeOfChart());
				List<XYChartData> secondSerie = xyChartDataProvider.findChartByYearsAndCipAndMonthBetween(dataViewModel.getSecondYear(), dataViewModel.getMinMonth(), dataViewModel.getMaxMonth(), dataViewModel.getProductCip(),dataViewModel.getTypeOfChart());
				dataViewModel.generateFirstSerieValues(firstSerie);
				dataViewModel.generateSecondSerieValues(secondSerie);

			}
		}

		dataViewModel.generateXaxisValues();
		dataViewModel.generateChartTitle();
		return dataViewModel.toJson();


	}



	/*@RequestMapping(method = RequestMethod.GET)
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
                  if (i>=10) {
                	  graph1.append(",[20"+i+","+montantAchat+"]");
                      graph2.append(",[20"+i+","+montantVente+"]");
				}else {
					graph1.append(",[200"+i+","+montantAchat+"]");
                    graph2.append(",[200"+i+","+montantVente+"]");
				}
        		  System.out.println(graph1.toString());*/

	@RequestMapping(method = RequestMethod.GET)
	public String saleStatistics(Statistic statistic, Model uiModel) {
		StringBuilder graph1= new StringBuilder().append("[[0, 0]");
		StringBuilder graph2= new StringBuilder().append("[[0, 0]");
		Date dateDebut = statistic.getDateDebut();
		Date dateFin = statistic.getDateFin();
		Date nextDay = statistic.getNextDate(dateDebut);
		int i = 1;
		while (nextDay.before(dateFin)) {
			List<Object[]> chiffreVente = Caisse.findChiffreVente(dateDebut, nextDay);
			for (Object[] chiffre : chiffreVente) {
				BigInteger  montantAchat = (BigInteger) chiffre[0];
				BigInteger  montantVente = (BigInteger) chiffre[1];
				graph1.append(",["+i+","+montantAchat+"]");
				graph2.append(",["+i+","+montantVente+"]");

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
			List<Object[]> chiffreVente = Caisse.getNbClientAndSaleAmountByDate(dateDebut, nextDay);
			for (Object[] chiffre : chiffreVente) {
				Long  nbClient = (Long) chiffre[0];
				BigInteger  montantVente = (BigInteger) chiffre[1];
				BigInteger panierMoyen = nbClient.intValue()==0? BigInteger.ZERO :montantVente.divide(BigInteger.valueOf(nbClient));
				graph1.append(",["+i+","+nbClient+"]");
				graph2.append(",["+i+","+panierMoyen+"]");
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
		List<Object[]> etatVente = MouvementStock.getEtatVente(statistic.getCip(),statistic.getDateDebut(), statistic.getDateFin());
		System.out.println(etatVente.size());
		if (!etatVente.isEmpty()) {
			for (Object[] obj : etatVente) {
				MouvementStock mvt = new MouvementStock();
				mvt.setDesignation((String)obj[0]);
				mvt.setQteDeplace((BigInteger)obj[1]);
				mvt.setPAchatTotal((BigInteger)obj[2]);
				mvt.setPVenteTotal((BigInteger)obj[3]);
				mvt.setRemiseTotal((BigInteger)obj[4]);
				mvt.setCip((String)obj[5]) ;
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
