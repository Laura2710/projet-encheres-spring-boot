import MenuManager from './MenuManager.js'
import UtilisateurManager from './UtilisateurManager.js'
import PhotoManager from './PhotoManager.js'

MenuManager.toggle();

if (window.location.pathname.includes("creer-compte")) {
	UtilisateurManager.verifierMdp();
}


if (window.location.pathname.includes("ajouter-photo")) {
	const photoManager= new PhotoManager();
	photoManager.previewPhoto();
}
