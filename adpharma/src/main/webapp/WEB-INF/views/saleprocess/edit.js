//Namespace definition
var cm = {};
cm.adpharma = {};
cm.adpharma.saleprocess = {};

cm.adpharma.saleprocess.EditPage = function() {
	// The variable that would, normally, be global
	var res = {};

	res.onInitRegisterEvents = function onInitRegisterEvents() {
		var cipmField = document.getElementById('cipm');
		if(cipmField){
			attachEventListener(cipmField, "keyPress", scanTouche, false);
			attachEventListener(cipmField, "keyup", requete, false);
		}
	};

	res.onInitRunScripts = function onInitRunScripts() {
		//
	};

	res.run = function() {
		// Binding a new function to the window object
		attachEventListener(window, "load", initPageHandler, false);
	};

	var initPageHandler = function initPageHandler() {
		res.onInitRegisterEvents();
		res.onInitRunScripts();
		// And more...

		var msg = "Salut Boris JS is fun!";
		alert( msg );
	};

	// Binding a new function to the window object
	window.attachEventListener = function(object, type, func, capture) {
		if (!!object.addEventListener) {
			object.addEventListener(type, func, capture);
		} else if (!!object.attachEvent) {
			object.attachEvent("on" + type, func);
		}
	};

	var requete = function requete() {
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
	};

	var scanTouche = function scanTouche(event) {
		var reCarValides = /\d/;
		var reCarSpeciaux = /[\x00\x08\x0D]/;
		var codeDecimal = codeTouche(event);
		var car = String.fromCharCode(codeDecimal);
		var autorisation = reCarValides.test(car) || reCarSpeciaux.test(car);

		return autorisation;
	};

	var codeTouche = function codeTouche(event) {
		for (prop in event) {
			if (prop == 'which')
				return (event.which);
		}
		return (event.keyCode);
	};

	return res;
};


var page = new cm.adpharma.saleprocess.EditPage();
page.run();


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
		if (remax == 0) {
			alert("Vous n'etes pas autorise a accorder des remises");
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
