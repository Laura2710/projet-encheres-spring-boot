import MenuManager from './MenuManager.js'
import UtilisateurManager from './UtilisateurManager.js'
import PhotoManager from './PhotoManager.js'
import FiltreManager from './FiltreManager.js'

MenuManager.toggle();

if (window.location.pathname.includes("creer-compte")) {
	const motDePasseManager=new UtilisateurManager("creer-compte-form")
	motDePasseManager.verifierMdp();
}

if (window.location.pathname.includes("mot-de-passe")) {
	const motDePasseManager=new UtilisateurManager("modifer-mot-de-passe")
	motDePasseManager.verifierMdp();
}



if (window.location.pathname.includes("ajouter-photo")) {
	const photoManager= new PhotoManager();
	photoManager.previewPhoto();
}

if (window.location.pathname == "/") {
	FiltreManager.toggleFiltre();
}