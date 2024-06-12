package fr.eni.ecole.projet.encheres.bll;

import java.time.LocalDate;

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

	ArticleAVendreDAO articleAVendreDAO;
	AdresseDAO adresseDAO;
	CategorieDAO categorieDAO;
	EnchereDAO enchereDAO;
	UtilisateurDAO utilisateurDAO;
	

	public ArticleAVendreServiceImpl(ArticleAVendreDAO articleAVendreDAO, AdresseDAO adresseDAO,
			CategorieDAO categorieDAO, EnchereDAO enchereDAO, UtilisateurDAO utilisateurDAO) {
		this.articleAVendreDAO = articleAVendreDAO;
		this.adresseDAO = adresseDAO;
		this.categorieDAO = categorieDAO;
		this.enchereDAO = enchereDAO;
		this.utilisateurDAO = utilisateurDAO;
	}


	@Override
	public ArticleAVendre getById(int idArticle) {
		ArticleAVendre article = this.articleAVendreDAO.getByID(idArticle);
		
		Adresse adresse = this.adresseDAO.getByID(article.getAdresseRetrait().getId());
		article.setAdresseRetrait(adresse);
		
		Categorie categorie = this.categorieDAO.read(article.getCategorie().getId());
		article.setCategorie(categorie);
		
		return article;
	
	}

	

	@Transactional
	@Override
	public void faireUneOffre(Enchere enchere) {
		BusinessException be = new BusinessException();
		boolean isValid = true;
		System.out.println(enchere);
		LocalDate debut = enchere.getArticleAVendre().getDateDebutEncheres();
		LocalDate fin = enchere.getArticleAVendre().getDateFinEncheres();
		int credit = enchere.getAcquereur().getCredit();
		int prixInitial = enchere.getArticleAVendre().getPrixInitial();
		int prixVente = enchere.getArticleAVendre().getPrixVente();
		int montant = enchere.getMontant();
		
		isValid &= verifierDatesEnchere(debut, fin, be);
		isValid &= verifierMontant(montant, prixInitial, prixVente, be);
		isValid &= verifierCreditSuffisant(montant, credit, be);
		
		if (isValid) {	
			// 1) trouver le dernier enchérisseur s'il existe et récréditer son compte
			Enchere derniereEnchere = this.enchereDAO.getDerniereEnchere(enchere.getArticleAVendre().getId());
			if (derniereEnchere != null) {
				int montantArecrediter = derniereEnchere.getMontant();
				Utilisateur dernierEnrichisseur = utilisateurDAO.getByPseudo(derniereEnchere.getAcquereur().getPseudo());
				dernierEnrichisseur.setCredit(montantArecrediter + dernierEnrichisseur.getCredit());
				this.utilisateurDAO.updateCredit(dernierEnrichisseur.getPseudo(), dernierEnrichisseur.getCredit());
			}
			
			// 2) Debiter le nouveau acquéreur 
			enchere.getAcquereur().setCredit(credit - montant);
			this.utilisateurDAO.updateCredit(enchere.getAcquereur().getPseudo(), enchere.getAcquereur().getCredit());
			
			// 3) Modifier le prix de vente
			this.articleAVendreDAO.updatePrixVente(enchere.getArticleAVendre().getId(), montant);
			enchere.getArticleAVendre().setPrixVente(montant);
			
			// 4) Enregistrer l'enchere
			int nbrEnchere = enchereDAO.addEnchere(enchere);
			if (nbrEnchere == 0) {
				be.add(BusinessCode.VALIDATION_OFFRE_AJOUT_ENCHERE);
				throw be;
			}

		}
		else {
			throw be;
		}
		
	}



	private boolean verifierCreditSuffisant(int montant, int credit, BusinessException be) {
		if (credit < montant || credit - montant < 0 ) {
			be.add(BusinessCode.VALIDATION_OFFRE_CREDIT);
			return false;
		}
		return true;
	}



	private boolean verifierMontant(int montant, int prixInitial, int prixVente, BusinessException be) {
		if (prixVente > prixInitial) {
			if (montant < prixVente) {
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
		if(debut.isAfter(today)) {
        	be.add(BusinessCode.VALIDATION_OFFRE_DATE_DEBUT);
        	return false;
		}
		if(fin.isBefore(today)) {
			be.add(BusinessCode.VALIDATION_OFFRE_DATE_FIN);
			return false;
		}
		return true;
	}


	@Override
	public Enchere getEnchereByIdArticle(int idArticle) {
		Enchere derniereEnchere = this.enchereDAO.getDerniereEnchere(idArticle);		
		if (derniereEnchere != null) {
			derniereEnchere.setAcquereur(utilisateurDAO.getByPseudo(derniereEnchere.getAcquereur().getPseudo()));
		}
		else {
			derniereEnchere = new Enchere();
			derniereEnchere.setAcquereur(new Utilisateur());
			
		}
		derniereEnchere.setArticleAVendre(articleAVendreDAO.getByID(idArticle));
		return derniereEnchere;
	}

}
