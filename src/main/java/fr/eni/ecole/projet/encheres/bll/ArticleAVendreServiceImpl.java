package fr.eni.ecole.projet.encheres.bll;

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

	@Override
	public List<ArticleAVendre> getArticlesAVendreEnCours() {
		// Remonter la liste des article a vendre en cours depuis la DAL
		List<ArticleAVendre> articlesAVendreEnCours = articleAVendreDAO.findAllStatutEnCours();

		return articlesAVendreEnCours;
	}
	

	@Override
	public List<ArticleAVendre> getArticlesAVendreAvecParamètres(String nomRecherche, int categorieRecherche) {
		List<ArticleAVendre> articlesAVendreAvecParametres = articleAVendreDAO.findAllWithParameters(nomRecherche, categorieRecherche);

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
	public List<Categorie> getAllCategories() {
		return categorieDAO.findAll();
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
	
}
