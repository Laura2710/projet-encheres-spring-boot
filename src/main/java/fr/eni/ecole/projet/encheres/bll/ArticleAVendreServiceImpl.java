package fr.eni.ecole.projet.encheres.bll;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Categorie;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;
import fr.eni.ecole.projet.encheres.dal.AdresseDAO;
import fr.eni.ecole.projet.encheres.dal.ArticleAVendreDAO;
import fr.eni.ecole.projet.encheres.dal.CategorieDAO;
import fr.eni.ecole.projet.encheres.exceptions.BusinessCode;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;

@Service
public class ArticleAVendreServiceImpl implements ArticleAVendreService {

	private ArticleAVendreDAO articleAVendreDAO;
	private AdresseDAO adresseDAO;
	private CategorieDAO categorieDAO;

	
	public ArticleAVendreServiceImpl(ArticleAVendreDAO articleAVendreDAO, AdresseDAO adresseDAO,
			CategorieDAO categorieDAO) {
		this.articleAVendreDAO = articleAVendreDAO;
		this.adresseDAO = adresseDAO;
		this.categorieDAO = categorieDAO;
	}

	/**
	 * Cette méthode permet de mettre un article en vente. Elle assigne
	 * l'utilisateur en tant que vendeur de l'article et valide l'article avant de
	 * l'ajouter à la base de données. Si les validations échouent, une
	 * BusinessException est levée.
	 *
	 * @param articleAVendre L'article à mettre en vente.
	 * @param utilisateur    L'utilisateur qui met l'article en vente.
	 * @throws BusinessException Si l'article n'est pas valide.
	 */
	@Override
	public void mettreArticleEnVente(ArticleAVendre articleAVendre, Utilisateur utilisateur) {
		BusinessException be = new BusinessException();
		articleAVendre.setVendeur(utilisateur);
		if (validationArticle(articleAVendre, be)) {
			try {
				articleAVendreDAO.addArticle(articleAVendre);
			} catch (DataAccessException e) {
				be.printStackTrace();
				// TODO Créer businessException
			}
		} else {
			throw be;
		}

	}

	/**
	 * Cette méthode permet de modifier un article en vente. Elle valide l'article
	 * avant de mettre à jour ses informations dans la base de données. Si les
	 * validations échouent, une BusinessException est levée.
	 *
	 * @param articleAVendre L'article à vendre à modifier.
	 * @param pseudo         Le pseudo de l'utilisateur modifiant l'article.
	 * @throws BusinessException Si l'article n'est pas valide.
	 */
	@Override
	public void modifierArticleEnVente(ArticleAVendre articleAVendre, String pseudo) {
		BusinessException be = new BusinessException();

		if (validationArticle(articleAVendre, be)) {
			Utilisateur utilisateur = new Utilisateur();
			utilisateur.setPseudo(pseudo);
			articleAVendre.setVendeur(utilisateur);
			articleAVendreDAO.updateArticle(articleAVendre);
		} else {
			throw be;
		}
	}

	/**
	 * Cette méthode vérifie si un article à vendre est valide en effectuant une
	 * série de validations sur ses différentes propriétés telles que le nom, la
	 * description, les dates des enchères, le prix initial, la catégorie et
	 * l'adresse de retrait. Si l'une de ces validations échoue, elle ajoute un code
	 * d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param articleAVendre L'article à vendre à valider.
	 * @param be             L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si toutes les validations sont réussies, false sinon.
	 */
	private boolean validationArticle(ArticleAVendre articleAVendre, BusinessException be) {
		boolean isValid = true;
		isValid &= validerArticleAVendre(articleAVendre, be);
		isValid &= validerNom(articleAVendre.getNom(), be);
		isValid &= validerDescription(articleAVendre.getDescription(), be);
		isValid &= validerDateDebutEncheres(articleAVendre.getDateDebutEncheres(), be);
		isValid &= validerDateFinEncheres(articleAVendre.getDateFinEncheres(), articleAVendre.getDateDebutEncheres(),
				be);
		isValid &= validerPrixInitial(articleAVendre.getPrixInitial(), be);
		isValid &= validerCategorie(articleAVendre.getCategorie(), be);
		isValid &= validerAdresseRetrait(articleAVendre.getAdresseRetrait(), be);
		return isValid;
	}

	/**
	 * Cette méthode vérifie si un article à vendre est valide. L'article ne doit
	 * pas être nul. Si cette condition n'est pas remplie, elle ajoute un code
	 * d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param articleAVendre L'article à vendre.
	 * @param be             L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si l'article est valide, false sinon.
	 */
	private boolean validerArticleAVendre(ArticleAVendre articleAVendre, BusinessException be) {
		if (articleAVendre == null) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_NULL);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si le nom d'un article est valide. Le nom ne doit pas
	 * être nul ou vide, et sa longueur doit être comprise entre 5 et 30 caractères.
	 * Si l'une de ces conditions n'est pas remplie, elle ajoute un code d'erreur à
	 * l'objet BusinessException et retourne false.
	 *
	 * @param nom Le nom de l'article.
	 * @param be  L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le nom est valide, false sinon.
	 */
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

	/**
	 * Cette méthode vérifie si la description d'un article est valide. La
	 * description ne doit pas être nulle ou vide, et sa longueur doit être comprise
	 * entre 20 et 300 caractères. Si l'une de ces conditions n'est pas remplie,
	 * elle ajoute un code d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param description La description de l'article.
	 * @param be          L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si la description est valide, false sinon.
	 */
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

	/**
	 * Cette méthode récupère une liste d'articles à vendre qui sont actuellement en
	 * cours de vente. Elle appelle la méthode correspondante de l'objet DAO pour
	 * obtenir les articles avec le statut en cours.
	 *
	 * @return Une liste d'articles à vendre actuellement en cours de vente.
	 */
	@Override
	public List<ArticleAVendre> getArticlesAVendreEnCours() {
		// Remonter la liste des article a vendre en cours depuis la DAL
		List<ArticleAVendre> articlesAVendreEnCours = articleAVendreDAO.findAllStatutEnCours();

		return articlesAVendreEnCours;
	}

	/**
	 * Cette méthode récupère une liste d'articles à vendre en fonction de divers
	 * paramètres de recherche. Elle filtre les articles par nom, catégorie, statut
	 * et cas d'utilisation. Si un utilisateur est connecté, son pseudo est
	 * également pris en compte pour certains cas d'utilisation.
	 *
	 * @param nomRecherche          Le nom ou partie du nom de l'article recherché.
	 * @param categorieRecherche    L'identifiant de la catégorie de l'article
	 *                              recherché.
	 * @param casUtilisationFiltres Le cas d'utilisation des filtres pour la
	 *                              recherche.
	 * @param principal             L'objet Principal représentant l'utilisateur
	 *                              connecté.
	 * @return Une liste d'articles correspondant aux paramètres de recherche.
	 */
	@Override
	public List<ArticleAVendre> getArticlesAVendreAvecParamètres(String nomRecherche, int categorieRecherche,
			int casUtilisationFiltres, Principal principal) {
		int statutRecherche = 1;
		String pseudoUtilisateurEnSession = null;
		if (principal != null) {
			switch (casUtilisationFiltres) {
			case 1:
			case 3:
				pseudoUtilisateurEnSession = principal.getName();
				break;
			case 4:
				statutRecherche = 0;
				pseudoUtilisateurEnSession = principal.getName();
				break;
			case 2:
			case 5:
				statutRecherche = 2;
				pseudoUtilisateurEnSession = principal.getName();
				break;

			}
		}

		List<ArticleAVendre> articlesAVendreAvecParametres = articleAVendreDAO.findAllWithParameters(nomRecherche,
				categorieRecherche, statutRecherche, casUtilisationFiltres, pseudoUtilisateurEnSession);

		return articlesAVendreAvecParametres;
	}

	/**
	 * Cette méthode récupère un article à vendre en fonction de son identifiant.
	 * Elle vérifie que l'identifiant est valide, puis récupère l'article ainsi que
	 * son adresse de retrait et sa catégorie. Si une erreur survient lors de
	 * l'accès aux données, une BusinessException est levée.
	 *
	 * @param idArticle : L'identifiant de l'article à récupérer.
	 * @return L'article à vendre correspondant à l'identifiant fourni.
	 * @throws BusinessException : Si l'identifiant de l'article est invalide ou si
	 *                           une erreur survient lors de l'accès aux données.
	 */
	@Override
	public ArticleAVendre getById(int idArticle) {
		BusinessException be = new BusinessException();
		if (idArticle > 0) {
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

		} else {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_NULL);
			throw be;
		}

	}

	


	/**
	 * Cette méthode vérifie si la date de début des enchères est valide. La date de
	 * début ne doit pas être nulle, être antérieure ou égale à la date actuelle. Si
	 * l'une de ces conditions n'est pas remplie, elle ajoute un code d'erreur à
	 * l'objet BusinessException et retourne false.
	 *
	 * @param dateDebutEncheres La date de début des enchères.
	 * @param be                L'objet BusinessException à compléter en cas
	 *                          d'erreur.
	 * @return true si la date de début des enchères est valide, false sinon.
	 */
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

	/**
	 * Cette méthode vérifie si la date de fin des enchères est valide. La date de
	 * fin ne doit pas être nulle, antérieure à la date actuelle, ou antérieure ou
	 * égale à la date de début des enchères. Si l'une de ces conditions n'est pas
	 * remplie, elle ajoute un code d'erreur à l'objet BusinessException et retourne
	 * false.
	 *
	 * @param dateFinEncheres   : La date de fin des enchères.
	 * @param dateDebutEncheres : La date de début des enchères.
	 * @param be                : L'objet BusinessException à compléter en cas
	 *                          d'erreur.
	 * @return true si la date de fin des enchères est valide, false sinon.
	 */
	private boolean validerDateFinEncheres(LocalDate dateFinEncheres, LocalDate dateDebutEncheres,
			BusinessException be) {
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

	/**
	 * Cette méthode vérifie si le prix initial d'un article est valide. Le prix
	 * initial doit être supérieur ou égal à 1. Si cette condition n'est pas
	 * remplie, elle ajoute un code d'erreur à l'objet BusinessException et retourne
	 * false.
	 *
	 * @param prixInitial : Le prix initial de l'article.
	 * @param be          : L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le prix initial est valide, false sinon.
	 */
	private boolean validerPrixInitial(int prixInitial, BusinessException be) {
		if (prixInitial < 1) {
			be.add(BusinessCode.VALIDATION_ARTICLE_A_VENDRE_PRIX_INITIAL);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si l'adresse de retrait d'un article est valide.
	 * L'adresse ne doit pas être nulle et doit avoir un identifiant valide. Si
	 * l'adresse est inconnue ou invalide, elle ajoute des codes d'erreur à l'objet
	 * BusinessException et retourne false.
	 *
	 * @param adresseRetrait L'adresse de retrait de l'article.
	 * @param be             L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si l'adresse de retrait est valide, false sinon.
	 */
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

	/**
	 * Cette méthode vérifie si la catégorie d'un article est valide. La catégorie
	 * ne doit pas être nulle et doit avoir un identifiant valide. Si la catégorie
	 * est inconnue ou invalide, elle ajoute des codes d'erreur à l'objet
	 * BusinessException et retourne false.
	 *
	 * @param categorie La catégorie de l'article.
	 * @param be        L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si la catégorie est valide, false sinon.
	 */
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

	/**
	 * Cette méthode récupère une catégorie en fonction de son identifiant.
	 *
	 * @param id L'identifiant de la catégorie à récupérer.
	 * @return La catégorie correspondant à l'identifiant fourni.
	 */
	@Override
	public Categorie getCategorieById(long id) {
		return categorieDAO.read(id);
	}

	/**
	 * Cette méthode récupère toutes les catégories disponibles.
	 *
	 * @return Une liste de toutes les catégories.
	 */
	public List<Categorie> getAllCategories() {
		return categorieDAO.findAll();
	}

	/**
	 * Cette méthode récupère une adresse en fonction de son identifiant.
	 *
	 * @param id L'identifiant de l'adresse à récupérer.
	 * @return L'adresse correspondant à l'identifiant fourni.
	 */
	public Adresse getAdresseById(long id) {
		return adresseDAO.getByID(id);
	}

	/**
	 * Cette méthode récupère toutes les adresses de retrait disponibles.
	 *
	 * @return Une liste de toutes les adresses de retrait.
	 */
	public List<Adresse> getAllAdressesRetrait() {
		return adresseDAO.findAll();
	}





	/**
	 * Cette méthode permet d'annuler la vente d'un article. Elle vérifie si
	 * l'annulation est valide en contrôlant la date de début de l'enchère et le
	 * statut de l'article. Si les validations passent, la vente est annulée dans la
	 * base de données. En cas de problème, une BusinessException est levée.
	 *
	 * @param article : L'article dont la vente doit être annulée.
	 * @throws BusinessException : Si l'annulation n'est pas valide ou si une erreur
	 *                           survient lors de l'annulation.
	 */
	@Override
	public void annulerVente(ArticleAVendre article) {
		BusinessException be = new BusinessException();
		boolean isValid = true;
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

	/**
	 * Cette méthode vérifie si le statut de la vente permet son annulation. La
	 * vente ne peut être annulée que si son statut est égal à 0. Si le statut est
	 * supérieur à 0, elle ajoute un code d'erreur à l'objet BusinessException et
	 * retourne false.
	 *
	 * @param statut : Le statut de la vente.
	 * @param be     : L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le statut permet l'annulation, false sinon.
	 */
	private boolean validerStatutVente(int statut, BusinessException be) {
		if (statut > 0) {
			be.add(BusinessCode.VALIDATION_ANNULER_VENTE_STATUT);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si la date de début des enchères permet l'annulation de
	 * la vente. La vente ne peut être annulée que si la date de début des enchères
	 * est postérieure à la date actuelle. Si la date de début est antérieure ou
	 * égale à la date actuelle, elle ajoute un code d'erreur à l'objet
	 * BusinessException et retourne false.
	 *
	 * @param dateDebutEncheres La date de début des enchères.
	 * @param be                L'objet BusinessException à compléter en cas
	 *                          d'erreur.
	 * @return true si la date de début permet l'annulation, false sinon.
	 */
	private boolean validerAnnulationDateDebutEnchere(LocalDate dateDebutEncheres, BusinessException be) {
		LocalDate today = LocalDate.now();
		if (dateDebutEncheres.isBefore(today) || dateDebutEncheres.equals(today)) {
			be.add(BusinessCode.VALIDATION_ANNULER_VENTE_DATE_DEBUT);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode permet d'activer toutes les ventes d'articles en mettant à jour
	 * leur statut dans la base de données. Elle appelle la méthode correspondante
	 * de l'objet DAO.
	 */
	@Override
	public void activerVente() {
		this.articleAVendreDAO.activerVente();
	}

	/**
	 * Cette méthode permet de clôturer toutes les ventes d'articles en mettant à
	 * jour leur statut dans la base de données. Elle appelle la méthode
	 * correspondante de l'objet DAO.
	 */
	@Override
	public void cloturerVente() {
		this.articleAVendreDAO.cloturerVente();
	}

	@Override
	public void ajouterPhotoArticle(int idArticle, String newFilename, String pseudo) {
		// TODO RESTE A FAIRE VALIDATION 
		
		BusinessException be = new BusinessException();
		boolean isValid = true;
		// Si l'article appartient à l'utilisateur
		isValid &= verifierArticleEstAutilisateur(idArticle, pseudo, be);
		if(isValid) {
			this.articleAVendreDAO.ajouterPhoto(idArticle, newFilename);
		}
		else {
			throw be;
		}
	}

	private boolean verifierArticleEstAutilisateur(int idArticle, String pseudo, BusinessException be) {
		int count = this.articleAVendreDAO.trouverProprietaireArticle(idArticle, pseudo);
		if (count < 1) {
			return false;
		}
		return true;
	}





}
