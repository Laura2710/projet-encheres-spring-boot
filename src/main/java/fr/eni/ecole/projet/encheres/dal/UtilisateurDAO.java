package fr.eni.ecole.projet.encheres.dal;

import fr.eni.ecole.projet.encheres.bo.Utilisateur;

public interface UtilisateurDAO {
	Utilisateur getByPseudo(String pseudo);

	int addUtilisateur(Utilisateur utilisateur);

	boolean findEmail(String email);

	boolean findPseudo(String pseudo);

	int updateCredit(String pseudo, int credit);

	int crediterVendeur(String pseudo, int credit);

	int updateProfil(Utilisateur utilisateur);

}
