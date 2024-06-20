package fr.eni.ecole.projet.encheres.configuration;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.ecole.projet.encheres.bll.ArticleAVendreService;

@Component
@EnableScheduling
public class EnchereManager {

	ArticleAVendreService articleAVendreService;

	public EnchereManager(ArticleAVendreService articleAVendreService) {
		this.articleAVendreService = articleAVendreService;
	}

	/**
	 * Méthode pour activer et clôturer les enchères des articles dont la date de
	 * début ou de fin est aujourd'hui.
	 */
	@Transactional
	@Scheduled(cron = "0 10 9 * * *")
	void activerEnchere() {
		articleAVendreService.activerVente();
		articleAVendreService.cloturerVente();
		System.out.println("Activation et clôture des enchères du jour réussies.");
	}

}
