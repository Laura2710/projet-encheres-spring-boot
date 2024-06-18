package fr.eni.ecole.projet.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import fr.eni.ecole.projet.encheres.bo.Adresse;

@Repository
public class AdresseDAOImpl implements AdresseDAO {

	private static final String FIND_ALL = "SELECT no_adresse, rue, code_postal, ville, adresse_eni FROM ADRESSES WHERE adresse_eni = 1";
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public Adresse getByID(long id) {
		String sql = "SELECT * FROM Adresses WHERE no_adresse = :id";
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("id", id);

		return namedParameterJdbcTemplate.queryForObject(sql, param, new AdresseRowMapper());
	}

	@Override
	public void addAdresse(Adresse adresse) {
		String sql = "INSERT INTO adresses (rue, code_postal, ville) VALUES (:rue, :codePostal,:ville)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("rue", adresse.getRue());
		params.addValue("codePostal", adresse.getCodePostal());
		params.addValue("ville", adresse.getVille());

		KeyHolder keyHolder = new GeneratedKeyHolder();

		namedParameterJdbcTemplate.update(sql, params, keyHolder);

		if (keyHolder.getKey() != null) {
			adresse.setId(keyHolder.getKey().longValue());
		}

	}
	
	@Override
	public List<Adresse> findAll() {
		return namedParameterJdbcTemplate.query(FIND_ALL, new AdresseRowMapper());
	}

	public class AdresseRowMapper implements RowMapper<Adresse> {

		@Override
		public Adresse mapRow(ResultSet rs, int rowNum) throws SQLException {
			Adresse adresse = new Adresse();
			adresse.setId(rs.getInt("no_adresse"));
			adresse.setRue(rs.getString("rue"));
			adresse.setCodePostal(rs.getString("code_postal"));
			adresse.setVille(rs.getString("ville"));
			adresse.setAdresseEni(rs.getBoolean("adresse_eni"));
			return adresse;
		}

	}

	@Override
	public int updateAdresse(Adresse adresse) {
		String sql = "UPDATE adresses SET rue=:rue, code_postal=:codePostal, ville=:ville WHERE no_adresse=:id";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("rue", adresse.getRue());
		params.addValue("codePostal", adresse.getCodePostal());
		params.addValue("ville", adresse.getVille());
		params.addValue("id", adresse.getId());
		
		return namedParameterJdbcTemplate.update(sql, params);
		
	}

}
