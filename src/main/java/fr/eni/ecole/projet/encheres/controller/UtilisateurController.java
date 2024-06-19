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
	public String creerUnCompte(@Valid @ModelAttribute("utilisateur") Utilisateur utilisateur,
			BindingResult bindingResult, @RequestParam("confirmationMdp") String confirmationMdp) {
		if (!utilisateur.getMotDePasse().equals(confirmationMdp)) {
			return "view-creer-compte";
		}
		if (!bindingResult.hasErrors()) {
			try {
				utilisateurService.creerUnCompte(utilisateur);
			} catch (BusinessException e) {
				e.getClefsExternalisations().forEach(key -> {
					ObjectError error = new ObjectError("globalError", key);
					bindingResult.addError(error);
				});
				return "view-creer-compte";
			}
		} else {
			return "view-creer-compte";
		}
		return "redirect:/";
	}

	@GetMapping("/profil")
	public String afficherMonProfil(@RequestParam(name = "id", required = false) String pseudoParam, Model model,
			Principal principal) {
		String pseudo = principal.getName();
		if (pseudoParam == null || (pseudoParam != null && pseudoParam.equals(pseudo))) {
			Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);
			model.addAttribute("utilisateur", utilisateurSession);
			//model.addAttribute
		}
		if (pseudoParam != null && pseudoParam != pseudo) {
			Utilisateur utilisateur = this.utilisateurService.getByPseudo(pseudoParam);
			model.addAttribute("utilisateur", utilisateur);
			
		}
		return "view-profil";
	}
	
	@GetMapping("/modifier-profil")
	public String modifiermonProfil(Model model,Principal principal) {
		String pseudo = principal.getName();
		Utilisateur utilisateurSession = this.utilisateurService.getInfoUtilisateur(pseudo);
		model.addAttribute("utilisateur", utilisateurSession); 
		return "view-profil-form";
	}
	
	@PostMapping("/modifier-profil")
	public String modifierProfilUtilisateur(@Valid @ModelAttribute("utilisateur") Utilisateur utilisateur, BindingResult bindingResult, Principal principal, Model model) {
		
		if (bindingResult.hasErrors()) {
			return "view-profil-form";
		}
		
		try {
			this.utilisateurService.miseAjourProfil(utilisateur, principal.getName() );			
		}
		catch (BusinessException e) {
			e.getClefsExternalisations().forEach(key -> {
				ObjectError error = new ObjectError("globalError", key);
				bindingResult.addError(error);
			});
		}
		
		return "view-profil-form";
	}
	
	@GetMapping("/modifier-mot-de-passe")
	public String modifiermotdePasse(@RequestParam(name = "id", required = false) String pseudoParam, Model model,
			Principal principal) {
		String pseudo = principal.getName();
		if (pseudoParam == null || (pseudoParam != null && pseudoParam.equals(pseudo))) {
			Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);
			model.addAttribute("utilisateur", utilisateurSession);
			//model.addAttribute
		}
		if (pseudoParam != null && pseudoParam != pseudo) {
			Utilisateur utilisateur = this.utilisateurService.getByPseudo(pseudoParam);
			model.addAttribute("utilisateur", utilisateur);
	
		}
		return "view-mdp";
	}
	}

			

	