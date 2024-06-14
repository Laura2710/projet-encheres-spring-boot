import MenuManager from './MenuManager.js'
import UtilisateurManager from './UtilisateurManager.js'

MenuManager.toggle();

if (window.location.pathname.includes("creer-compte")) {
	UtilisateurManager.verifierMdp();
}
