package fr.eni.ecole.projet.encheres.bll;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Categorie;
import fr.eni.ecole.projet.encheres.dal.AdresseDAO;
import fr.eni.ecole.projet.encheres.dal.ArticleAVendreDAO;
import fr.eni.ecole.projet.encheres.dal.CategorieDAO;
import fr.eni.ecole.projet.encheres.exceptions.BusinessCode;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;

@Service
public class ArticleAVendreServiceImpl implements ArticleAVendreService {

	AdresseDAO adresseDao;
	CategorieDAO categorieDAO;
	ArticleAVendreDAO articleAVendreDAO;
	
	
	public ArticleAVendreServiceImpl(AdresseDAO adresseDao) {
		this.adresseDao = adresseDao;
	}

	// A rajouter dans les validations : point de retrait et categorie//
	
	@Override
	public void mettreArticleEnVente(ArticleAVendre articleAVendre) {
			BusinessException be = new BusinessException();
			boolean isValid = true;
			
			isValid &= validerArticleAVendre(articleAVendre, be);
			isValid &= validerNom(articleAVendre.getNom(), be);
			isValid &= validerDescription(articleAVendre.getDescription(), be);
			isValid &= validerDateDebutEncheres(articleAVendre.getDateDebutEncheres(), be);
			isValid &= validerDateFinEncheres(articleAVendre.getDateFinEncheres(), be);
			isValid &= validerDateFinEncheres(articleAVendre.getDateFinEncheres(), be);
			isValid &= validerPrixInitial(articleAVendre.getPrixInitial(), be);
			isValid &= validerCategorie(articleAVendre.getCategorie(), be);
			isValid &= validerAdresseRetrait(articleAVendre.getAdresseRetrait(), be);
			
			if (isValid) {
				articleAVendreDAO.addArticle(articleAVendre);
			} else {
				throw be;
			}
		}

	// Validation pour mettre un article en vente

	private boolean validerArticleAVendre(ArticleAVendre articleAVendre, BusinessException be) {
		if (articleAVendre== null) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_NULL);
			return false;
		}
		return true;
	}
	
	private boolean validerNom(String nom, BusinessException be) {
		if (nom== null || nom.isBlank()) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_NOM_NULL);
			return false;
		}
		
		if (nom.length() < 5 || nom.length() > 30) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DESCRIPTION_LENGTH);
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
	
	private boolean validerDateDebutEncheres(LocalDate dateDebutEncheres, BusinessException be) {
		 if (dateDebutEncheres == null) {
	            be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DATE_DEBUT_NULL);
	            return false;
	        }
	        
	        if (dateDebutEncheres.isBefore(LocalDate.now())) {
	            be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DATE_DEBUT_PASSE);
	            return false;
	        }
		return true;
	}
	

	private boolean validerDateFinEncheres(LocalDate dateFinEncheres, BusinessException be) {
		 if (dateFinEncheres == null) {
	            be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DATE_FIN_NULL);
	            return false;
	        }
	        
	        if (dateFinEncheres.isBefore(LocalDate.now())) {
	            be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_DATE_FIN_PASSE);
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
		Adresse adresseEnBase = this.adresseDao.getByID(adresseRetrait.getId());
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
}
