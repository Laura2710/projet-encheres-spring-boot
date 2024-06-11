package fr.eni.ecole.projet.encheres.bo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Enchere implements Serializable{
	private static final long serialVersionUID = 1L;
	//Association avec l'acquereur
	@NotNull
	private Utilisateur acquereur;
	//Association avec l'articleAVendre
	@NotNull
	private ArticleAVendre articleAVendre;
	@NotNull
	private LocalDateTime dateEnchere;
	@NotNull
	@Min(value = 1)
	private int montant;
	
	public Enchere() {
		
	}

	

	public Enchere(Utilisateur acquereur, ArticleAVendre articleAVendre,
			LocalDateTime dateEnchere, int montant) {
		super();
		this.acquereur = acquereur;
		this.articleAVendre = articleAVendre;
		this.dateEnchere = dateEnchere;
		this.montant = montant;
	}



	public LocalDateTime getDateEnchere() {
		return dateEnchere;
	}

	public void setDateEnchere(LocalDateTime dateEnchere) {
		this.dateEnchere = dateEnchere;
	}

	public ArticleAVendre getArticleAVendre() {
		return articleAVendre;
	}

	public void setArticleAVendre(ArticleAVendre articleAVendre) {
		this.articleAVendre = articleAVendre;
	}

	public Utilisateur getAcquereur() {
		return acquereur;
	}

	public void setAcquereur(Utilisateur acquereur) {
		this.acquereur = acquereur;
	}

	public int getMontant() {
		return montant;
	}

	public void setMontant(int montant) {
		this.montant = montant;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Enchere [dateEnchere=");
		builder.append(dateEnchere);
		builder.append(", articleAVendre=");
		builder.append(articleAVendre);
		builder.append(", acquereur=");
		builder.append(acquereur);
		builder.append(", montant=");
		builder.append(montant);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(articleAVendre, dateEnchere, montant, acquereur);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Enchere other = (Enchere) obj;
		return Objects.equals(articleAVendre, other.articleAVendre) && Objects.equals(dateEnchere, other.dateEnchere)
				&& montant == other.montant && Objects.equals(acquereur, other.acquereur);
	}
	
	
	

}
