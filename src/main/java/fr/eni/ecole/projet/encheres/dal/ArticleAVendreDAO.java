package fr.eni.ecole.projet.encheres.dal;

import java.util.List;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;

public interface ArticleAVendreDAO {
	
	ArticleAVendre getByID(long id);
	
	void addArticle(ArticleAVendre articleAVendre, Utilisateur vendeur, Adresse adresse);

	List<ArticleAVendre> findAllStatutEnCours();

}
