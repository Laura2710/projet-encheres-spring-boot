package fr.eni.ecole.projet.encheres.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


public class EnchereDAOImpl implements EnchereDAO {
	
	private static final String FIND_MONTANT = "SELECT MONTANT_ENCHERE FROM ENCHERES WHERE id = :id";
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public int findMontant(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		return jdbcTemplate.queryForObject(FIND_MONTANT, namedParameters, int.class);
	}

}
