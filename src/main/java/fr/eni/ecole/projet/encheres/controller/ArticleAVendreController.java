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
		model.addAttribute("listCategorie",listCategorie);
		//Ajout au model ma variable "nomRecherché" qui contiendra la chaine de caractère a retrouver dans le nom des articles
		String nomRecherche = null;
		model.addAttribute("nomRecherche", nomRecherche);
		//Ajout au model de ma variable categorieRecherché qui contiendra l'id de la catégorie a rechercher
		int categorieRecherche = 0 ;
		model.addAttribute("categorieRecherche", categorieRecherche);
		return "index";
	}
	
	@PostMapping("/rechercher")
	public String afficherArticleAVendre(@RequestParam(value = "nomRecherche") String nomRecherche,@RequestParam(value = "categorieRecherche") int categorieRecherche,Model model) {
		List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreAvecParamètres(nomRecherche, categorieRecherche);
		model.addAttribute("articlesAVendre", articlesAVendre);
		List<Categorie> listCategorie = articleAVendreService.getAllCategories();
		model.addAttribute("listCategorie",listCategorie);
		model.addAttribute("nomRecherche", nomRecherche);
		model.addAttribute("categorieRecherche", categorieRecherche);
		return "index";
		
	}


	
	@GetMapping("/vendre")
	public String vendreArticle(Model model, Principal principal) {
		String pseudo = principal.getName();
		Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);

		List<Categorie> categories = this.articleAVendreService.getAllCategories();
		List<Adresse> adressesRetrait = this.articleAVendreService.getAllAdressesRetrait();
		if(utilisateurSession != null && !utilisateurSession.isAdministrateur()) {
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

	@GetMapping("/encheres/detail")
	public String voirDetailEnchere(@RequestParam("id") int idArticle, Model model, Principal principal) {
	
		try {
			ArticleAVendre article = this.articleAVendreService.getById(idArticle);
			Utilisateur utilisateur = utilisateurService.getByPseudo(principal.getName());
			String vendeur = article.getVendeur().getPseudo();
			boolean leVendeurEstConnecte = vendeur.equals(principal.getName());
			
			if (article.getStatut() == 1) {
				// Récupération des détails de l'enchère associée à cet article
				Enchere enchere = this.articleAVendreService.getEnchereByIdArticle(idArticle);		
				enchere.setArticleAVendre(article);
				ajouterDonneesEnchere(model, utilisateur, article, enchere);
				model.addAttribute("leVendeurEstConnecte", leVendeurEstConnecte);;
				model.addAttribute("enchereForm", enchere);
				return "view-detail-vente";
			}
			// Le vendeur peut modifier la vente (si c’est son article et que l’enchère n’a pas débuté)
			if (article.getStatut() == 0 && leVendeurEstConnecte) {
				// TODO
				return "redirect:/";		
				
			}
			
		}
		 catch (BusinessException e) {
			 List<String> errors = new ArrayList<String>();
			e.getClefsExternalisations().forEach(key -> {
				errors.add(key);
			});
			model.addAttribute("errorBLL", errors);
			return "view-detail-vente";
		}
		return "redirect:/";	
	}



	@PostMapping("/encheres/detail")
	public String soumettreOffreEnchere(@Valid @ModelAttribute("enchereForm") Enchere enchereSoumise,
			BindingResult bindingResult, Principal principal, Model model) {
	    
	    String pseudoUtilisateur = principal.getName();
	    Utilisateur utilisateur = utilisateurService.getByPseudo(pseudoUtilisateur);
	    
	    int idArticle = (int) enchereSoumise.getArticleAVendre().getId();
	    ArticleAVendre article = articleAVendreService.getById(idArticle);
	    
	    Enchere enchere = articleAVendreService.getEnchereByIdArticle(idArticle);

	    if (bindingResult.hasErrors()) {
	        ajouterDonneesEnchere(model, utilisateur, article, enchere);
	        return "view-detail-vente";
	    }

	    if (!article.getVendeur().getPseudo().equals(pseudoUtilisateur)) {
	        try {
	            enchereSoumise.setArticleAVendre(article);
	            articleAVendreService.faireUneOffre(enchereSoumise, utilisateur);
	        } catch (BusinessException e) {
	            e.getClefsExternalisations().forEach(key -> {
	                ObjectError err = new ObjectError("globalError", key);
	                bindingResult.addError(err);
	            });
	            ajouterDonneesEnchere(model, utilisateur, article, enchere);
	            return "view-detail-vente";
	        }
	    }

	    return "redirect:/encheres/detail?id=" + enchereSoumise.getArticleAVendre().getId();
	}
	
	private void ajouterDonneesEnchere(Model model, Utilisateur utilisateur, ArticleAVendre article, Enchere enchere) {
	    model.addAttribute("utilisateur", utilisateur);
	    model.addAttribute("article", article);
	    model.addAttribute("enchere", enchere);
	}
}
