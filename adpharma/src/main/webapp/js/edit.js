
function calculePt() {
	var pu = document.getElementById("pu").value;
	var qte = document.getElementById("qte").value;
	var rem = document.getElementById("rem").value;
	if (rem == "") {
		rem = 0 ;
	}
	if (qte != "") {

		
		document.getElementById("pt").value = (pu - rem) * qte;
	}else{
		document.getElementById("rem").value = "";
		document.getElementById("pt").value =  0 ;
	}
}



function annCmd(){
	Check = confirm("Voulez vous vraiment annuler cette commande ?. ");
	if(Check == false){
		return false;
		
	}else{
		return true;
	}
	}


function calculePtWhithRem() {
	var rem = document.getElementById("rem").value;
	var qte = document.getElementById("qte").value;
	var remax = document.getElementById("remax").value;
	remax = Math.floor(remax);
	var pu = document.getElementById("pu").value;
	var pId = document.getElementById("pId").value;

	if (pId == "") {
		alert("veullez rechercher un  produit !");
	} else if (qte == "") {
		document.getElementById("pt").value = 0;

		alert("veullez saisir la quantite a commandee !");

	} else if (rem > remax) {
		if (remax == 0) {
			alert("Vous n'etes pas autorise a accorder des remises");
			document.getElementById("rem").value = 0;
			document.getElementById("pt").value = pu * qte;
		
		}else{
			document.getElementById("rem").value = 0;
			document.getElementById("pt").value = pu * qte;
			alert("la remise est surepieure a la remise max:" + remax + " fcfa");

		}

	} else {

		document.getElementById("pt").value = (pu - rem) * qte;
	}

}

function conf() {
	Check = confirm("Voulez vous vraiment supprimer la ligne? ");
	if (Check == false) {
		return false;

	} else {
		return true;
	}
}

function scanTouche(event) {
	var reCarValides = /\d/;
	var reCarSpeciaux = /[\x00\x08\x0D]/;
	var codeDecimal = codeTouche(event);
	var car = String.fromCharCode(codeDecimal);
	var autorisation = reCarValides.test(car) || reCarSpeciaux.test(car);

	return autorisation;
}
function scanToucheminus(event) {
	var reCarValides = /\d/;
	var reCarSpeciaux = /[\x00\x08\x0D\x2D]/;
	var codeDecimal = codeTouche(event);
	var car = String.fromCharCode(codeDecimal);
	var autorisation = reCarValides.test(car) || reCarSpeciaux.test(car);

	return autorisation;
}

function codeTouche(evenement) {
	for (prop in evenement) {
		if (prop == 'which')
			return (evenement.which);
	}
	return (evenement.keyCode);
}


function verif_formulaire() {
	if (document.formulaire.pId.value == "") {
		document.formulaire.cip.focus();
		requete();
		return false ;
	}
	if (document.formulaire.cip.value == "") {
		alert("Veuillez entrer le cip du produit!");
		document.formulaire.cip.focus();
		return false;
	}
	if (document.formulaire.qte.value == "") {
		alert("Veuillez entrer la quantite!");
		document.formulaire.qte.focus();
		return false;
	}
	if (document.formulaire.pa.value == "") {
		alert("Veuillez saisir le Prix achat Unitaire!");
		document.formulaire.pa.focus();
		return false;
	}
	return true ;

}

$(function(){
	$('#formulaire').bind('submit',function(event) {
	return	verif_formulaire();
	});
	});


// Dialogue de creation de l'ordonnance
/*function createPrescriptionDialog(){
	$('#ordonanceform').dialog({
		open: function(){
			$.getJSON( '${find_ordonance_url}', function(data){
				if(data==null){
					alert("Pas d'ordonance en cours");
				}else{
					$('#prescripteur').val(data.Prescripteur);
					$('#hopital').val(data.hospital);
					$('#nom_patient').val(data.nomDuPatient);
					$('#prescripteur').val(data.date_prescription);
				}
			});
		},
		autoOpen: false,
		width: 500,
		resizable:true,
		draggable :true,
		title:"Creer une ordonnance",
        hide:'fade',
        show:'fade',
        position:'top',
		buttons: {
			"Cancel": function() { 
				$(this).dialog("close"); 
			},
			"Save": function() { 
				$(this).dialog("close"); 
			}
		}
	});
	
	$('#btOrdo').click(function(){
		$('#ordonanceform').dialog('open');
		return false;
	});
}*/


// widget de dialogue a utiliser
function dialog_widget(id, title, width, hide_effect, show_effect, position){
	var dialog= $('#'+id).dialog({
		autoOpen: false,
		width: width,
		resizable:true,
		draggable :false,
		title: title,
        hide:  hide_effect,
        show:  show_effect,
        position: position,
		buttons: {
			"Cancel": function() { 
				$(this).dialog("close"); 
			}
		},
	});
	
	$('#search').click(function(){
		dialog.dialog('open');
		return false;
	});
}

// Validation du formulaire de l'etat
function validForm(){
	var checkSame= document.getElementById("same").checked;
	var checkdiff= document.getElementById("diff").checked;
	var dateMin= document.getElementById("year_min");
	var dateMax= document.getElementById("year_max");
	var moisMin= document.getElementById('month_min').value;
	var moisMax= document.getElementById('month_max').value;
	var date1= new Date(2013, monthToNumber(moisMin.toLowerCase()), 1);
	var date2= new Date(2013, monthToNumber(moisMax.toLowerCase()), 1);
	console.log(date1);
	console.log(date2);
	if(checkSame){
		if(dateMin.value!= dateMax.value){
			alert("Les annees doivent etre egales");
			return false;
		}
	}
	if(checkdiff){
		if(dateMin.value== dateMax.value){
			alert("Les annees doivent etre differentes");
			return false;
		}
	}
	if(date1>date2){
		alert("Le mois minimum doit etre inferieur au mois maximum");
		return false;
	}
	return true;
}

function monthToNumber(month){
	if(month=="janvier") return 0;
	else if(month=="fevrier") return 1;
	else if(month=="mars") return 2;
	else if(month=="avril") return 3;
	else if(month=="mai") return 4;
	else if(month=="juin") return 5;
	else if(month=="juillet") return 6;
	else if(month=="aout") return 7;
	else if(month=="septembre") return 8;
	else if(month=="octobre") return 9;
	else if(month=="novembre") return 10;
	else if(month=="decembre") return 11;
	else return null;
}





