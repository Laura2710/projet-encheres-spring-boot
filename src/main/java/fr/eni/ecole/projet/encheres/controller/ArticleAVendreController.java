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

import fr.eni.ecole.projet.encheres.bll.ArticleAVendreService;
import fr.eni.ecole.projet.encheres.bll.UtilisateurService;
import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Categorie;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;
import fr.eni.ecole.projet.encheres.exceptions.BusinessCode;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class ArticleAVendreController {

	ArticleAVendreService articleAVendreService;

	UtilisateurService utilisateurService;

	public ArticleAVendreController(ArticleAVendreService articleAVendreService,
			UtilisateurService utilisateurService) {
		this.articleAVendreService = articleAVendreService;
		this.utilisateurService = utilisateurService;
	}

	@GetMapping
	public String afficherArticleAVendre(Model model, Principal principal) {

		List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreEnCours();
		model.addAttribute("articlesAVendre", articlesAVendre);
		List<Categorie> listCategorie = articleAVendreService.getAllCategories();
		model.addAttribute("listCategorie", listCategorie);
		// Ajout au model ma variable "nomRecherche" qui contiendra la chaine de
		// caractère a retrouver dans le nom des articles
		String nomRecherche = null;
		model.addAttribute("nomRecherche", nomRecherche);
		// Ajout au model de ma variable categorieRecherche qui contiendra l'id de la
		// catégorie a rechercher
		int categorieRecherche = 0;
		model.addAttribute("categorieRecherche", categorieRecherche);
//		Ajout de la condition "est connecté"
		if (principal != null) {
			String pseudo = principal.getName();
			Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);
			// Ajout des parametres utiles aux filtres si l'utilisateurs est connecté et non
			// Admin.
			if (utilisateurSession != null && !utilisateurSession.isAdministrateur()) {
				// Parametre pour les input Select
				int casUtilisationFiltres = 0;
				model.addAttribute("casUtilisationFiltres", casUtilisationFiltres);
			}
		}
		return "index";
	}

	// TODO Ajoux des liens si connecté sur les artciles
	// TODO Mutualisation de code
	// TODO Methode GetAllCategorie en attribut
	// TODO Sécurité et Validation
	// TODO Css et JS pour enable les select en fonction du radio (statutRecherche
	// et mesEncheresRecherche = defaut si enable)

	@PostMapping("/rechercher")
	public String afficherArticleAVendre(@RequestParam(value = "nomRecherche") String nomRecherche,
			@RequestParam(value = "categorieRecherche") int categorieRecherche,
			@RequestParam(value = "casUtilisationFiltres") int casUtilisationFiltres, Model model,
			Principal principal) {
		List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreAvecParamètres(nomRecherche,
				categorieRecherche, casUtilisationFiltres, principal);
		model.addAttribute("articlesAVendre", articlesAVendre);
		List<Categorie> listCategorie = articleAVendreService.getAllCategories();
		model.addAttribute("listCategorie", listCategorie);
		model.addAttribute("nomRecherche", nomRecherche);
		model.addAttribute("categorieRecherche", categorieRecherche);

		if (principal != null) {
			String pseudo = principal.getName();
			Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);
			// Ajout des parametres utiles aux filtres si l'utilisateurs est connecté et non
			// Admin.
			if (utilisateurSession != null && !utilisateurSession.isAdministrateur()) {
				// Parametre pour les input select
				model.addAttribute("casUtilisationFiltres", casUtilisationFiltres);
			}
		}
		return "index";

	}

	@GetMapping("/vendre")
	public String vendreArticle(Model model, Principal principal) {
		try {
			String pseudo = principal.getName();
			Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);
			if (utilisateurSession != null && !utilisateurSession.isAdministrateur()) {
				model.addAttribute("articleAVendre", new ArticleAVendre());
				model.addAttribute("modeModif", false);
				model.addAttribute("action", "/vendre");
				return "view-vente-article";
			} else {
				return "redirect:/index";
			}
		} catch (Exception e) {
			model.addAttribute("error", "Impossible d'enregister la vente");
		}
		return "view-vente-article";
	}

	@PostMapping("/vendre")
	public String vendreArticle(@Valid @ModelAttribute("articleAVendre") ArticleAVendre articleAVendre,
			BindingResult bindingResult, Principal principal, Model model) {

		String pseudo = principal.getName();
		Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);

		if (bindingResult.hasErrors()) {
			model.addAttribute("articleAVendre", articleAVendre);
			model.addAttribute("modeModif", false);
			model.addAttribute("action", "/vendre");
		}

		if (utilisateurSession != null && !utilisateurSession.isAdministrateur()) {
			if (!bindingResult.hasErrors()) {
				try {
					articleAVendreService.mettreArticleEnVente(articleAVendre, utilisateurSession);
					return "redirect:/";
				} catch (BusinessException be) {
					List<String> clefs = be.getClefsExternalisations();
					if (clefs != null) {
						clefs.forEach(key -> {
							ObjectError error = new ObjectError("globalError", key);
							bindingResult.addError(error);
						});
					}
				}
			}
		} else {
			ObjectError error = new ObjectError("globalError", BusinessCode.VALIDATION_UTILISATEUR_ADMIN);
			bindingResult.addError(error);
			return "redirect:/";
		}
		return "view-vente-article";
	}

	@ModelAttribute("categories")
	public List<Categorie> injecteCategorie() {
		List<Categorie> categories = this.articleAVendreService.getAllCategories();
		return categories;
	}

	@ModelAttribute("adressesRetrait")
	public List<Adresse> injecteAdresse() {
		List<Adresse> adressesRetrait = this.articleAVendreService.getAllAdressesRetrait();
		return adressesRetrait;
	}

	@GetMapping("/vendre/modifier")
	public String modifierArticle(@RequestParam("id") int idArticle, Model model, Principal principal) {
		try {
			ArticleAVendre article = this.articleAVendreService.getById(idArticle);
			if ((article.getStatut() == 0) && principal.getName().equals(article.getVendeur().getPseudo())) {
				model.addAttribute("articleAVendre", article);
				model.addAttribute("modeModif", true);
				model.addAttribute("action", "/vendre/modifier");
				return "view-vente-article";
			} else {
				return "redirect:/";
			}
		} catch (Exception e) {
			model.addAttribute("error", "Impossible de modifier la vente");
			return "redirect:/";
		}
	}

	@PostMapping("/vendre/modifier")
	public String modifierArticle(@Valid @ModelAttribute("articleAVendre") ArticleAVendre articleAVendre,
			BindingResult bindingResult, Principal principal, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("modeModif", true);
			model.addAttribute("action", "/vendre/modifier");
			return "view-vente-article";
		}
		try {
			if (articleAVendre.getStatut() == 0
					&& principal.getName().equals(articleAVendre.getVendeur().getPseudo())) {
				if (!bindingResult.hasErrors()) {
					try {
						articleAVendreService.modifierArticleEnVente(articleAVendre);
						return "redirect:/";
					} catch (BusinessException be) {
						be.getClefsExternalisations().forEach(key -> {
							ObjectError error = new ObjectError("globalError", key);
							bindingResult.addError(error);
						});
					}
				}
			} else {
				ObjectError error = new ObjectError("globalError",
						BusinessCode.VALIDATION_UTILISATEUR_NON_CREATEUR_VENTE);
				bindingResult.addError(error);
				return "redirect:/";
			}
		} catch (BusinessException be) {
			be.getClefsExternalisations().forEach(key -> {
				ObjectError error = new ObjectError("globalError", key);
				bindingResult.addError(error);
			});
		}
		return "index";
	}

	@GetMapping("/vente/annuler")
	public String annulerVente(@RequestParam("id") int idArticle, Principal principal, Model model) {
		try {
			ArticleAVendre article = this.articleAVendreService.getById(idArticle);
			// Vérifier que le vendeur est l'utilisateur connecté
			if (article.getVendeur().getPseudo().equals(principal.getName())) {
				try {
					this.articleAVendreService.annulerVente(article);
				} catch (BusinessException e) {
					List<String> errors = new ArrayList<String>();
					e.getClefsExternalisations().forEach(key -> {
						errors.add(key);
					});
					model.addAttribute("errorBLL", errors);
					model.addAttribute("articleAVendre", article);
					return "view-vente-article";
				}
			}
		} catch (BusinessException e) {
			return afficherVueErreur(e, model);
		}
		return "redirect:/";
	}
	
	private String afficherVueErreur(BusinessException e, Model model) {
		List<String> errors = new ArrayList<String>();
		e.getClefsExternalisations().forEach(key -> {
			errors.add(key);
		});
		model.addAttribute("errorBLL", errors);
		return "view-errors";
	}

}
