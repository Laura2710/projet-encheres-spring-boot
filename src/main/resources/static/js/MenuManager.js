'use strict'

export default class MenuManager {
	static toggle() {
		const menu = document.getElementById("menu");
		const navContainer = document.getElementById("navContainer");
		const menuBar1 = document.getElementById("menuBar1");
		const menuBar2 = document.getElementById("menuBar2");
		const menuBar3 = document.getElementById("menuBar3");
		
		menu.addEventListener("click", ()=> {
			if(navContainer.classList.contains("hideMobile")) {
				navContainer.classList.remove("hideMobile");
				menuBar1.classList.add("rotate45");
				menuBar2.classList.add("hide");
				menuBar3.classList.add("rotateMinus45");
			}
			else {
				navContainer.classList.add("hideMobile");
				menuBar1.classList.remove("rotate45");
				menuBar2.classList.remove("hide");
				menuBar3.classList.remove("rotateMinus45");
			}
			
		})
	}	
}