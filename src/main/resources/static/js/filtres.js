if (!document.getElementById("filtresIsAuth")) {
	document.getElementById("selectsFiltres").setAttribute("class", "hide");
	document.getElementById("inputRadio").setAttribute("class", "hide");

} else {
	var option0 = document.getElementById("selectAchats").options[0];
	var option1 = document.getElementById("selectAchats").options[1];
	var option2 = document.getElementById("selectAchats").options[2];
	var option3 = document.getElementById("selectVentes").options[0];
	var option4 = document.getElementById("selectVentes").options[1];
	var option5 = document.getElementById("selectVentes").options[2];

	var valeurSelect = document.getElementById("valeurSelect").value;
	initFiltres();

	var selected;
	document.getElementById('achats').addEventListener("change", function() {
		affichageFiltres();
	})
	document.getElementById('ventes').addEventListener("change", function() {
		affichageFiltres();
	})
}

function initFiltres() {
	switch (valeurSelect) {
		case "0": option0.setAttribute("selected", true);
			document.getElementById("achats").setAttribute("checked", true); break;
		case "1": option1.setAttribute("selected", true);
			document.getElementById("achats").setAttribute("checked", true); break;
		case "2": option2.setAttribute("selected", true);
			document.getElementById("achats").setAttribute("checked", true); break;
		case "3": option3.setAttribute("selected", true);
			document.getElementById("ventes").setAttribute("checked", true); break;
		case "4": option4.setAttribute("selected", true);
			document.getElementById("ventes").setAttribute("checked", true); break;
		case "5": option5.setAttribute("selected", true);
			document.getElementById("ventes").setAttribute("checked", true); break;
	}
	affichageFiltres()
}

function affichageFiltres() {
	selected = document.querySelector('input[name="buySell"]:checked');
	if (selected.value == "achats") {
		document.getElementById("selectVentes").setAttribute("disabled", true);
		document.getElementById("selectAchats").removeAttribute("disabled", true);

	} else if (selected.value == "ventes") {
		document.getElementById("selectAchats").setAttribute("disabled", true);
		document.getElementById("selectVentes").removeAttribute("disabled", true);
	}
}


