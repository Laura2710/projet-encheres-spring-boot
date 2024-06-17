package fr.eni.ecole.projet.encheres.configuration;

import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.ecole.projet.encheres.bll.ArticleAVendreService;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;

@Component
@EnableScheduling
public class EnchereManager {

	ArticleAVendreService articleAVendreService;

	public EnchereManager(ArticleAVendreService articleAVendreService) {
		this.articleAVendreService = articleAVendreService;
	}
	
	/**
	 * Méthode pour activer les enchères des articles dont la date de début est aujourd'hui.
	 */
	@Transactional
    @Scheduled(cron = "0 0 2 * * *")  //  signifie "tous les jours à 2h00 du matin"
    //@Scheduled(cron = "*/10 * * * * *")  //  signifie "toutes les 10 secondes"
	void activerEnchere() {
		try {
			// Récupérer la liste des articles qui ont le statut 0
			List<ArticleAVendre> articlesAVendre = articleAVendreService.getVentesNonCommencees();

			// Activer les enchères 
			if (!articlesAVendre.isEmpty()) {
				articlesAVendre.forEach(a -> {
					articleAVendreService.activerVente(a.getId());
				});
				System.out.println("Activation des enchères du jour");
			}
		} catch (BusinessException e) {
			e.getClefsExternalisations().forEach(key -> {
				System.out.println("Une erreur est survenue: " + key);
			});
		}
	}

	/**
	 * Méthode pour clôturer les enchères dont la date de fin est aujourd'hui.
	 */
	@Transactional
    @Scheduled(cron = "0 0 2 * * *")  //  signifie "tous les jours à 2h00 du matin"
    //@Scheduled(cron = "*/10 * * * * *")  //  signifie "toutes les 10 secondes"
	protected void cloturerEnchere() {
		try {
			// Récupérer la liste des articles qui ont le statut 1
			List<ArticleAVendre> articlesAVendre = articleAVendreService.getVentesTerminees();

			// Clôturer les enchères du jour
			if (!articlesAVendre.isEmpty()) {
				articlesAVendre.forEach(a -> {
					articleAVendreService.cloturerVente(a.getId());
				});
				System.out.println("Clôturer les enchères du jour");
			}
		} catch (BusinessException e) {
			e.getClefsExternalisations().forEach(key -> {
				System.out.println("Une erreur est survenue: " + key);
			});
		}
	}
}
