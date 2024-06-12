package fr.eni.ecole.projet.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Categorie;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;

@Repository
public class ArticleAVendreDAOImpl implements ArticleAVendreDAO {

	private static final String INSERT = "INSERT INTO ARTICLES_A_VENDRE(nom_article, description, date_debut_encheres, date_fin_encheres, statut_enchere, prix_initial, prix_vente, id_utilisateur, no_categorie, no_adresse_retrait) VALUES "
			+ " (:nom, :description, :dateDebutEncheres, :dateFinEncheres, :statut, :prixInitial, :prixVente, :vendeur, :categorie, :adresse)";

	private static final String FIND_BY_ID = "SELECT * FROM ARTICLES_A_VENDRE WHERE no_article = :id";
	private static final String UPDATE_PRIX_VENTE = "UPDATE articles_a_vendre SET prix_vente=:prixVente WHERE no_article=:idArticle";
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Override
	public ArticleAVendre getByID(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		return namedParameterJdbcTemplate.queryForObject(FIND_BY_ID, namedParameters, new ArticleAVendreRowMapper());
	}

	@Override
	public void addArticle(ArticleAVendre articleAVendre, Utilisateur vendeur, Adresse adresse) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("nom", articleAVendre.getNom());
		namedParameters.addValue("description", articleAVendre.getDescription());
		namedParameters.addValue("dateDebutEncheres", articleAVendre.getDateDebutEncheres());
		namedParameters.addValue("dateFinEncheres", articleAVendre.getDateFinEncheres());
		namedParameters.addValue("statut", articleAVendre.getStatut());
		namedParameters.addValue("prixInitial", articleAVendre.getPrixInitial());
		namedParameters.addValue("prixVente", articleAVendre.getPrixVente());
		namedParameters.addValue("vendeur", articleAVendre.getVendeur().getNom());
		namedParameters.addValue("categorie", articleAVendre.getCategorie().getId());
		namedParameters.addValue("adresse", articleAVendre.getAdresseRetrait().getId());
		namedParameterJdbcTemplate.update(INSERT, namedParameters);
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



}
