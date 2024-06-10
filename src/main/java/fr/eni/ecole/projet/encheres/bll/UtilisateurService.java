package fr.eni.ecole.projet.encheres.bll;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;

public interface UtilisateurService {
	Utilisateur getByPseudo(String pseudo);

	Adresse getAdresseByID(int id);
}
