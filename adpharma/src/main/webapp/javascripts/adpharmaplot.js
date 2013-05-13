function adpharmaMixtePlot(){
	var minMonth ; //minimal month for which we want chart
	var maxMonth ;
	var minYear ;
	var MaYear;
	var ticks = new Array();
	var firstChartLegend ;
	var secondChartLegend ;
	var title ;
	
	parseIntToMonth = function(intValue){
		switch(intValue)
		{
		case "1":
		return "Jan"
		break;
		case "2":
		alert("Et PAF, le chien !");
		break;
		case "pingouin":
		alert("Bonjour, Tux");
		break;
		default:
		alert("Aucun Mois ne correspond a cet entier");
		break;
		}

	}
}