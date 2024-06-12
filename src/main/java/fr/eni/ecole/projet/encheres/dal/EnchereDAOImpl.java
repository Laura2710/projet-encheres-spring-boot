package fr.eni.ecole.projet.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
	private static final String TROUVER_DERNIERE_ENCHERE = "SELECT TOP 1 id_utilisateur,no_article, montant_enchere FROM encheres WHERE no_article = :idArticle ORDER BY date_enchere DESC";
	private static final String AJOUTER_ENCHERE = "INSERT INTO encheres (id_utilisateur, no_article, montant_enchere) VALUES (:idUtilisateur, :idArticle, :montant)";
	
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
		try {
			return jdbcTemplate.queryForObject(TROUVER_DERNIERE_ENCHERE, param, new EnchereRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
	        return null;
	    }
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
			
			Utilisateur encherisseur = new Utilisateur();
			encherisseur.setPseudo(rs.getString("id_utilisateur"));
			
			ArticleAVendre article = new ArticleAVendre();
			article.setId(rs.getInt("no_article"));
			
			enchere.setAcquereur(encherisseur);
			enchere.setArticleAVendre(article);
			
			return enchere;
		}

	}



}
