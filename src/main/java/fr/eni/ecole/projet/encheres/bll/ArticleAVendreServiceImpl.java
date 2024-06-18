package fr.eni.ecole.projet.encheres.bll;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Categorie;
import fr.eni.ecole.projet.encheres.bo.Enchere;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;
import fr.eni.ecole.projet.encheres.dal.AdresseDAO;
import fr.eni.ecole.projet.encheres.dal.ArticleAVendreDAO;
import fr.eni.ecole.projet.encheres.dal.CategorieDAO;
import fr.eni.ecole.projet.encheres.dal.EnchereDAO;
import fr.eni.ecole.projet.encheres.dal.UtilisateurDAO;
import fr.eni.ecole.projet.encheres.exceptions.BusinessCode;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;

@Service
public class ArticleAVendreServiceImpl implements ArticleAVendreService {

	private ArticleAVendreDAO articleAVendreDAO;
	private AdresseDAO adresseDAO;
	private UtilisateurDAO utilisateurDAO;
	private CategorieDAO categorieDAO;
	private EnchereDAO enchereDAO;


public ArticleAVendreServiceImpl(ArticleAVendreDAO articleAVendreDAO, AdresseDAO adresseDAO,
		UtilisateurDAO utilisateurDAO, CategorieDAO categorieDAO, EnchereDAO enchereDAO) {
	this.articleAVendreDAO = articleAVendreDAO;
	this.adresseDAO = adresseDAO;
	this.utilisateurDAO = utilisateurDAO;
	this.categorieDAO = categorieDAO;
	this.enchereDAO = enchereDAO;
}
	
	@Override
	public void mettreArticleEnVente(ArticleAVendre articleAVendre, Utilisateur utilisateur) {
			BusinessException be = new BusinessException();
			articleAVendre.setVendeur(utilisateur);
			 if (validationArticle(articleAVendre, be)) {
			        try {
			            articleAVendreDAO.addArticle(articleAVendre);
			        } catch (DataAccessException e) {
			            be.printStackTrace();
			            //TODO Créer businessException
			        }
			    } else {
			        throw be;
			    }
			
		}
	
	@Override
	public void modifierArticleEnVente(ArticleAVendre articleAVendre) {
		// test ID vérif si article existe
		BusinessException be = new BusinessException();
		
		if (validationArticle(articleAVendre, be)) {
	            articleAVendreDAO.updateArticle(articleAVendre);
	    } else {
	        throw be;
	    }
	}
	
	// Mutualisation des méthodes mettreArticleEnVente et modifierArticleEnVente
	
	private boolean validationArticle(ArticleAVendre articleAVendre, BusinessException be) {
		boolean isValid = true;
		
		isValid &= validerArticleAVendre(articleAVendre, be);
	    isValid &= validerNom(articleAVendre.getNom(), be);
	    isValid &= validerDescription(articleAVendre.getDescription(), be);
	    isValid &= validerDateDebutEncheres(articleAVendre.getDateDebutEncheres(), be);
	    isValid &= validerDateFinEncheres(articleAVendre.getDateFinEncheres(), articleAVendre.getDateDebutEncheres(), be);
	    isValid &= validerPrixInitial(articleAVendre.getPrixInitial(), be);
	    isValid &= validerCategorie(articleAVendre.getCategorie(), be);
	    isValid &= validerAdresseRetrait(articleAVendre.getAdresseRetrait(), be);
	    
	    
	    return isValid;
	    }

	// Validation pour mettre un article en vente

	private boolean validerArticleAVendre(ArticleAVendre articleAVendre, BusinessException be) {
		if (articleAVendre == null) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_NULL);
			return false;
		}
		return true;
	}

	private boolean validerNom(String nom, BusinessException be) {
		if (nom == null || nom.isBlank()) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_NOM_NULL);
			return false;
		}

		if (nom.length() < 5 || nom.length() > 30) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_NOM_LENGTH);
			return false;
		}

		return true;
	}

	private boolean validerDescription(String description, BusinessException be) {
		if (description == null || description.isBlank()) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DESCRIPTION_BLANK);
			return false;
		}
		if (description.length() < 20 || description.length() > 300) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DESCRIPTION_LENGTH);
			return false;
		}
		return true;
	}

	@Override
	public List<ArticleAVendre> getArticlesAVendreEnCours() {
		// Remonter la liste des article a vendre en cours depuis la DAL
		List<ArticleAVendre> articlesAVendreEnCours = articleAVendreDAO.findAllStatutEnCours();

		return articlesAVendreEnCours;
	}
	

	@Override
	public List<ArticleAVendre> getArticlesAVendreAvecParamètres(String nomRecherche, int categorieRecherche, int casUtilisationFiltres, Principal principal) {
		int statutRecherche = 1;
		String pseudoUtilisateurEnSession = null;
		if(principal != null) {
		switch (casUtilisationFiltres) {
		case 1, 3:
			pseudoUtilisateurEnSession = principal.getName();
			break;
		case 4:
			statutRecherche = 0;
			pseudoUtilisateurEnSession = principal.getName();
			break;
		case 2, 5:
			statutRecherche = 2;
			pseudoUtilisateurEnSession = principal.getName();
			break;
			
		}}
		
		List<ArticleAVendre> articlesAVendreAvecParametres = articleAVendreDAO.findAllWithParameters(nomRecherche, categorieRecherche,statutRecherche,casUtilisationFiltres,pseudoUtilisateurEnSession);

		return articlesAVendreAvecParametres;
	}

	@Override
	public ArticleAVendre getById(int idArticle) {
		BusinessException be = new BusinessException();
		try {
			ArticleAVendre article = this.articleAVendreDAO.getByID(idArticle);

			Adresse adresse = this.adresseDAO.getByID(article.getAdresseRetrait().getId());
			article.setAdresseRetrait(adresse);

			Categorie categorie = this.categorieDAO.read(article.getCategorie().getId());
			article.setCategorie(categorie);

			return article;
		} catch (DataAccessException e) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_NULL);
			throw be;
		}

	}

	@Transactional
	@Override
	public void faireUneOffre(Enchere enchere, Utilisateur utilisateur) {
		BusinessException be = new BusinessException();
		boolean isValid = true;
		ArticleAVendre article = this.articleAVendreDAO.getByID(enchere.getArticleAVendre().getId());
		LocalDate debut = article.getDateDebutEncheres();
		LocalDate fin = article.getDateFinEncheres();
		int prixInitial = article.getPrixInitial();
		int prixVente = article.getPrixVente();
		int credit = utilisateur.getCredit();
		int montant = enchere.getMontant();
		enchere.setAcquereur(utilisateur);

		isValid &= verifierDatesEnchere(debut, fin, be);
		isValid &= verifierMontant(montant, prixInitial, prixVente, be);
		isValid &= verifierCreditSuffisant(montant, credit, be);

		if (isValid) {

			// Récréditer le compte du dernier enchérisseur s'il existe
			// Compter le nombre d'offre existant pour l'article
			int count = this.enchereDAO.getTotalOffre(enchere.getArticleAVendre().getId());

			if (count > 0) {
				Enchere derniereEnchere = this.enchereDAO.getDerniereEnchere(enchere.getArticleAVendre().getId());
				Utilisateur dernierEnrichisseur = utilisateurDAO.getByPseudo(
						derniereEnchere.getAcquereur().getPseudo());
				dernierEnrichisseur.setCredit(derniereEnchere.getMontant() + dernierEnrichisseur.getCredit());
				this.utilisateurDAO.updateCredit(dernierEnrichisseur.getPseudo(), dernierEnrichisseur.getCredit());
			}

			// Debiter le nouveau acquéreur
			utilisateur.setCredit(credit - montant);
			this.utilisateurDAO.updateCredit(utilisateur.getPseudo(), utilisateur.getCredit());

			// Mettre à jour le prix de vente de l'article
			this.articleAVendreDAO.updatePrixVente(enchere.getArticleAVendre().getId(), montant);
			enchere.getArticleAVendre().setPrixVente(montant);

			// Ajouter l'enchère
			int nbrEnchere = enchereDAO.addEnchere(enchere);
			if (nbrEnchere == 0) {
				be.add(BusinessCode.VALIDATION_OFFRE_AJOUT_ENCHERE);
				throw be;
			}
		} else {
			throw be;
		}
	}

	private boolean verifierCreditSuffisant(int montant, int credit, BusinessException be) {
		if (credit < montant) {
			be.add(BusinessCode.VALIDATION_OFFRE_CREDIT);
			return false;
		}
		return true;
	}

	private boolean validerDateDebutEncheres(LocalDate dateDebutEncheres, BusinessException be) {
		LocalDate today = LocalDate.now();
		if (dateDebutEncheres == null) {
	            be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DATE_DEBUT_NULL);
	            return false;
	        }
	        
	        if (dateDebutEncheres.isBefore(LocalDate.now()) || dateDebutEncheres.equals(today)) {
	            be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DATE_DEBUT_PASSE);
	            return false;
	        }
		return true;
	}

	private boolean validerDateFinEncheres(LocalDate dateFinEncheres, LocalDate dateDebutEncheres, BusinessException be) {
		 if (dateFinEncheres == null) {
	            be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DATE_FIN_NULL);
	            return false;
	        }
	        
	        if (dateFinEncheres.isBefore(LocalDate.now())) {
	            be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DATE_FIN_PASSE);
	            return false;
	        }
	        
	        if (dateFinEncheres.isBefore(dateDebutEncheres) || dateFinEncheres.equals(dateDebutEncheres)) {
	        	be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DATE_FIN_AVANT_DEBUT);
	            return false;
	        }
		return true;
	}

	private boolean validerPrixInitial(int prixInitial, BusinessException be) {
		if (prixInitial < 1) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_PRIX_INITIAL);
			return false;
		}
		return true;
	}

	private boolean validerAdresseRetrait(Adresse adresseRetrait, BusinessException be) {

		if (adresseRetrait == null) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_ADRESSE_NULL);
			return false;
		}
		if (adresseRetrait.getId() <= 0) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_ADRESSE_INCONNU);
			return false;
		}
		Adresse adresseEnBase = this.adresseDAO.getByID(adresseRetrait.getId());
		if (adresseEnBase == null) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_ADRESSE_INCONNU);
			return false;
		}
		return true;
	}

	private boolean validerCategorie(Categorie categorie, BusinessException be) {
		if (categorie == null) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_CATEGORIE_NULL);
			return false;
		}
		if (categorie.getId() <= 0) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_CATEGORIE_INCONNU);
			return false;
		}
		Categorie categorieEnBase = this.categorieDAO.read(categorie.getId());
		if (categorieEnBase == null) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_CATEGORIE_INCONNU);
			return false;
		}
		return true;
	}

	@Override
	public Categorie getCategorieById(long id) {
		return categorieDAO.read(id);
	}
	
	public List<Categorie> getAllCategories() {
		return categorieDAO.findAll();
	}

	
	public Adresse getAdresseById(long id) {
		return adresseDAO.getByID(id);
	}
	
	public List<Adresse> getAllAdressesRetrait() {
		return adresseDAO.findAll();
	}

	// Méthodes pour enchères
	private boolean verifierMontant(int montant, int prixInitial, int prixVente, BusinessException be) {
		if (prixVente > prixInitial) {
			if (montant <= prixVente) {
				be.add(BusinessCode.VALIDATION_OFFRE_MONTANT);
				return false;
			}
		}
		if (prixVente < prixInitial) {
			if (montant < prixInitial) {
				be.add(BusinessCode.VALIDATION_OFFRE_MONTANT);
				return false;
			}
		}
		return true;
	}

	private boolean verifierDatesEnchere(LocalDate debut, LocalDate fin, BusinessException be) {
		LocalDate today = LocalDate.now();

		if (debut.isAfter(today)) {
			be.add(BusinessCode.VALIDATION_OFFRE_DATE_DEBUT);
			return false;
		}

		if (fin.isBefore(today)) {
			be.add(BusinessCode.VALIDATION_OFFRE_DATE_FIN);
			return false;
		}
		return true;
	}

	@Override
	public Enchere getEnchereByIdArticle(int idArticle) {
		int count = this.enchereDAO.getTotalOffre(idArticle);
		ArticleAVendre articleAVendre = this.articleAVendreDAO.getByID(idArticle);
		if (count > 0) {
			Enchere derniereEnchere = this.enchereDAO.getDerniereEnchere(idArticle);
			Utilisateur acquereur = derniereEnchere.getAcquereur();
			acquereur = utilisateurDAO.getByPseudo(acquereur.getPseudo());
			derniereEnchere.setMontant(derniereEnchere.getMontant() + 1);
			derniereEnchere.setArticleAVendre(articleAVendre);
			derniereEnchere.setAcquereur(acquereur);
			return derniereEnchere;
		}
		Enchere enchere = new Enchere();
		enchere.setMontant(articleAVendre.getPrixInitial());
		enchere.setArticleAVendre(articleAVendre);
		return enchere;
	}


	@Override
	public void annulerVente(ArticleAVendre article) {
		BusinessException be = new BusinessException();
		boolean isValid = true;
		
		// tests erreurs
		// article.setDateDebutEncheres(article.getDateDebutEncheres().minusDays(1));;
		
		
		isValid &= validerAnnulationDateDebutEnchere(article.getDateDebutEncheres(), be);
		isValid &= validerStatutVente(article.getStatut(), be);
		
		// Si l'enchère n'a pas débuté et que son statut est bien à 0
		if (isValid) {
			int count = this.articleAVendreDAO.annulerVente(article.getId());
			if (count == 0) {
				be.add(BusinessCode.VALIDATION_ANNULER_VENTE);
				throw be;
			}
		} else {
			throw be;
		}

	}

	private boolean validerStatutVente(int statut, BusinessException be) {
		if (statut > 0) {
			be.add(BusinessCode.VALIDATION_ANNULER_VENTE_STATUT);
			return false;
		}
		return true;
	}

	private boolean validerAnnulationDateDebutEnchere(LocalDate dateDebutEncheres, BusinessException be) {
		LocalDate today = LocalDate.now();
		if (dateDebutEncheres.isBefore(today) || dateDebutEncheres.equals(today)) {
			be.add(BusinessCode.VALIDATION_ANNULER_VENTE_DATE_DEBUT);
			return false;
		}
		return true;
	}


	@Override
	public void activerVente() {
		BusinessException be = new BusinessException();
		int nbr = this.articleAVendreDAO.activerVente();
		if (nbr == 0) {
			be.add(BusinessCode.VALIDATION_ACTIVER_VENTE);
			throw be;
		}
	}


	@Override
	public void cloturerVente() {
		BusinessException be = new BusinessException();
		int nbr = this.articleAVendreDAO.cloturerVente();
		if (nbr == 0) {
			be.add(BusinessCode.VALIDATION_CLOTURER_VENTE);
			throw be;
		}
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void effectuerRetrait(ArticleAVendre article, String pseudoVendeur) {
		BusinessException be = new BusinessException();
		boolean isValid = true;
		
		// verifier que le status de l'article est 2
		isValid &= verifierStatutNonLivre(article.getStatut(), be);
		
		if(isValid) {
			// Créditer vendeur
			int count = crediterVendeur(article, pseudoVendeur);
			if (count < 1) {
				be.add(BusinessCode.VALIDATION_CREDITER_VENDEUR);
				throw be;
			}
			// Mettre à jour statut de l'article à 3
			count += this.articleAVendreDAO.livrerVente(article.getId());
			if (count < 2) {
				be.add(BusinessCode.VALIDATION_LIVRER_ARTICLE);
				throw be;
			}
		}
		
	}

	private int crediterVendeur(ArticleAVendre article, String pseudoVendeur) {
		Utilisateur utilisateur = this.utilisateurDAO.getByPseudo(pseudoVendeur);
		Enchere enchere = this.enchereDAO.getDerniereEnchere(article.getId());
		int montantAcrediter = enchere.getMontant();
		int creditActuel = utilisateur.getCredit();
		utilisateur.setCredit(creditActuel + montantAcrediter);
		int count = this.utilisateurDAO.crediterVendeur(utilisateur.getPseudo(), utilisateur.getCredit()); 
		return count;
	}

	private boolean verifierStatutNonLivre(int statut, BusinessException be) {
		if (statut != 2) {
			be.add(BusinessCode.VALIDATION_STATUT_NON_LIVRE);
			return false;
		}
		return true;
	}


}
