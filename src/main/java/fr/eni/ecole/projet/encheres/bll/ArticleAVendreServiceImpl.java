package fr.eni.ecole.projet.encheres.bll;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.dal.AdresseDAO;
import fr.eni.ecole.projet.encheres.dal.ArticleAVendreDAO;
import fr.eni.ecole.projet.encheres.dal.CategorieDAO;
import fr.eni.ecole.projet.encheres.dal.EnchereDAO;
import fr.eni.ecole.projet.encheres.dal.UtilisateurDAO;

@Service
public class ArticleAVendreServiceImpl implements ArticleAVendreService {

	//Injection des Repository
	private ArticleAVendreDAO articleAVendreDAO;
	private AdresseDAO adresseDAO;
	private UtilisateurDAO utilisateurDAO;
	private CategorieDAO categorieDAO;
	private EnchereDAO enchereDAO;
	
	public ArticleAVendreServiceImpl(ArticleAVendreDAO articleAVendreDAO, AdresseDAO adresseDAO,
			UtilisateurDAO utilisateurDAO, CategorieDAO categorieDAO, EnchereDAO enchereDAO) {
		this.articleAVendreDAO = articleAVendreDAO;
		this.adresseDAO = adresseDAO;
		this.utilisateurDAO = utilisateurDAO;
		this.categorieDAO = categorieDAO;
		this.enchereDAO = enchereDAO;
	}

	@Override
	public List<ArticleAVendre> getArticlesAVendreEnCours() {
		//Remonter la liste des article a vendre en cours depuis la DAL
		List<ArticleAVendre> articlesAVendreEnCours = articleAVendreDAO.findAllStatutEnCours();
		
		return articlesAVendreEnCours;
	}
	
	
	

}
