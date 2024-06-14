package fr.eni.ecole.projet.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Enchere;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;

@Repository
public class EnchereDAOImpl implements EnchereDAO {

	private static final String FIND_MONTANT = "SELECT MONTANT_ENCHERE FROM ENCHERES WHERE id = :id";
	private static final String TROUVER_DERNIERE_ENCHERE = "SELECT TOP 1 id_utilisateur, no_article, montant_enchere, date_enchere FROM encheres WHERE no_article=:idArticle ORDER BY montant_enchere DESC";
	private static final String AJOUTER_ENCHERE = "INSERT INTO encheres (id_utilisateur, no_article, montant_enchere) VALUES (:idUtilisateur, :idArticle, :montant)";
	private static final String TROUVER_PAR_UTILISATEUR_ET_ARTICLE = "SELECT * FROM ENCHERES WHERE id_utilisateur=:idUtilisateur AND no_article=:idArticle";
	private static final String COMPTER_OFFRES_PAR_ARTICLE = "SELECT COUNT(*) FROM ENCHERES WHERE no_article=:idArticle";
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public int findMontant(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		return jdbcTemplate.queryForObject(FIND_MONTANT, namedParameters, int.class);
	}

	@Override
	public Enchere getDerniereEnchere(long id) {
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("idArticle", id);
		return jdbcTemplate.queryForObject(TROUVER_DERNIERE_ENCHERE, param, new EnchereRowMapper());
	}

	@Override
	public int addEnchere(Enchere enchere) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idUtilisateur", enchere.getAcquereur().getPseudo());
		namedParameters.addValue("idArticle", enchere.getArticleAVendre().getId());
		namedParameters.addValue("montant", enchere.getMontant());
		return jdbcTemplate.update(AJOUTER_ENCHERE, namedParameters);
	}

	public class EnchereRowMapper implements RowMapper<Enchere> {

		@Override
		public Enchere mapRow(ResultSet rs, int rowNum) throws SQLException {
			Enchere enchere = new Enchere();
			enchere.setMontant(rs.getInt("montant_enchere"));
		    enchere.setDateEnchere(rs.getTimestamp("date_enchere").toLocalDateTime());

			Utilisateur encherisseur = new Utilisateur();
			encherisseur.setPseudo(rs.getString("id_utilisateur"));

			ArticleAVendre article = new ArticleAVendre();
			article.setId(rs.getInt("no_article"));

			enchere.setAcquereur(encherisseur);
			enchere.setArticleAVendre(article);

			return enchere;
		}

	}

	@Override
	public Enchere getEnchereByUtilisateurAndArticle(long id, String pseudo) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idUtilisateur", pseudo);
		namedParameters.addValue("idArticle", id);
		return jdbcTemplate.queryForObject(TROUVER_PAR_UTILISATEUR_ET_ARTICLE, namedParameters,new EnchereRowMapper());
	}

	@Override
	public int getTotalOffre(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idArticle", id);
		return jdbcTemplate.queryForObject(COMPTER_OFFRES_PAR_ARTICLE, namedParameters, Integer.class);
	}


}
