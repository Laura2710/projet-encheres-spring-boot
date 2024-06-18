package fr.eni.ecole.projet.encheres.configuration;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.eni.ecole.projet.encheres.bll.ArticleAVendreService;
import fr.eni.ecole.projet.encheres.bo.ArticleAVendre;

@Component
@EnableScheduling
public class EnchereActivationManager {

	ArticleAVendreService articleAVendreService;

	public EnchereActivationManager(ArticleAVendreService articleAVendreService) {
		this.articleAVendreService = articleAVendreService;
	}

	// cron : secondes, minutes, heures, jour du mois, mois, jour de la semaine
	// @Scheduled(cron = "0 0 0 * * *") // exécution quotidienne à minuit
	// @Scheduled(cron = "*/10 * * * * *") // exécution toutes les 10 secondes
	private void activerVente() {
		System.out.println("Activer les ventes du jour");
		LocalDate today = LocalDate.now();
		List<ArticleAVendre> articlesAVendre = articleAVendreService.getVentesNonCommencees();
		List<ArticleAVendre> articlesDuJour = articlesAVendre	.stream()
																.filter(a -> a.getDateDebutEncheres().equals(today))
																.collect(Collectors.toList());

		articlesDuJour.forEach(a -> {
			articleAVendreService.activerVente(a.getId());
		});
		;
	}

}
