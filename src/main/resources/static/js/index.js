import MenuManager from './MenuManager.js'
import UtilisateurManager from './UtilisateurManager.js'

MenuManager.toggle();

if (window.location.pathname.includes("creer-compte")) {
	const motDePasseManager=new UtilisateurManager("creer-compte-form")
	motDePasseManager.verifierMdp();
}
if (window.location.pathname.includes("mot-de-passe")) {
	const motDePasseManager=new UtilisateurManager("modifer-mot-de-passe")
	motDePasseManager.verifierMdp();
}