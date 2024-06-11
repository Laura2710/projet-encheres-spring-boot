'use strict'

export default class UtilisateurManager {
	static verifierMdp() {
		let form = document.getElementById("creer-compte-form");
		form.addEventListener("submit", (e) => {
			e.preventDefault();

			UtilisateurManager.deleteErrorsMsg();

			const inputs = form.elements;
			const password = inputs["motDePasse"].value;
			const confirmPassword = inputs["confirmationMdp"].value;
			const regex = /(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&\-_=+])(?=.*[a-z]).{8,}/gm;

			let errors = [];
			
			if (!regex.test(password)) {
				errors.push("Le mot de passe doit contenir au moins : 1 majuscule, 1 chiffre, 1 caractère spécial");
			}

			if (password !== confirmPassword) {
				errors.push("Les mots de passe ne correspondent pas");
			}

			if (errors.length > 0) {
				const listErrors = document.createElement("ul");
				listErrors.classList.add("listErrors");

				errors.forEach(error => {
					let li = document.createElement("li");
					li.classList.add("errors");
					li.innerText = error;
					listErrors.appendChild(li);
				});
				
				let btnActions = document.getElementsByClassName("btnActions");
				btnActions[0].before(listErrors);
			} else {
				e.target.submit();
			}
		});
	}

	static deleteErrorsMsg() {
		let errors = document.getElementsByClassName("listErrors");
		while (errors.length > 0) {
			errors[0].remove();
		}
	}
}
