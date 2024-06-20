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
	public int addUtilisateur(Utilisateur utilisateur) {
		String sql = "INSERT INTO utilisateurs (pseudo, nom, prenom, email, telephone, mot_de_passe, no_adresse) VALUES (:pseudo, :nom, :prenom, :email, :telephone, :motDePasse, :idAdresse)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pseudo", utilisateur.getPseudo());
		params.addValue("nom", utilisateur.getNom());
		params.addValue("prenom", utilisateur.getNom());
		params.addValue("email", utilisateur.getEmail());
		params.addValue("telephone", utilisateur.getTelephone());
		params.addValue("motDePasse", utilisateur.getMotDePasse());
		params.addValue("idAdresse", utilisateur.getAdresse().getId());
		

		int nbrLigneAffectee = namedParameterJdbcTemplate.update(sql, params);
		return nbrLigneAffectee;
	}
	
	@Override
	public boolean findEmail(String email) {
		String sql = "SELECT COUNT(*) as nbr FROM utilisateurs WHERE email=:email";
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("email", email);
		int count = namedParameterJdbcTemplate.queryForObject(sql, param, Integer.class);

        // Retourne true si le nombre d'utilisateurs  est supérieur à zéro
        return count > 0;
	}
	
	@Override
	public boolean findPseudo(String pseudo) {
		String sql = "SELECT COUNT(*) as nbr FROM utilisateurs WHERE pseudo=:pseudo";
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("pseudo", pseudo);
		int count = namedParameterJdbcTemplate.queryForObject(sql, param, Integer.class);

        // Retourne true si le nombre d'utilisateurs  est supérieur à zéro
        return count > 0;
	}

	@Override
	public int updateCredit(String pseudo, int credit) {
		String sql = "UPDATE utilisateurs SET credit=:credit WHERE pseudo=:pseudo";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("credit", credit);
		params.addValue("pseudo", pseudo);
		return namedParameterJdbcTemplate.update(sql, params); 
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


	@Override
	public int crediterVendeur(String pseudo, int credit) {
		String sql = "UPDATE utilisateurs SET credit=:credit WHERE pseudo=:pseudo";
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("pseudo", pseudo);
		param.addValue("credit", credit);
		return namedParameterJdbcTemplate.update(sql, param);
	}

	@Override
	public int updateProfil(Utilisateur utilisateur) {
		String sql = "UPDATE utilisateurs SET nom=:nom, prenom=:prenom, email=:email, telephone=:telephone WHERE pseudo=:pseudo";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("nom", utilisateur.getNom());
		params.addValue("prenom", utilisateur.getPrenom());
		params.addValue("email", utilisateur.getEmail());
		params.addValue("telephone", utilisateur.getTelephone());
		params.addValue("pseudo", utilisateur.getPseudo());
		return namedParameterJdbcTemplate.update(sql, params);
	}

	@Override
	public int findOldPwd(String ancienMotDePasse, String pseudo) {
		String sql= "SELECT count(*) FROM utilisateurs WHERE pseudo= :pseudo AND mot_de_passe= :mdp";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pseudo", pseudo);
		params.addValue("mdp", ancienMotDePasse);
		return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
		
	}

}
