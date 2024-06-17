package fr.eni.ecole.projet.encheres.bll;


import java.util.List;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Categorie;
import fr.eni.ecole.projet.encheres.bo.Enchere;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;

public interface ArticleAVendreService {

	List<ArticleAVendre> getArticlesAVendreEnCours();

	ArticleAVendre getById(int idArticle);

	void faireUneOffre(Enchere enchere, Utilisateur utilisateur);

	Enchere getEnchereByIdArticle(int idArticle);

	void mettreArticleEnVente(ArticleAVendre articleAVendre, Utilisateur utilisateur);
	
	void modifierArticleEnVente(ArticleAVendre articleAVendre);
	
	Categorie getCategorieById (long id);
	
	List<Categorie> getAllCategories();
	
	Adresse getAdresseById (long id);

	List<Adresse> getAllAdressesRetrait();

	void annulerVente(ArticleAVendre article);


	List<ArticleAVendre> getVentesNonCommencees();

	void activerVente(long id);

	List<ArticleAVendre> getArticlesAVendreAvecParamètres(String nomRecherche,
			int categorieRecherche);

	List<ArticleAVendre> getVentesTerminees();

	void cloturerVente(long id);

	void effectuerRetrait(ArticleAVendre article, String name);


}