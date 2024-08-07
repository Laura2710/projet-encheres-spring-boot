package fr.eni.ecole.projet.encheres.bll;


import java.security.Principal;
import java.util.List;

import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.bo.Categorie;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;

public interface ArticleAVendreService {

	List<ArticleAVendre> getArticlesAVendreEnCours();

	ArticleAVendre getById(int idArticle);

	void mettreArticleEnVente(ArticleAVendre articleAVendre, Utilisateur utilisateur);
	
	void modifierArticleEnVente(ArticleAVendre articleAVendre, String pseudo);
	
	Categorie getCategorieById (long id);
	
	List<Categorie> getAllCategories();
	
	Adresse getAdresseById (long id);

	List<Adresse> getAllAdressesRetrait();

	void annulerVente(ArticleAVendre article);

	List<ArticleAVendre> getArticlesAVendreAvecParamètres(String nomRecherche,
			int categorieRecherche, int casUtilisationFiltres, Principal principal);

	void activerVente();

	void cloturerVente();


	void ajouterPhotoArticle(int idArticle, String newFilename, String name);

}
