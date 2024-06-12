package fr.eni.ecole.projet.encheres.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.eni.ecole.projet.encheres.bll.ArticleAVendreService;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;

@Controller
@RequestMapping("/")
public class ArticleAVendreController {

	ArticleAVendreService articleAVendreService;

	public ArticleAVendreController(ArticleAVendreService articleAVendreService) {
		this.articleAVendreService = articleAVendreService;
	}
	
	@GetMapping
	public String afficherArticleAVendre(Model model) {
		List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreEnCours();
		model.addAttribute("articlesAVendre", articlesAVendre);
		return "view-article-a-vendre";
	}
	
}
