package fr.eni.ecole.projet.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.ecole.projet.encheres.bo.Categorie;


@Repository
public class CategorieDAOImpl implements CategorieDAO {
	
	private static final String FIND_BY_ID = "SELECT NO_CATEGORIE, LIBELLE FROM CATEGORIES WHERE no_categorie = :id";


	private static final String FIND_ALL = "SELECT NO_CATEGORIE, LIBELLE FROM CATEGORIES";
	
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public Categorie read(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		
		return jdbcTemplate.queryForObject(FIND_BY_ID,  namedParameters, new CategorieRowMappeur());
	}

	@Override
	public List<Categorie> findAll() {
		return jdbcTemplate.query(FIND_ALL, new CategorieRowMappeur());
	}
	
	
	class CategorieRowMappeur implements RowMapper<Categorie>{
		@Override
		public Categorie mapRow(ResultSet rs, int rowNUM) throws SQLException {
			Categorie categorie = new Categorie();
			categorie.setId(rs.getLong("no_categorie"));
			categorie.setLibelle(rs.getString("libelle"));
			
			return categorie;
		}
	}
}
