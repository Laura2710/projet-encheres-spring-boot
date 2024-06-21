package fr.eni.ecole.projet.encheres.bll;

import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Enchere;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;

public interface EnchereService {
	Enchere getEnchereByIdArticle(int idArticle);
	void faireUneOffre(Enchere enchere, Utilisateur utilisateur);
	void effectuerRetrait(ArticleAVendre article, String name);

}
