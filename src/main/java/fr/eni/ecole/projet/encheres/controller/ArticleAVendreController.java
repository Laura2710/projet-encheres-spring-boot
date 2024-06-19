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
import fr.eni.ecole.projet.encheres.bo.Enchere;
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

				System.out.println(article);
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

	@GetMapping("/encheres/detail")
	public String voirDetailEnchere(@RequestParam("id") int idArticle, Model model, Principal principal) {
		try {
			Utilisateur utilisateur = utilisateurService.getByPseudo(principal.getName());
			ArticleAVendre article = this.articleAVendreService.getById(idArticle);
			Enchere enchere = this.articleAVendreService.getEnchereByIdArticle(idArticle);
			injecterDonneesEnchere(model, utilisateur, article, enchere);

			boolean isAcquereur = enchere.getAcquereur() != null;
			boolean isAcquereurConnecte = isAcquereur
					&& enchere.getAcquereur().getPseudo().equals(utilisateur.getPseudo());
			boolean isVendeurConnecte = article.getVendeur().getPseudo().equals(utilisateur.getPseudo());

			// SI UNE VENTE EST N'A PAS COMMENCE
			if (article.getStatut() == 0) {
				model.addAttribute("showNomArticle", true);
			}

			// SI UNE VENTE EST EN COURS
			if (article.getStatut() == 1) {
				model.addAttribute("enchereForm", enchere);
				model.addAttribute("showNomArticle", true);
			}

			// SI UNE VENTE EST TERMINEE OU LIVREE, AFFICHER TELEPHONE
			if (article.getStatut() == 2 || article.getStatut() == 3) {
				model.addAttribute("showTelephone", true);
			}

			// SI UNE VENTE EST TERMINEE
			if (article.getStatut() == 2) {
				if (isAcquereurConnecte) {
					model.addAttribute("cloture", "acquereur");
				} else if (isAcquereur && isVendeurConnecte) {
					model.addAttribute("cloture", "vendeur");
					model.addAttribute("acquereur", enchere.getAcquereur().getPseudo());
				} else if (!isAcquereur && isVendeurConnecte) {
					model.addAttribute("cloture", "clotureSansAcquereur");
				} else {
					model.addAttribute("showNomArticle", true);
				}
			}

			// SI UNE VENTE A ETE LIVREE
			if (article.getStatut() == 3) {
				if (isAcquereur && isVendeurConnecte) {
					model.addAttribute("statut", "livraison");
					model.addAttribute("acquereur", enchere.getAcquereur().getPseudo());
				}
			}

			// SI UNE VENTE A ETE ANNULEE
			if (article.getStatut() == 100 && isVendeurConnecte) {
				model.addAttribute("statut", "annulee");
			}

			return "view-detail-vente";

		} catch (BusinessException e) {
			return afficherVueErreur(e, model);
		}
	}

	@PostMapping("/encheres/detail")
	public String soumettreOffreEnchere(@Valid @ModelAttribute("enchereForm") Enchere enchereSoumise,
			BindingResult bindingResult, Principal principal, Model model) {

		// Récupération de l'utilisateur connecté à partir de son pseudo
		Utilisateur utilisateur = utilisateurService.getByPseudo(principal.getName());

		// Récupération de l'identifiant de l'article soumis pour enchère
		int idArticle = (int) enchereSoumise.getArticleAVendre().getId();

		try {
			// Récupération de l'article s'il existe et de son enchère associée
			ArticleAVendre article = articleAVendreService.getById(idArticle);

			// Vérification du statut de l'article et du vendeur pour rediriger si
			// nécessaire
			if (article.getStatut() != 1 || article.getVendeur().getPseudo().equals(principal.getName())) {
				return "redirect:/encheres/detail?id=" + enchereSoumise.getArticleAVendre().getId();
			}

			// Vérification des erreurs de validation du formulaire
			if (bindingResult.hasErrors()) {
				Enchere enchere = articleAVendreService.getEnchereByIdArticle(idArticle);
				injecterDonneesEnchere(model, utilisateur, article, enchere);
				return "view-detail-vente";
			}

			try {
				// Soumission de l'offre pour l'enchère
				articleAVendreService.faireUneOffre(enchereSoumise, utilisateur);
			} catch (BusinessException e) {
				handleBusinessException(e, bindingResult, model, utilisateur, idArticle);
				return "view-detail-vente";
			} catch (RuntimeException e) { // Capturer l'exception venant de @Transactionnal
				model.addAttribute("errorBLL", "validation.offre.donnees.inaccessibles");
				return "view-errors";
			}

		} catch (BusinessException e) {
			return afficherVueErreur(e, model);
		}

		return "redirect:/encheres/detail?id=" + enchereSoumise.getArticleAVendre().getId();
	}
	
	
	

	/**
	 * Gère la requête de retrait d'une enchère.
	 * 
	 * @param idArticle : L'ID de l'article.
	 * @param principal : l'utilisateur actuellement connecté.
	 * @param model     : pour ajouter des attributs dans la vue.
	 * @return URL de redirection vers la page d'accueil.
	 * 
	 * @throws BusinessException Si une erreur liée à la logique métier se produit.
	 */
	@GetMapping("/encheres/retrait")
	public String retraitEnchere(@RequestParam("id") int idArticle, Principal principal, Model model) {
		try {
			// Récupération de l'article s'il existe
			ArticleAVendre article = articleAVendreService.getById(idArticle);
			// Vérifie si l'utilisateur actuellement connecté est le vendeur de l'article
			if (article.getVendeur().getPseudo().equals(principal.getName())) {
				this.articleAVendreService.effectuerRetrait(article, principal.getName());
			}
		} catch (BusinessException e) {
			return afficherVueErreur(e, model);
		}
		return "redirect:/"; 
	}

	
	
	
	private void injecterDonneesEnchere(Model model, Utilisateur utilisateur, ArticleAVendre article, Enchere enchere) {
		model.addAttribute("utilisateur", utilisateur);
		model.addAttribute("article", article);
		model.addAttribute("enchere", enchere);
		boolean leVendeurEstConnecte = article.getVendeur().getPseudo().equals(utilisateur.getPseudo());
		model.addAttribute("leVendeurEstConnecte", leVendeurEstConnecte);
	}

	private void preparerDonneesEnchere(Model model, Utilisateur utilisateur, int idArticle) {
		ArticleAVendre article = articleAVendreService.getById(idArticle);
		Enchere enchere = articleAVendreService.getEnchereByIdArticle(idArticle);
		injecterDonneesEnchere(model, utilisateur, article, enchere);
	}

	private void handleBusinessException(BusinessException e, BindingResult bindingResult, Model model,
			Utilisateur utilisateur, int idArticle) {
		e.getClefsExternalisations().forEach(key -> {
			ObjectError error = new ObjectError("globalError", key);
			bindingResult.addError(error);
		});
		preparerDonneesEnchere(model, utilisateur, idArticle);
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
