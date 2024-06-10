'use strict'

export default class UtilisateurManager {

	static verifierMdpPattern() {
		let form = document.getElementById("creer-compte-form");
		form.addEventListener("submit", (e) => {
			e.preventDefault();
			const inputs = this.formAdd.elements;
			const password = inputs["password"].value;
			const regex = /(?=.*[0-9])(?=.*[A-Z])(?=.*[?!@#_-])(?=.*[a-z]).{8,}/gm;

			let isValid = regex.test(password);
			if (isValid) {
				e.target.submit();
			}
			else {
				let p = document.createElement("p");
				p.classList.add("errors");
				p.innerText = "Le mot de passe doit contenir au moins : 1 majuscule, 1 chiffre, 1 caractère spécial";
				inputs["password"].after(p);
			}

		})
	}

	static verifierCorrespondance() {
		// TODO
	}

}