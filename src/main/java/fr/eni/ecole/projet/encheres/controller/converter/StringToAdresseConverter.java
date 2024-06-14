package fr.eni.ecole.projet.encheres.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import fr.eni.ecole.projet.encheres.bll.ArticleAVendreService;
import fr.eni.ecole.projet.encheres.bo.Adresse;

@Component
public class StringToAdresseConverter implements Converter<String, Adresse> {

	private ArticleAVendreService service;
	
	public StringToAdresseConverter (ArticleAVendreService service) {
		this.service = service;
		}
	
	public Adresse convert(String id) {
		Long theId = Long.parseLong(id);
		return service.getAdresseById(theId);
	}
}
