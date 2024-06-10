package fr.eni.ecole.projet.encheres.dal;

import fr.eni.ecole.projet.encheres.bo.Utilisateur;

public interface UtilisateurDAO {
	Utilisateur getByPseudo(String pseudo);
}
