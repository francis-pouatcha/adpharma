
function calculePt() {
	var pu = document.getElementById("pu").value;
	var qte = document.getElementById("qte").value;
	if (qte != "") {

		document.getElementById("pt").value = pu * qte;

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

function codeTouche(evenement) {
	for (prop in evenement) {
		if (prop == 'which')
			return (evenement.which);
	}
	return (evenement.keyCode);
}


function verif_formulaire() {
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

}


