package fr.eni.ecole.projet.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import fr.eni.ecole.projet.encheres.bll.UtilisateurService;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;

@Controller
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
	


}
