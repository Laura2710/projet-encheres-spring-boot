package fr.eni.ecole.projet.encheres.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.ecole.projet.encheres.bll.UtilisateurService;
import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;

@Controller
@SessionAttributes({"utilisateurSession"})
public class ConnexionController {
	
	UtilisateurService utilisateurService;
	
	public ConnexionController(UtilisateurService utilisateurService) {
		this.utilisateurService = utilisateurService;
	}
	
	@ModelAttribute("utilisateurSession")
	public Utilisateur initialiserUtilisateur() {
		return new Utilisateur();
	}
	

	@GetMapping("/login")
	public String redirectionConnexion(@RequestParam(value = "error", required = false) String error, Model model) {
		  if (error != null) {
	            model.addAttribute("errorMessage", "Identifiant ou mot de passe invalide. Réessayez");
	        }
		return "login";
	}
	
	@GetMapping("/session")
	public String initialiserSessionUtilisateur(Principal principal, @ModelAttribute("utilisateurSession") Utilisateur utilisateurSession) {
		String pseudo = principal.getName();
		Utilisateur utilisateur = this.utilisateurService.getByPseudo(pseudo);
		System.out.println(utilisateur);
		if (utilisateur.getPseudo() != null) {
			utilisateurSession.setPseudo(pseudo);
			utilisateurSession.setNom(utilisateur.getNom());
			utilisateurSession.setPrenom(utilisateur.getPrenom());
			utilisateurSession.setAdministrateur(utilisateur.isAdministrateur());
			utilisateurSession.setCredit(utilisateur.getCredit());
			
			Adresse adresse = this.utilisateurService.getAdresseByID(utilisateur.getAdresse().getId());
			utilisateurSession.setAdresse(adresse);
		}
		else {
			return "login";
		}
		return "redirect:/";
	}
	


}
