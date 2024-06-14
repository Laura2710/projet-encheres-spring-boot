package fr.eni.ecole.projet.encheres.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import fr.eni.ecole.projet.encheres.bll.ArticleAVendreService;
import fr.eni.ecole.projet.encheres.bo.Categorie;

@Component
public class StringToCategorieConverter implements Converter<String, Categorie> {
	
	private ArticleAVendreService service;
	
	public StringToCategorieConverter (ArticleAVendreService service) {
		this.service = service;
		}
	
	public Categorie convert(String id) {
		Long theId = Long.parseLong(id);
		return service.getCategorieById(theId);
	}

}
