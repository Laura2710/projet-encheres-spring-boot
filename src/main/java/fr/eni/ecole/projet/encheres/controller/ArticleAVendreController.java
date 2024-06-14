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
	public String afficherArticleAVendre(Model model) {
		List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreEnCours();
		model.addAttribute("articlesAVendre", articlesAVendre);
		List<Categorie> listCategorie = articleAVendreService.getAllCategories();
		model.addAttribute("listCategorie", listCategorie);
		// Ajout au model ma variable "nomRecherché" qui contiendra la chaine de
		// caractère a retrouver dans le nom des articles
		String nomRecherché = null;
		model.addAttribute("nomRecherché", nomRecherché);
		// Ajout au model de ma variable categorieRecherché qui contiendra l'id de la
		// catégorie a rechercher
		int categorieRecherché = 0;
		model.addAttribute("categorieRecherché", categorieRecherché);
		return "index";
	}

	@GetMapping("/vendre")
	public String vendreArticle(Model model, Principal principal) {
		String pseudo = principal.getName();
		Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);

		List<Categorie> categories = this.articleAVendreService.getAllCategories();
		List<Adresse> adressesRetrait = this.articleAVendreService.getAllAdressesRetrait();
		if (utilisateurSession != null && !utilisateurSession.isAdministrateur()) {
			model.addAttribute("articleAVendre", new ArticleAVendre());
			model.addAttribute("categories", categories);
			model.addAttribute("adressesRetrait", adressesRetrait);
			return "view-vente-article";
		} else {
			return "redirect:/index";
		}
	}

	@PostMapping("/vendre")
	public String vendreArticle(Principal principal,
			@Valid @ModelAttribute("articleAVendre") ArticleAVendre articleAVendre, BindingResult bindingResult) {
		String pseudo = principal.getName();
		Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);
		if (utilisateurSession != null && !utilisateurSession.isAdministrateur()) {
			if (!bindingResult.hasErrors()) {
				try {
					articleAVendreService.mettreArticleEnVente(articleAVendre);
					return "redirect:/index";
				} catch (BusinessException be) {
					be.getClefsExternalisations().forEach(key -> {
						ObjectError error = new ObjectError("globalError", key);
						bindingResult.addError(error);
					});
				}
			}
		} else {
			ObjectError error = new ObjectError("globalError", BusinessCode.VALIDATION_UTILISATEUR_ADMIN);
			bindingResult.addError(error);
			return "redirect:/index";
		}

		return "index";
	}
	
	@GetMapping("/vente/annuler") 
	public String annulerVente(@RequestParam("id") int idArticle, Principal principal, Model model) {
		ArticleAVendre article = this.articleAVendreService.getById(idArticle);
		// Vérifier que le vendeur est l'utilisateur connecté
		if (article.getVendeur().getPseudo().equals(principal.getName())) {
			try {
				this.articleAVendreService.annulerVente(article);
			}
			catch (BusinessException e) {
				List<String> errors = new ArrayList<String>();
				e.getClefsExternalisations().forEach(key -> {
					errors.add(key);
				});
				model.addAttribute("errorBLL", errors);
				return "view-vente-article";
			}
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

			if (article.getStatut() == 1) {
				model.addAttribute("enchereForm", enchere);
			}

			return "view-detail-vente";

		} catch (BusinessException e) {
			List<String> errors = new ArrayList<String>();
			e.getClefsExternalisations().forEach(key -> {
				errors.add(key);
			});
			model.addAttribute("errorBLL", errors);
			return "view-detail-vente";
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
			
	        // Vérification du statut de l'article et du vendeur pour rediriger si nécessaire
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
				handleRuntimeException(e, model);
				return "view-detail-vente";
			}

		} catch (BusinessException e) {
	        handleBusinessException(e, model);
			return "view-detail-vente";
		}

		return "redirect:/encheres/detail?id=" + enchereSoumise.getArticleAVendre().getId();
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
	
	
	private void handleBusinessException(BusinessException e, Model model) {
	    List<String> errors = e.getClefsExternalisations();
	    model.addAttribute("errorBLL", errors);
	}

	private void handleBusinessException(BusinessException e, BindingResult bindingResult, Model model,
	                                      Utilisateur utilisateur, int idArticle) {
	    List<String> errors = e.getClefsExternalisations();
	    errors.forEach(key -> {
	        ObjectError error = new ObjectError("globalError", key);
	        bindingResult.addError(error);
	    });
	    preparerDonneesEnchere(model, utilisateur, idArticle);
	}

	private void handleRuntimeException(RuntimeException e, Model model) {
	    model.addAttribute("errorBLL", "validation.offre.donnees.inaccessibles");
	}
}
