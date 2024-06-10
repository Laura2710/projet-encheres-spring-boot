import UtilisateurManager from './UtilisateurManager.js'

if (window.location.pathname.includes("creer-compte")) {
	UtilisateurManager.verifierMdpPattern();
}
