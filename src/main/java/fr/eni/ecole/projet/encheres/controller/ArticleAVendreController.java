package fr.eni.ecole.projet.encheres.controller;

import java.security.Principal;
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
		return "index";
	}
	
	// Penser à refactoriser
	
	@GetMapping("/vendre")
	public String vendreArticle(Model model, Principal principal) {
		try {
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
		} catch (Exception e) {
			model.addAttribute("error", "Impossible d'enregister la vente");
			return "view-vente-article";
		}
	}


	
	@PostMapping("/vendre")
	public String vendreArticle(@Valid @ModelAttribute("articleAVendre") ArticleAVendre articleAVendre, BindingResult bindingResult, Principal principal, Model model) {
		String pseudo = principal.getName();
		Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(pseudo);
		List<Categorie> categories = this.articleAVendreService.getAllCategories();
		List<Adresse> adressesRetrait = this.articleAVendreService.getAllAdressesRetrait();
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

	    model.addAttribute("categories", categories);
	    model.addAttribute("adressesRetrait", adressesRetrait);
	    return "view-vente-article";
	}
	
	@GetMapping("/vendre/modifier")
	public String modifierArticle(@RequestParam("id") int idArticle, Model model, Principal principal) {
		try {
			ArticleAVendre article = this.articleAVendreService.getById(idArticle);
			if((article.getStatut() == 0) && principal.getName().equals(article.getVendeur().getPseudo())) {
			
			List<Categorie> categories = this.articleAVendreService.getAllCategories();
			List<Adresse> adressesRetrait = this.articleAVendreService.getAllAdressesRetrait();
			
				model.addAttribute("articleAVendre", new ArticleAVendre());
				model.addAttribute("categories", categories);
				model.addAttribute("adressesRetrait", adressesRetrait);
				return "view-vente-article";
			} else {
				return "redirect:/index";
			}
		} catch (Exception e) {
			model.addAttribute("error", "Impossible de modifier la vente");
			return "view-vente-article";		
		}
	}
	
	@PostMapping("/vendre/modifier")
	public String modifierArticle(@Valid @ModelAttribute("articleAVendre") ArticleAVendre articleAVendre, BindingResult bindingResult, @RequestParam("id") int idArticle, Principal principal) {
		ArticleAVendre article = this.articleAVendreService.getById(idArticle);
		if (article.getStatut() == 0 && principal.getName().equals(article.getVendeur().getPseudo())) {
			if (!bindingResult.hasErrors()) {
				try {
					articleAVendreService.modifierArticleEnVente(articleAVendre);
					return "redirect:/index";
				} catch (BusinessException be) {
					be.getClefsExternalisations().forEach(key-> {
						ObjectError error = new ObjectError("globalError", key);
						bindingResult.addError(error);
					});
				}
			}
		} else {
			ObjectError error = new ObjectError("globalError", BusinessCode.VALIDATION_UTILISATEUR_NON_CREATEUR_VENTE);
			bindingResult.addError(error);
			return "redirect:/index";
		}
		
		return "index";
	}
	
	@GetMapping("/encheres/detail")
	public String voirDetailEnchere(@RequestParam("id") int idArticle, Model model, Principal principal) {
		try {
			ArticleAVendre article = this.articleAVendreService.getById(idArticle);			
			model.addAttribute("article", article);
			
			// En tant qu’utilisateur connecté, je peux afficher le détail d’une enchère en cours. 
			if (article.getStatut() == 1) {
				Enchere enchere = this.articleAVendreService.getEnchereByIdArticle(idArticle);
				enchere.setMontant(article.getPrixVente() + 1);
				Utilisateur utilisateur = utilisateurService.getByPseudo(principal.getName());
				model.addAttribute("enchere", enchere);
				model.addAttribute("utilisateur", utilisateur);	
				return "view-detail-vente";							
			}
			// Le vendeur peut modifier la vente (si c’est son article et que l’enchère n’a pas débuté)
			if (article.getStatut() == 0 && principal.getName().equals(article.getVendeur().getPseudo())) {
				// TODO rediriger vers le formulaire
			}
			return "redirect:/";
		}
		catch (Exception e) {
			model.addAttribute("error", "Aucune vente ne correspond à votre recherche");
			return "view-detail-vente";		
		}
	}

	@PostMapping("/encheres/detail")
	 public String soumettreOffreEnchere(@Valid @ModelAttribute("enchere") Enchere enchere, BindingResult bindingResult, Principal principal, Model model) {
		int idArticle = (int) enchere.getArticleAVendre().getId();
		ArticleAVendre article = this.articleAVendreService.getById(idArticle);	
		model.addAttribute("article", article);
		model.addAttribute("enchere", enchere);
  	  	Utilisateur utilisateur = this.utilisateurService.getByPseudo(principal.getName());
		model.addAttribute("utilisateur", utilisateur);	

      if (!bindingResult.hasErrors()) {
    	  
    	  if (!article.getVendeur().getPseudo().equals(principal.getName())) {
    		  if (enchere.getArticleAVendre().getId() > 0) {
    			  try {
    				  enchere.setAcquereur(utilisateur);
    				  enchere.setArticleAVendre(article);
    				  this.articleAVendreService.faireUneOffre(enchere);
    			  }
    			  catch(BusinessException e) {    				  
    				  e.getClefsExternalisations().forEach(key -> {
    					  ObjectError error = new ObjectError("globalError", key);
    					  bindingResult.addError(error);
    				  });
    				  return "view-detail-vente";		
    				  
    			  }
    		  }
    		  
    	  }
    	  return "redirect:/encheres/detail?id="+enchere.getArticleAVendre().getId();		    	  
      }
      else {
    	  enchere.setAcquereur(new Utilisateur());
    	  return "view-detail-vente";	
      }
    }
}
