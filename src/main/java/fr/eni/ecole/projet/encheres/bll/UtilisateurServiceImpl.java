package fr.eni.ecole.projet.encheres.bll;

import org.springframework.stereotype.Service;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;
import fr.eni.ecole.projet.encheres.dal.AdresseDAO;
import fr.eni.ecole.projet.encheres.dal.UtilisateurDAO;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

	UtilisateurDAO utilisateurDAO;
	AdresseDAO adresseDAO;
	
	
	public UtilisateurServiceImpl(UtilisateurDAO utilisateurDAO, AdresseDAO adresseDAO) {
		this.utilisateurDAO = utilisateurDAO;
		this.adresseDAO = adresseDAO;
	}

	@Override
	public Utilisateur getByPseudo(String pseudo) {
		return this.utilisateurDAO.getByPseudo(pseudo);
	}

	@Override
	public Adresse getAdresseByID(int id) {
		return this.adresseDAO.getByID(id);
	}

}
