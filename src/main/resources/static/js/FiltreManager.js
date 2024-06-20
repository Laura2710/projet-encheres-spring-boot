'use strict'

export default class FiltreManager {
	
	static toggleFiltre() {
		const btnFiltre = document.getElementById("btnFiltre");
		btnFiltre.addEventListener("click", () => {
			const containerFiltre = document.getElementById("containerFiltre");
			if(containerFiltre.classList.contains("hideMobile")) {
				containerFiltre.classList.remove("hideMobile");
			}
			else {
				containerFiltre.classList.add("hideMobile");
			}
		})
	}
	
}