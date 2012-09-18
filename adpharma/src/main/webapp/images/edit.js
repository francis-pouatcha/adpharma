function calculePt() {
	var pu = document.getElementById("pu").value;
	var qte = document.getElementById("qte").value;
	if (qte != "") {

		document.getElementById("pt").value = pu * qte;

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
		document.getElementById("rem").value = 0;
		document.getElementById("pt").value = pu * qte;
		alert("la remise est surepieure a la remise max:" + remax + " fcfa");

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

function requete() {
	var cipm = document.getElementById("cipm").value;
	if (cipm.length == 10) {
		dojo.xhrGet({
			url : "${find_cipm_url }" + cipm,
			handleAs : "text",
			load : function(data) {
				if (data == "Aucun produit Trouve") {
					document.getElementById("cip").value = "";
					document.getElementById("des").value = "";
					document.getElementById("pu").value = "";
					document.getElementById("pId").value = "";
					document.getElementById("remax").value = "";
					document.getElementById("qte").value = "";
					document.getElementById("pt").value = "";
					document.getElementById("rem").value = "";

					alert("Aucun produit Trouve");
				} else {
					var t = data.split(',');
					document.getElementById("cip").value = t[0];
					document.getElementById("des").value = t[1];
					document.getElementById("pu").value = t[2];
					document.getElementById("pId").value = t[4];
					document.getElementById("remax").value = t[3];
					document.getElementById("qte").value = 1;
					document.getElementById("pt").value = t[2];
				}
			},
			error : function(error) {
				alert(data);
			},
			sync : "true"

		});
	}

}

function codeTouche(evenement) {
	for (prop in evenement) {
		if (prop == 'which')
			return (evenement.which);
	}
	return (evenement.keyCode);
}

function scanTouche(evenement) {
	var reCarValides = /\d/;
	var reCarSpeciaux = /[\x00\x08\x0D]/;
	var codeDecimal = codeTouche(evenement);
	var car = String.fromCharCode(codeDecimal);
	var autorisation = reCarValides.test(car) || reCarSpeciaux.test(car);

	return autorisation;
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
