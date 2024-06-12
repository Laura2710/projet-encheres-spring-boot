package fr.eni.ecole.projet.encheres.bll;

import java.util.List;

import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;

public interface ArticleAVendreService {

	List<ArticleAVendre> getArticlesAVendreEnCours();
	
	
}
