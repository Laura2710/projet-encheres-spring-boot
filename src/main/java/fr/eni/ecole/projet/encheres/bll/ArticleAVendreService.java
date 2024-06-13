package fr.eni.ecole.projet.encheres.bll;

import java.util.List;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Categorie;
import fr.eni.ecole.projet.encheres.bo.Enchere;

public interface ArticleAVendreService {

	List<ArticleAVendre> getArticlesAVendreEnCours();

	ArticleAVendre getById(int idArticle);

	void faireUneOffre(Enchere enchere);

	Enchere getEnchereByIdArticle(int idArticle);

	void mettreArticleEnVente(ArticleAVendre articleAVendre);
	
	List<Categorie> getAllCategories();

	List<Adresse> getAllAdressesRetrait();

}
