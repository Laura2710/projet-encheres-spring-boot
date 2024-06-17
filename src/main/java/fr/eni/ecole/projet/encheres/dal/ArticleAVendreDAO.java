package fr.eni.ecole.projet.encheres.dal;

import java.util.List;

import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;

public interface ArticleAVendreDAO {

	ArticleAVendre getByID(long id);

	void addArticle(ArticleAVendre articleAVendre);

	void updatePrixVente(long id, int montant);

	List<ArticleAVendre> findAllStatutEnCours();

	int annulerVente(long id);

	void updateArticle(ArticleAVendre articleAVendre);

	List<ArticleAVendre> findAllWithParameters(String nomRecherche, int categorieRecherche);

	int livrerVente(long id);

	int activerVente();

	int cloturerVente();


}
