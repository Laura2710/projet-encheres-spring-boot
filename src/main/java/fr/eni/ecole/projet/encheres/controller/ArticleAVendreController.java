package fr.eni.ecole.projet.encheres.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.multipart.MultipartFile;

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

	/**
	 * Affiche les articles à vendre en cours.
	 *
	 * @param model     Le modèle pour la vue.
	 * @param principal Les informations de l'utilisateur connecté.
	 * @return La vue index.
	 */
	@GetMapping
	public String afficherArticleAVendre(Model model, Principal principal) {

		List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreEnCours();
		model.addAttribute("articlesAVendre", articlesAVendre);
		// Ajout au model ma variable "nomRecherche" qui contiendra la chaine de
		// caractère a retrouver dans le nom des articles
		String nomRecherche = null;
		model.addAttribute("nomRecherche", nomRecherche);
		// Ajout au model de ma variable categorieRecherche qui contiendra l'id de la
		// catégorie a rechercher
		int categorieRecherche = 0;
		model.addAttribute("categorieRecherche", categorieRecherche);
		// Ajout de la condition "est connecté"
		if (principal != null) {
			// Ajout des parametres utiles aux filtres si l'utilisateurs est connecté et non
			// Admin.
			// Parametre pour les input Select
			int casUtilisationFiltres = 0;
			model.addAttribute("casUtilisationFiltres", casUtilisationFiltres);
		}
		return "index";
	}

	/**
	 * Affiche les articles à vendre en fonction des paramètres de recherche.
	 *
	 * @param nomRecherche          Le nom de l'article recherché.
	 * @param categorieRecherche    L'identifiant de la catégorie recherchée.
	 * @param casUtilisationFiltres Les filtres d'utilisation.
	 * @param model                 Le modèle pour la vue.
	 * @param principal             Les informations de l'utilisateur connecté.
	 * @return La vue index.
	 */
	@PostMapping("/rechercher")
	public String afficherArticleAVendre(@RequestParam(value = "nomRecherche") String nomRecherche,
			@RequestParam(value = "categorieRecherche") int categorieRecherche,
			@RequestParam(value = "casUtilisationFiltres") int casUtilisationFiltres, Model model,
			Principal principal) {
		List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreAvecParamètres(nomRecherche,
				categorieRecherche, casUtilisationFiltres, principal);
		model.addAttribute("articlesAVendre", articlesAVendre);
		model.addAttribute("nomRecherche", nomRecherche);
		model.addAttribute("categorieRecherche", categorieRecherche);

		if (principal != null) {
			// Ajout des parametres utiles aux filtres si l'utilisateurs est connecté et non
			// Admin.
			// Parametre pour les input select
			model.addAttribute("casUtilisationFiltres", casUtilisationFiltres);
		}
		return "index";

	}

	/**
	 * Affiche le formulaire pour mettre en vente un nouvel article.
	 *
	 * @param model     Le modèle pour la vue.
	 * @param principal Les informations de l'utilisateur connecté.
	 * @return La vue du formulaire de vente d'article.
	 */
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

	/**
	 * Traite la mise en vente d'un nouvel article.
	 *
	 * @param articleAVendre L'article à vendre.
	 * @param bindingResult  Les résultats de la validation.
	 * @param principal      Les informations de l'utilisateur connecté.
	 * @param model          Le modèle pour la vue.
	 * @return La vue à afficher.
	 */
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

	/**
	 * Injecte les catégories dans le modèle.
	 *
	 * @return La liste des catégories.
	 */
	@ModelAttribute("categories")
	public List<Categorie> injecteCategorie() {
		List<Categorie> categories = this.articleAVendreService.getAllCategories();
		return categories;
	}

	/**
	 * Injecte les adresses de retrait dans le modèle.
	 *
	 * @param principal Les informations de l'utilisateur connecté.
	 * @return La liste des adresses de retrait.
	 */
	@ModelAttribute("adressesRetrait")
	public List<Adresse> injecteAdresse(Principal principal) {
		List<Adresse> adressesRetrait = new ArrayList<Adresse>();
		if (principal != null) {
			Utilisateur utilisateurSession = this.utilisateurService.getByPseudo(principal.getName());
			Adresse adressePerso = this.articleAVendreService.getAdresseById(utilisateurSession.getAdresse().getId());
			adressesRetrait.add(adressePerso);
			this.articleAVendreService.getAllAdressesRetrait().forEach(a -> {
				adressesRetrait.add(a);
			});
			;
		}
		return adressesRetrait;
	}

	/**
	 * Affiche le formulaire de modification d'un article à vendre.
	 *
	 * @param idArticle L'identifiant de l'article à modifier.
	 * @param model     Le modèle pour la vue.
	 * @param principal Les informations de l'utilisateur connecté.
	 * @return La vue du formulaire de modification de vente d'article.
	 */
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

	/**
	 * Traite la modification d'un article à vendre.
	 *
	 * @param articleAVendre L'article à vendre.
	 * @param bindingResult  Les résultats de la validation.
	 * @param principal      Les informations de l'utilisateur connecté.
	 * @param model          Le modèle pour la vue.
	 * @return La vue à afficher.
	 */
	@PostMapping("/vendre/modifier")
	public String modifierArticle(@Valid @ModelAttribute("articleAVendre") ArticleAVendre articleAVendre,
			BindingResult bindingResult, Principal principal, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("articleAVendre", articleAVendre);
			model.addAttribute("modeModif", true);
			model.addAttribute("action", "/vendre/modifier");
			return "view-vente-article";
		}
		try {
			if (articleAVendre.getStatut() == 0) {
				if (!bindingResult.hasErrors()) {
					try {
						articleAVendreService.modifierArticleEnVente(articleAVendre, principal.getName());
						// return "redirect:/";
						return "view-ajouter-image";
					} catch (BusinessException be) {
						be.getClefsExternalisations().forEach(key -> {
							ObjectError error = new ObjectError("globalError", key);
							bindingResult.addError(error);
						});
						model.addAttribute("articleAVendre", articleAVendre);
						model.addAttribute("modeModif", true);
						model.addAttribute("action", "/vendre/modifier");
						return "view-vente-article";
					}
				}
			}
		} catch (BusinessException be) {
			be.getClefsExternalisations().forEach(key -> {
				ObjectError error = new ObjectError("globalError", key);
				bindingResult.addError(error);
			});
			return "redirect:/";
		}
		return "redirect:/";
	}

	/**
	 * Annule la vente d'un article.
	 *
	 * @param idArticle L'identifiant de l'article à annuler.
	 * @param principal Les informations de l'utilisateur connecté.
	 * @param model     Le modèle pour la vue.
	 * @return La vue à afficher.
	 */
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

	@GetMapping("/ajouter-photo")
	public String ajouterPhoto() {
		return "view-ajouter-image";
	}

	// TODO Méthode Post NON TERMINEE

	@PostMapping("/ajouter-photo")
	public String ajouterPhoto(@RequestParam("inputPhoto") MultipartFile file, @RequestParam("idArticle") int idArticle, Principal principal, Model model) throws IOException {
	    String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images/uploads";

	    if (idArticle > 0) {   	
	    	// Vérifiez si le répertoire de téléchargement existe, sinon le créer
	    	Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
	    	if (!Files.exists(uploadPath)) {
	    		Files.createDirectories(uploadPath);
	    	}
	    	
	    	// Vérifiez si le fichier n'est pas vide et que sa taille est inférieure à 600000 octets (600 Ko)
	    	if (!file.isEmpty() && file.getSize() < 600000) {
	    		// Vérifiez si le fichier est de type PNG ou JPEG
	    		if (file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")) {
	    			String originalFilename = file.getOriginalFilename();
	    			if (originalFilename != null) {
	    				
	    				// Générer un nouveau nom de fichier unique (avec un horodatage)
	    				String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
	    				String newFilename = System.currentTimeMillis() + fileExtension;
	    				
	    				// Construire le chemin complet pour le fichier à enregistrer
	    				Path fileNameAndPath = uploadPath.resolve(newFilename);
	    				System.out.println(newFilename);
	    				Files.write(fileNameAndPath, file.getBytes());
	    				
	    				articleAVendreService.ajouterPhotoArticle(idArticle, newFilename, principal.getName());
	    				ArticleAVendre article = articleAVendreService.getById(idArticle);
	    				model.addAttribute("articleAVendre", article);
	    			}
	    		}
	    	}
	    }
	    return "view-ajouter-image";
	}


}
