package fr.eni.ecole.projet.encheres.dal;

import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;

public interface ArticleAVendreDAO {
	
	ArticleAVendre getByID(long id);
	
	void addArticle(ArticleAVendre articleAVendre);

}
