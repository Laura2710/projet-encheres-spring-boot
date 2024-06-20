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
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Enchere;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/encheres")
public class EnchereController {

	UtilisateurService utilisateurService;
	ArticleAVendreService articleAVendreService;

	public EnchereController(UtilisateurService utilisateurService, ArticleAVendreService articleAVendreService) {
		this.utilisateurService = utilisateurService;
		this.articleAVendreService = articleAVendreService;
	}

	/**
	 * Affiche les détails d'une enchère pour un article spécifique.
	 * 
	 * @param idArticle L'identifiant de l'article dont les détails de l'enchère doivent être affichés.
	 * @param model permettant d'ajouter des attributs à la vue.
	 * @param principal représentant l'utilisateur actuellement connecté.
	 * @return Le nom de la vue à afficher.
	 * @throws BusinessException Si une erreur se produit lors de la récupération des données.
	 */
	@GetMapping("/detail")
	public String voirDetailEnchere(@RequestParam("id") int idArticle, Model model, Principal principal) {
		try {
			Utilisateur utilisateur = utilisateurService.getByPseudo(principal.getName());
			ArticleAVendre article = this.articleAVendreService.getById(idArticle);
			Enchere enchere = this.articleAVendreService.getEnchereByIdArticle(idArticle);
			injecterDonneesEnchere(model, utilisateur, article, enchere);

			boolean isAcquereur = enchere.getAcquereur() != null;
			boolean isAcquereurConnecte = isAcquereur && enchere.getAcquereur().getPseudo().equals(utilisateur.getPseudo());
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

			return "view-detail-vente";

		} catch (BusinessException e) {
			return afficherVueErreur(e, model);
		}
	}

	/**
	 * Soumet une offre d'enchère pour un article spécifique.
	 * 
	 * @param enchereSoumise contenant les détails de l'offre soumise.
	 * @param bindingResult  pour la gestion des erreurs de validation.
	 * @param principal      représentant l'utilisateur actuellement connecté.
	 * @param model          permettant d'ajouter des attributs à la vue.
	 * @return Le nom de la vue à afficher ou une redirection vers la vue de détails de l'enchère.
	 */
	@PostMapping("/detail")
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
				model.addAttribute("showNomArticle", true);
				return "view-detail-vente";
			}

			try {
				// Soumission de l'offre pour l'enchère
				articleAVendreService.faireUneOffre(enchereSoumise, utilisateur);
			} catch (BusinessException e) {
				handleBusinessException(e, bindingResult, model, utilisateur, idArticle);
				model.addAttribute("showNomArticle", true);
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
	@GetMapping("/retrait")
	public String retraitEnchere(@RequestParam("id") int idArticle, Principal principal, Model model) {

		try {
			// Récupération de l'article s'il existe
			ArticleAVendre article = articleAVendreService.getById(idArticle);
			// Vérifie si l'utilisateur actuellement connecté est le vendeur de l'article
			if (article.getVendeur().getPseudo().equals(principal.getName())) {
				this.articleAVendreService.effectuerRetrait(article, principal.getName());
				return "redirect:/encheres/detail?id=" + idArticle;
			} else {
				model.addAttribute("retraitError",
						"Attention : toute tentative de hacking est enregistrée et signalée aux autorités compétentes.");
				return "view-errors";
			}
		} catch (BusinessException e) {
			return afficherVueErreur(e, model);
		}

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
