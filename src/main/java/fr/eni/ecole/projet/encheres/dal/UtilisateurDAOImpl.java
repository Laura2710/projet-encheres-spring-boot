package fr.eni.ecole.projet.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;

@Repository
public class UtilisateurDAOImpl implements UtilisateurDAO {

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Override
	public Utilisateur getByPseudo(String pseudo) {
		String sql = "SELECT pseudo, nom, prenom, email, telephone, credit, administrateur, no_adresse FROM utilisateurs WHERE pseudo = :pseudo";
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("pseudo", pseudo);
		return namedParameterJdbcTemplate.queryForObject(sql, param, new UtilisateurRowMapper());
	}
	
	@Override
	public void addUtilisateur(Utilisateur utilisateur) {
		String sql = "INSERT INTO utilisateurs (pseudo, nom, prenom, email, telephone, mot_de_passe, credit, administrateur, no_adresse) VALUES (:pseudo, :nom, :prenom, :email, :telephone, :motDePasse, 10, 0, :idAdresse)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pseudo", utilisateur.getPseudo());
		params.addValue("nom", utilisateur.getNom());
		params.addValue("prenom", utilisateur.getNom());
		params.addValue("email", utilisateur.getEmail());
		params.addValue("telephone", utilisateur.getTelephone());
		params.addValue("motDePasse", utilisateur.getMotDePasse());
		params.addValue("idAdresse", utilisateur.getAdresse().getId());
		
		namedParameterJdbcTemplate.update(sql, params);

	}
	
	public class UtilisateurRowMapper implements RowMapper<Utilisateur> {

		@Override
		public Utilisateur mapRow(ResultSet rs, int rowNum) throws SQLException {
			Utilisateur utilisateur = new Utilisateur();
			utilisateur.setPseudo(rs.getString("pseudo"));
			utilisateur.setNom(rs.getString("nom"));
			utilisateur.setPrenom(rs.getString("prenom"));
			utilisateur.setEmail(rs.getString("email"));
			utilisateur.setTelephone(rs.getString("telephone"));
			utilisateur.setCredit(rs.getInt("credit"));
			utilisateur.setAdministrateur(rs.getBoolean("administrateur"));
			
			Adresse adresse = new Adresse();
			adresse.setId(rs.getInt("no_adresse"));
			
			utilisateur.setAdresse(adresse);
			return utilisateur;
		}

	}



}
