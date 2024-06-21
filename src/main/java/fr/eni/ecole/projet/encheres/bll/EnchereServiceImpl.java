package fr.eni.ecole.projet.encheres.bll;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Enchere;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;
import fr.eni.ecole.projet.encheres.dal.ArticleAVendreDAO;
import fr.eni.ecole.projet.encheres.dal.EnchereDAO;
import fr.eni.ecole.projet.encheres.dal.UtilisateurDAO;
import fr.eni.ecole.projet.encheres.exceptions.BusinessCode;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;

@Service
public class EnchereServiceImpl implements EnchereService {
	private EnchereDAO enchereDAO;
	private ArticleAVendreDAO articleAVendreDAO;
	private UtilisateurDAO utilisateurDAO;


	public EnchereServiceImpl(EnchereDAO enchereDAO, ArticleAVendreDAO articleAVendreDAO,
			UtilisateurDAO utilisateurDAO) {
		this.enchereDAO = enchereDAO;
		this.articleAVendreDAO = articleAVendreDAO;
		this.utilisateurDAO = utilisateurDAO;
	}


	/**
	 * Cette méthode récupère l'enchère associée à un article. Si des offres
	 * existent pour cet article, elle retourne la dernière enchère en mettant à
	 * jour le montant et l'article associés. Si aucune offre n'existe, elle
	 * retourne une enchère initiale avec le prix de départ de l'article.
	 *
	 * @param idArticle : L'identifiant de l'article pour lequel récupérer
	 *                  l'enchère.
	 * @return L'enchère associée à l'article. Si aucune enchère n'existe, retourne
	 *         une enchère initiale.
	 */
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
	
	
	/**
	 * Cette méthode permet à un utilisateur d'enchérir sur un article mis en vente.
	 * Elle vérifie si l'offre est valide en contrôlant les dates de l'enchère, le
	 * montant de l'offre, et si l'utilisateur dispose de crédits suffisants. Si
	 * l'offre est valide, elle met à jour le crédit de l'utilisateur, récrédite le
	 * dernier enchérisseur (si existant), débite le nouveau enchérisseur, met à
	 * jour le prix de vente de l'article et enregistre l'enchère. En cas de
	 * problème, une BusinessException est levée.
	 *
	 * @param enchere     : l'objet Enchere contenant les informations de l'offre.
	 * @param utilisateur : L'utilisateur faisant l'offre.
	 * @throws BusinessException : Si l'offre n'est pas valide ou si une erreur
	 *                           survient lors de l'ajout de l'enchère.
	 */
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
		int montant = enchere.getMontant();
		enchere.setAcquereur(utilisateur);
		int credit = utilisateur.getCredit();
		isValid &= verifierDatesEnchere(debut, fin, be);
		isValid &= verifierMontant(montant, prixInitial, prixVente, be);
		isValid &= verifierCreditSuffisant(montant, credit, be);

		if (isValid) {

			// Récréditer le compte du dernier enchérisseur s'il existe
			// Compter le nombre d'offre existant pour l'article
			boolean isSameAcquereur = false;
			int count = this.enchereDAO.getTotalOffre(enchere.getArticleAVendre().getId());

			if (count > 0) {
				Enchere derniereEnchere = this.enchereDAO.getDerniereEnchere(enchere.getArticleAVendre().getId());
				Utilisateur dernierEnrichisseur = utilisateurDAO.getByPseudo(
						derniereEnchere.getAcquereur().getPseudo());
				dernierEnrichisseur.setCredit(derniereEnchere.getMontant() + dernierEnrichisseur.getCredit());
				this.utilisateurDAO.updateCredit(dernierEnrichisseur.getPseudo(), dernierEnrichisseur.getCredit());
				if (dernierEnrichisseur.getPseudo().equals(utilisateur.getPseudo())) {
					utilisateur.setCredit(dernierEnrichisseur.getCredit());
					isSameAcquereur = true;
				}
			}
			
			// Debiter le nouveau acquéreur
			if (isSameAcquereur == true) {
				credit = utilisateur.getCredit();
			}
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

	/**
	 * Cette méthode permet d'effectuer le retrait d'un article vendu. Elle vérifie
	 * que l'article n'a pas encore été livré en contrôlant son statut. Si la
	 * vérification passe, elle crédite le vendeur et met à jour le statut de
	 * l'article comme livré. En cas d'échec de l'une des opérations, une
	 * BusinessException est levée et le rollback est exécuté.
	 *
	 * @param article       : L'article à retirer.
	 * @param pseudoVendeur : Le pseudo du vendeur de l'article.
	 * @throws BusinessException : Si le statut de l'article n'est pas valide, si
	 *                           une erreur survient lors du crédit du vendeur, ou
	 *                           si une erreur survient lors de la mise à jour du
	 *                           statut de l'article.
	 */
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void effectuerRetrait(ArticleAVendre article, String pseudoVendeur) {
		BusinessException be = new BusinessException();
		boolean isValid = true;

		// verifier que le status de l'article est 2
		isValid &= verifierStatutNonLivre(article.getStatut(), be);

		if (isValid) {
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
	

	/**
	 * Cette méthode vérifie si l'utilisateur dispose d'un crédit suffisant pour
	 * faire une offre d'un montant donné. Si le crédit est insuffisant, elle ajoute
	 * un code d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param montant : Le montant de l'offre.
	 * @param credit  : Le crédit disponible de l'utilisateur.
	 * @param be      : L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le crédit est suffisant, false sinon.
	 */
	private boolean verifierCreditSuffisant(int montant, int credit, BusinessException be) {
		if (credit < montant) {
			be.add(BusinessCode.VALIDATION_OFFRE_CREDIT);
			return false;
		}
		return true;
	}
	
	/**
	 * Cette méthode vérifie la validité du montant proposé pour une enchère en
	 * fonction du prix initial et du prix de vente actuel de l'article. Si le
	 * montant n'est pas valide, elle ajoute un code d'erreur à l'objet
	 * BusinessException et retourne false.
	 *
	 * @param montant     Le montant proposé pour l'enchère.
	 * @param prixInitial Le prix initial de l'article.
	 * @param prixVente   Le prix de vente actuel de l'article.
	 * @param be          L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le montant est valide, false sinon.
	 */
	private boolean verifierMontant(int montant, int prixInitial, int prixVente, BusinessException be) {
		if (prixVente >= prixInitial) {
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

	/**
	 * Cette méthode vérifie la validité des dates d'enchère en comparant la date de
	 * début et la date de fin avec la date actuelle. Si les dates ne sont pas
	 * valides, elle ajoute des codes d'erreur à l'objet BusinessException et
	 * retourne false.
	 *
	 * @param debut La date de début des enchères.
	 * @param fin   La date de fin des enchères.
	 * @param be    L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si les dates sont valides, false sinon.
	 */
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
	
	/**
	 * Cette méthode crédite le vendeur d'un article avec le montant de la dernière
	 * enchère. Elle récupère les informations de l'utilisateur et de la dernière
	 * enchère, puis met à jour le crédit de l'utilisateur dans la base de données.
	 *
	 * @param article       : L'article vendu dont le vendeur doit être crédité.
	 * @param pseudoVendeur : Le pseudo du vendeur à créditer.
	 * @return Le nombre de lignes mises à jour dans la base de données (devrait
	 *         être 1 si l'opération est réussie).
	 */
	private int crediterVendeur(ArticleAVendre article, String pseudoVendeur) {
		Utilisateur utilisateur = this.utilisateurDAO.getByPseudo(pseudoVendeur);
		Enchere enchere = this.enchereDAO.getDerniereEnchere(article.getId());
		int montantAcrediter = enchere.getMontant();
		int creditActuel = utilisateur.getCredit();
		utilisateur.setCredit(creditActuel + montantAcrediter);
		int count = this.utilisateurDAO.crediterVendeur(utilisateur.getPseudo(), utilisateur.getCredit());
		return count;
	}
	
	


	/**
	 * Cette méthode vérifie si le statut d'un article indique qu'il n'a pas encore
	 * été livré. Le statut doit être égal à 2 pour indiquer que l'article n'est pas
	 * encore livré. Si le statut est différent de 2, elle ajoute un code d'erreur à
	 * l'objet BusinessException et retourne false.
	 *
	 * @param statut Le statut de l'article.
	 * @param be     L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le statut indique que l'article n'est pas livré, false sinon.
	 */
	private boolean verifierStatutNonLivre(int statut, BusinessException be) {
		if (statut != 2) {
			be.add(BusinessCode.VALIDATION_STATUT_NON_LIVRE);
			return false;
		}
		return true;
	}

}
