package fr.eni.ecole.projet.encheres.configuration;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.ecole.projet.encheres.bll.ArticleAVendreService;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;

@Component
@EnableScheduling
public class EnchereManager {

	ArticleAVendreService articleAVendreService;

	public EnchereManager(ArticleAVendreService articleAVendreService) {
		this.articleAVendreService = articleAVendreService;
	}
	
	/**
	 * Méthode pour activer et clôturer les enchères des articles dont la date de début ou de fin est aujourd'hui.
	 */
	@Transactional
    @Scheduled(cron = "0 0 16 * * *")  //  signifie "tous les jours à 16h00"
    //@Scheduled(cron = "*/10 * * * * *")  //  signifie "toutes les 10 secondes"
	void activerEnchere() {
		try {		
			articleAVendreService.activerVente();
			articleAVendreService.cloturerVente();
			System.out.println("methode appelée");
		} catch (BusinessException e) {
			e.getClefsExternalisations().forEach(key -> {
				System.out.println("Une erreur est survenue: " + key);
			});
		}
	}

}
