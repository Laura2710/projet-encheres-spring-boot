package fr.eni.ecole.projet.encheres.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.eni.ecole.projet.encheres.bll.UtilisateurService;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/utilisateur")
public class UtilisateurController {
	
	UtilisateurService utilisateurService;
	
	
	public UtilisateurController(UtilisateurService utilisateurService) {
		this.utilisateurService = utilisateurService;
	}

	@GetMapping("/creer-compte")
	public String creerCompte(Model model) {
		Utilisateur utilisateur = new Utilisateur();
		model.addAttribute("utilisateur", utilisateur);
		return "view-creer-compte";
	}

	@PostMapping("/creer-compte")
	public String creerUnCompte(@Valid @ModelAttribute("utilisateur") Utilisateur utilisateur, BindingResult bindingResult, @RequestParam("confirmationMdp") String confirmationMdp) {
		if (!utilisateur.getMotDePasse().equals(confirmationMdp)) {
			return "view-creer-compte";
		}
		if (!bindingResult.hasErrors()) {
			try {
				utilisateurService.creerUnCompte(utilisateur);
			}
			catch (BusinessException e) {
				e.getClefsExternalisations().forEach(key -> {
					ObjectError error = new ObjectError("globalError", key);
					bindingResult.addError(error);
				});
				return "view-creer-compte";
			}
		}
		else {
			return "view-creer-compte";
		}
		return "redirect:/";
	}
	
	@GetMapping("/profil")
	public String afficherMonProfil(Model model, Principal principal) {
		String pseudo = principal.getName();
		Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);
model.addAttribute("utilisateur",utilisateurSession);
			return "view-profil";
	}
}
