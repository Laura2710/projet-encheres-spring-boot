package fr.eni.ecole.projet.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.eni.ecole.projet.encheres.bll.ArticleAVendreService;

@Controller
@RequestMapping
public class ArticleAVendreController {

	ArticleAVendreService articleAVendreService;

	public ArticleAVendreController(ArticleAVendreService articleAVendreService) {
		this.articleAVendreService = articleAVendreService;
	}
	
	
}
