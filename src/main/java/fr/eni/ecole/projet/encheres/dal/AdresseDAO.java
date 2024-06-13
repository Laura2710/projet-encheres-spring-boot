package fr.eni.ecole.projet.encheres.dal;

import java.util.List;

import fr.eni.ecole.projet.encheres.bo.Adresse;

public interface AdresseDAO {
	Adresse getByID(long id);

	void addAdresse(Adresse adresse);

	List<Adresse> findAll();

}
