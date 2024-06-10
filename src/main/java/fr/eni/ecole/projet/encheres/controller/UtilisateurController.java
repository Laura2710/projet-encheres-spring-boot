package fr.eni.ecole.projet.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.eni.ecole.projet.encheres.bo.Utilisateur;

@Controller
@RequestMapping("/utilisateur")
public class UtilisateurController {
	
	@GetMapping("/creer-compte")
	public String creerCompte(Model model) {
		Utilisateur utilisateur = new Utilisateur();
		model.addAttribute("utilisateur", utilisateur);
		return "view-creer-compte";
	}

	@PostMapping("/creer-compte")
	public String creerUnCompte(@ModelAttribute("utilisateur") Utilisateur utilisateur, @RequestParam("confirmationMdp") String confirmationMdp) {
		System.out.println(utilisateur);
		System.out.println(confirmationMdp);
		
		if (utilisateur.getMotDePasse() != confirmationMdp) {
			return "view-creer-compte";
		}
		
		return "redirect:/";
	}
}
