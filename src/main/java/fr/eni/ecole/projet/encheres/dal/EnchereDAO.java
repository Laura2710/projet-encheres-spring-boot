package fr.eni.ecole.projet.encheres.dal;

import fr.eni.ecole.projet.encheres.bo.Enchere;

public interface EnchereDAO {

	int findMontant(long id);

	Enchere getDerniereEnchere(long id);

	int addEnchere(Enchere enchere);
}
