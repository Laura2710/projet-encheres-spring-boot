package fr.eni.ecole.projet.encheres.dal;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Categorie;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;

@Repository
public class ArticleAVendreDAOImpl implements ArticleAVendreDAO {

	private static final String INSERT_ARTICLE = "INSERT INTO ARTICLES_A_VENDRE(nom_article, description, date_debut_encheres, date_fin_encheres, statut_enchere, prix_initial, id_utilisateur, no_categorie, no_adresse_retrait) VALUES "
			+ " (:nom, :description, :dateDebutEncheres, :dateFinEncheres, 0, :prixInitial, :vendeur, :categorie, :adresse)";
	
	private static final String UPDATE_ARTICLE = "UPDATE ARTICLES_A_VENDRE SET nom_article = :nom, description = :description, date_debut_encheres = :dateDebutEncheres, date_fin_encheres = :dateFinEncheres,prix_initial = :prixInitial, no_categorie = :categorie, no_adresse_retrait = :adresse WHERE no_article=:id";

	private static final String FIND_BY_ID = "SELECT * FROM ARTICLES_A_VENDRE WHERE no_article = :id";
	private static final String UPDATE_PRIX_VENTE = "UPDATE articles_a_vendre SET prix_vente=:prixVente WHERE no_article=:idArticle";
	private static final String FIND_ALL_STATUT_EN_COURS = "SELECT * FROM ARTICLES_A_VENDRE WHERE statut_enchere = 1";
	private static final String DELETE_VENTE = "UPDATE articles_a_vendre SET statut_enchere=100 WHERE no_article=:idArticle";
	private static final String GET_VENTE_NON_COMMENCEES_DU_JOUR = "SELECT * FROM articles_a_vendre WHERE statut_enchere=0 AND date_debut_encheres=CAST(GETDATE() as DATE)";
	private static final String ACTIVER_VENTE = "UPDATE articles_a_vendre SET statut_enchere=1 WHERE no_article=:idArticle";
	private static final String GET_VENTE_TERMINEES_DU_JOUR = "SELECT * FROM articles_a_vendre WHERE statut_enchere=1 AND date_fin_encheres=CAST(GETDATE() as DATE)";
	private static final String CLOTURER_VENTE = "UPDATE articles_a_vendre SET statut_enchere=2 WHERE no_article=:idArticle";

	private static final String LIVRER_VENTE = "UPDATE articles_a_vendre SET statut_enchere=3 WHERE no_article=:idArticle";

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public ArticleAVendre getByID(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		return namedParameterJdbcTemplate.queryForObject(FIND_BY_ID, namedParameters, new ArticleAVendreRowMapper());
	}

	@Override
	public void addArticle(ArticleAVendre articleAVendre) {
		MapSqlParameterSource namedParameters = preparerParamValidationArticle(articleAVendre);
		namedParameters.addValue("vendeur", articleAVendre.getVendeur().getPseudo());
		namedParameterJdbcTemplate.update(INSERT_ARTICLE, namedParameters);
	}

	@Override
	public void updateArticle(ArticleAVendre articleAVendre) {
		MapSqlParameterSource namedParameters = preparerParamValidationArticle(articleAVendre);
		namedParameters.addValue("id", articleAVendre.getId());
		BusinessException be = new BusinessException();
		
		if (articleExiste(articleAVendre.getId())) {
		namedParameterJdbcTemplate.update(UPDATE_ARTICLE, namedParameters);
		} else {
	        throw be; 
	    }
	}
	
	private boolean articleExiste(long id) {
		 String sql = "SELECT COUNT(*) FROM articles_a_vendre WHERE no_article = :id";
		 MapSqlParameterSource parameters = new MapSqlParameterSource();
		 parameters.addValue("id", id);
		 int count = namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class);
		 return count > 0;
	}
	
	private MapSqlParameterSource preparerParamValidationArticle(ArticleAVendre articleAVendre) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("nom", articleAVendre.getNom());
	    namedParameters.addValue("description", articleAVendre.getDescription());
	    namedParameters.addValue("dateDebutEncheres", convertirDate(articleAVendre.getDateDebutEncheres()));
	    namedParameters.addValue("dateFinEncheres", convertirDate(articleAVendre.getDateFinEncheres()));
	    namedParameters.addValue("prixInitial", articleAVendre.getPrixInitial());
	    namedParameters.addValue("categorie", articleAVendre.getCategorie().getId());
	    namedParameters.addValue("adresse", articleAVendre.getAdresseRetrait().getId());
	    return namedParameters;
	}
	
	public Date convertirDate(LocalDate localDate) {
	    return Date.valueOf(localDate);
	}
	

	@Override
	public void updatePrixVente(long id, int montant) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("prixVente", montant);
		params.addValue("idArticle", id);
		namedParameterJdbcTemplate.update(UPDATE_PRIX_VENTE, params);
	}

	public class ArticleAVendreRowMapper implements RowMapper<ArticleAVendre> {

		@Override
		public ArticleAVendre mapRow(ResultSet rs, int rowNum) throws SQLException {
			ArticleAVendre articleAVendre = new ArticleAVendre();
			articleAVendre.setId(rs.getInt("no_article"));
			articleAVendre.setNom(rs.getString("nom_article"));
			articleAVendre.setDescription(rs.getString("description"));
			articleAVendre.setDateDebutEncheres(rs.getDate("date_debut_encheres").toLocalDate());
			articleAVendre.setDateFinEncheres(rs.getDate("date_fin_encheres").toLocalDate());
			articleAVendre.setStatut(rs.getInt("statut_enchere"));
			articleAVendre.setPrixInitial(rs.getInt("prix_initial"));
			articleAVendre.setPrixVente(rs.getInt("prix_vente"));

			Utilisateur utilisateur = new Utilisateur();
			utilisateur.setPseudo(rs.getString("id_utilisateur"));
			articleAVendre.setVendeur(utilisateur);

			Categorie categorie = new Categorie();
			categorie.setId(rs.getInt("no_categorie"));
			articleAVendre.setCategorie(categorie);

			Adresse adresse = new Adresse();
			adresse.setId(rs.getInt("no_adresse_retrait"));
			articleAVendre.setAdresseRetrait(adresse);
			return articleAVendre;
		}

	}

	@Override
	public List<ArticleAVendre> findAllStatutEnCours() {
		return namedParameterJdbcTemplate.query(FIND_ALL_STATUT_EN_COURS, new ArticleAVendreRowMapper());

	}

	@Override

	public int annulerVente(long idArticle) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", idArticle);
		return namedParameterJdbcTemplate.update(DELETE_VENTE, params);
	}

	@Override
	public List<ArticleAVendre> getVentesNonCommencees() {
		return namedParameterJdbcTemplate.query(GET_VENTE_NON_COMMENCEES_DU_JOUR, new ArticleAVendreRowMapper());
	}

	@Override
	public int activerVente(long idArticle) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", idArticle);
		return namedParameterJdbcTemplate.update(ACTIVER_VENTE, params);
	}

	public List<ArticleAVendre> findAllWithParameters(String nomRecherche, int categorieRecherche) {
		// Création de mon String Builder avec la requete de base
		StringBuilder FIND_ALL_WITH_PARAMETERS = new StringBuilder(
				"SELECT * FROM ARTICLES_A_VENDRE WHERE statut_enchere = 1 ");

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		if (nomRecherche != "") {
			//Ajout des % a mon nom recherché pour la requete SQL
			String SQLNomRecherche = "%" + nomRecherche + '%';
			namedParameters.addValue("SQLNomRecherche",SQLNomRecherche);
			FIND_ALL_WITH_PARAMETERS.append("AND nom_article LIKE :SQLNomRecherche ");
		}
		if (categorieRecherche != 0) {
			namedParameters.addValue("categorieRecherche", categorieRecherche);
			FIND_ALL_WITH_PARAMETERS.append("AND no_categorie = :categorieRecherche ");
		}
		return namedParameterJdbcTemplate.query(FIND_ALL_WITH_PARAMETERS.toString(), namedParameters,
				new ArticleAVendreRowMapper());

	}

	@Override
	public List<ArticleAVendre> getVentesTerminees() {
		return namedParameterJdbcTemplate.query(GET_VENTE_TERMINEES_DU_JOUR, new ArticleAVendreRowMapper());
	}

	@Override
	public int cloturerVente(long idArticle) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", idArticle);
		return namedParameterJdbcTemplate.update(CLOTURER_VENTE, params);
	}

	@Override
	public int livrerVente(long idArticle) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", idArticle);
		return namedParameterJdbcTemplate.update(LIVRER_VENTE, params);
	}

}
