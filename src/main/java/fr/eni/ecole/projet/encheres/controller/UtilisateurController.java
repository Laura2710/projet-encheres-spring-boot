package fr.eni.ecole.projet.encheres.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Affiche la page de création de compte.
	 *
	 * @param model Le modèle pour la vue.
	 * @return La vue pour créer un compte.
	 */
	@GetMapping("/creer-compte")
	public String creerCompte(Model model) {
		Utilisateur utilisateur = new Utilisateur();
		model.addAttribute("utilisateur", utilisateur);
		return "view-creer-compte";
	}

	/**
	 * Traite la création d'un compte utilisateur.
	 *
	 * @param utilisateur     L'utilisateur à créer.
	 * @param bindingResult   Les résultats de la validation.
	 * @param confirmationMdp La confirmation du mot de passe.
	 * @return La vue à afficher.
	 */
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

	/**
	 * Affiche le profil de l'utilisateur.
	 *
	 * @param pseudoParam Le pseudo de l'utilisateur à afficher.
	 * @param model       Le modèle pour la vue.
	 * @param principal   Les informations de l'utilisateur connecté.
	 * @return La vue du profil.
	 */
	@GetMapping("/profil")
	public String afficherMonProfil(@RequestParam(name = "id", required = false) String pseudoParam, Model model,
			Principal principal) {
		String pseudo = principal.getName();
		if (pseudoParam == null || (pseudoParam != null && pseudoParam.equals(pseudo))) {
			Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);
			model.addAttribute("utilisateur", utilisateurSession);
		}
		if (pseudoParam != null && pseudoParam != pseudo) {
			Utilisateur utilisateur = this.utilisateurService.getByPseudo(pseudoParam);
			model.addAttribute("utilisateur", utilisateur);

		}
		return "view-profil";
	}

	/**
	 * Affiche le formulaire de modification du profil de l'utilisateur.
	 *
	 * @param model     Le modèle pour la vue.
	 * @param principal Les informations de l'utilisateur connecté.
	 * @return La vue du formulaire de modification de profil.
	 */
	@GetMapping("/modifier-profil")
	public String modifiermonProfil(Model model, Principal principal) {
		String pseudo = principal.getName();
		Utilisateur utilisateurSession = this.utilisateurService.getInfoUtilisateur(pseudo);
		model.addAttribute("utilisateur", utilisateurSession);
		return "view-profil-form";
	}

	/**
	 * Traite la modification du profil de l'utilisateur.
	 *
	 * @param utilisateur   L'utilisateur à mettre à jour.
	 * @param bindingResult Les résultats de la validation.
	 * @param principal     Les informations de l'utilisateur connecté.
	 * @param model         Le modèle pour la vue.
	 * @return La vue à afficher.
	 */
	@PostMapping("/modifier-profil")
	public String modifierProfilUtilisateur(@Valid @ModelAttribute("utilisateur") Utilisateur utilisateur,
			BindingResult bindingResult, Principal principal, Model model) {

		if (bindingResult.hasErrors()) {
			return "view-profil-form";
		}

		try {
			this.utilisateurService.miseAjourProfil(utilisateur, principal.getName());
		} catch (BusinessException e) {
			e.getClefsExternalisations().forEach(key -> {
				ObjectError error = new ObjectError("globalError", key);
				bindingResult.addError(error);
			});
		}

		return "view-profil";
	}

	/**
	 * Affiche le formulaire de modification du mot de passe.
	 *
	 * @param pseudoParam Le pseudo de l'utilisateur.
	 * @param model       Le modèle pour la vue.
	 * @param principal   Les informations de l'utilisateur connecté.
	 * @return La vue du formulaire de modification du mot de passe.
	 */
	@GetMapping("/modifier-mot-de-passe")
	public String modifiermotdePasse(@RequestParam(name = "id", required = false) String pseudoParam, Model model,
			Principal principal) {
		String pseudo = principal.getName();
		if (pseudoParam == null || (pseudoParam != null && pseudoParam.equals(pseudo))) {
			Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);
			model.addAttribute("utilisateur", utilisateurSession);
		}
		if (pseudoParam != null && pseudoParam != pseudo) {
			Utilisateur utilisateur = this.utilisateurService.getByPseudo(pseudoParam);
			model.addAttribute("utilisateur", utilisateur);

		}
		return "view-mdp";
	}

	/**
	 * Traite la modification du mot de passe de l'utilisateur.
	 *
	 * @param ancienMotDePasse  L'ancien mot de passe de l'utilisateur.
	 * @param nouveauMotDePasse Le nouveau mot de passe de l'utilisateur.
	 * @param confirmationMdp   La confirmation du nouveau mot de passe.
	 * @param principal         Les informations de l'utilisateur connecté.
	 * @param model             Le modèle pour la vue.
	 * @return La vue à afficher.
	 */
	@PostMapping("/modifier-mot-de-passe")
	public String modifierMotDePasse(@RequestParam("ancienMotDePasse") String ancienMotDePasse,
			@RequestParam("motDePasse") String nouveauMotDePasse,
			@RequestParam("confirmationMdp") String confirmationMdp, Principal principal, Model model) {

		List<String> errors = new ArrayList<>();
		if (!nouveauMotDePasse.equals(confirmationMdp)) {
			errors.add("Les mots de passe de correspondent pas");
			model.addAttribute("errorBLL", errors);
			return "view-mdp";
		}

		String pseudo = principal.getName();
		Utilisateur utilisateur = utilisateurService.getByPseudo(pseudo);

		try {
			this.utilisateurService.updateMotDePasse(ancienMotDePasse, nouveauMotDePasse, utilisateur);
			return "redirect:/utilisateur/profil";
		} catch (BusinessException e) {
			e.getClefsExternalisations().forEach(key -> {
				errors.add(key);
			});
			model.addAttribute("errorBLL", errors);
			return "view-mdp";
		}

	}
}
