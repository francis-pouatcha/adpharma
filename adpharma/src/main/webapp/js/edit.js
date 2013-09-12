
function calculePt() {
	var pu = document.getElementById("pu").value;
	var qte = document.getElementById("qte").value;
	var rem = document.getElementById("rem").value;
	var remP= document.getElementById("remP").value;
	if (rem == "") {
		rem = 0 ;
	}
	if (qte != "") {
		// Calcul de la promotion
		if(remP!=""){
			calculPromo();
		}
		document.getElementById("pt").value = (pu - rem) * qte;
	}else{
		document.getElementById("rem").value = "";
		document.getElementById("pt").value =  0 ;
	};
}



function calculPromo(){
	 $('#rem').val(Math.floor(($('#remP').val()*$('#pu').val())/100));
	 rem= $('#rem').val();
	 pu = document.getElementById("pu").value;
	 qte = document.getElementById("qte").value;
	document.getElementById("pt").value = (pu - rem) * qte;
}



function annCmd(){
	Check = confirm("Voulez vous vraiment annuler cette commande ?. ");
	if(Check == false){
		return false;
		
	}else{
		return true;
	}
	}

// Fonction generique d'annulation
function deleteBox(texte){
	var check= confirm(texte);
	if(check==false){
		return false;
	}else{
		return true;
	};
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
		$('#rem').val("");
		$('#remP').val("");			
		return false;
	} else if (qte == "") {
		document.getElementById("pt").value = 0;

		alert("veullez saisir la quantite a commandee !");
		$('#rem').val("");
		$('#remP').val("");
		return false;

	} else if (rem > remax) {
		if (remax == 0) {
			alert("Vous n'etes pas autorise a accorder des remises");
			document.getElementById("rem").value = 0;
			document.getElementById("pt").value = pu * qte;
			$('#remP').val(0);
			return false;
		}else{
			document.getElementById("rem").value = 0;
			document.getElementById("pt").value = pu * qte;
			alert("la remise est superieure a la remise max:" + remax + " fcfa");
			$('#remP').val(0);
			return false;
		}

	} else {
		document.getElementById("pt").value = (pu - rem) * qte;
		return true;
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
	
	$('#rem').keyup(function() {
		var bool = calculePtWhithRem();
		console.log(bool);
		if(bool)
			$('#remP').val(Math.round(($('#rem').val()*100)/$('#pu').val()));
	});

$('#remP').keyup(function() {
		if($('#pId').val()==""){
			alert("veullez rechercher un  produit !");
			$('#remP').val("");
		}else{
			$('#rem').val(Math.floor(($('#remP').val()*$('#pu').val())/100));
			calculePtWhithRem();
		}
		
	});

	
	$('.unique').val($('#rem').val()*100/$('#pu').val());
	
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
		resizable:false,
		draggable :true,
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










