package fr.eni.ecole.projet.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.dal.AdresseDAOImpl.AdresseRowMapper;

@Repository
public class AdresseDAOImpl implements AdresseDAO{


	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Override
	public Adresse getByID(int id) {
		String sql = "SELECT * FROM Adresses WHERE no_adresse = :id";
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("id", id);
		
		return namedParameterJdbcTemplate.queryForObject(sql, param, new AdresseRowMapper());
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

}
