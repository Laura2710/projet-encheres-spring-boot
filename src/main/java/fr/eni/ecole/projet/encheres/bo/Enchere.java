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
	private Utilisateur utilisateur;
	//Association avec l'artileAVendre
	@NotNull
	private ArticleAVendre articleAVendre;
	@NotNull
	private LocalDateTime date;
	@NotNull
	@Min(value = 1)
	private int montant;
	
	public Enchere() {
		
	}

	

	public Enchere(Utilisateur utilisateur, ArticleAVendre articleAVendre,
			LocalDateTime date, int montant) {
		super();
		this.utilisateur = utilisateur;
		this.articleAVendre = articleAVendre;
		this.date = date;
		this.montant = montant;
	}



	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public ArticleAVendre getArticleAVendre() {
		return articleAVendre;
	}

	public void setArticleAVendre(ArticleAVendre articleAVendre) {
		this.articleAVendre = articleAVendre;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
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
		builder.append("Enchere [date=");
		builder.append(date);
		builder.append(", articleAVendre=");
		builder.append(articleAVendre);
		builder.append(", utilisateur=");
		builder.append(utilisateur);
		builder.append(", montant=");
		builder.append(montant);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(articleAVendre, date, montant, utilisateur);
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
		return Objects.equals(articleAVendre, other.articleAVendre) && Objects.equals(date, other.date)
				&& montant == other.montant && Objects.equals(utilisateur, other.utilisateur);
	}
	
	
	

}
