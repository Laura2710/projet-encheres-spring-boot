package fr.eni.ecole.projet.encheres.bo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ArticleAVendre implements Serializable{
	private static final long serialVersionUID = 1L;
	private long id;
	//Association avec une catégorie
	@NotNull
	private Categorie categorie;
	@NotBlank
	@Size(min = 5, max = 30)
	private String nom;
	@NotBlank
	@Size(min = 20, max = 300)
	private String description;
	@NotNull
	@Future
	private LocalDate dateDebutEncheres;
	@NotNull
	@Future
	private LocalDate dateFinEncheres;
	private int statut;
	@NotNull
	@Min(value = 1)
	private int prixInitial;
	private int prixVente;
	//Association avec un utilisateur - vendeur
	@NotNull
	private Utilisateur vendeur;
	//Association avec une adresse de retrait
	@NotNull
	private Adresse adresseRetrait;
	
	public ArticleAVendre() {
		
	}

	public ArticleAVendre(long id,Categorie categorie,String nom,
			String description,LocalDate dateDebutEncheres,
			LocalDate dateFinEncheres, int statut,int prixInitial, int prixVente,
			Utilisateur vendeur,Adresse adresseRetrait) {
		this.id = id;
		this.categorie = categorie;
		this.nom = nom;
		this.description = description;
		this.dateDebutEncheres = dateDebutEncheres;
		this.dateFinEncheres = dateFinEncheres;
		this.statut = statut;
		this.prixInitial = prixInitial;
		this.prixVente = prixVente;
		this.vendeur = vendeur;
		this.adresseRetrait = adresseRetrait;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getDateDebutEncheres() {
		return dateDebutEncheres;
	}

	public void setDateDebutEncheres(LocalDate dateDebutEncheres) {
		this.dateDebutEncheres = dateDebutEncheres;
	}

	public LocalDate getDateFinEncheres() {
		return dateFinEncheres;
	}

	public void setDateFinEncheres(LocalDate dateFinEncheres) {
		this.dateFinEncheres = dateFinEncheres;
	}

	public int getStatut() {
		return statut;
	}

	public void setStatut(int statut) {
		this.statut = statut;
	}

	public int getPrixInitial() {
		return prixInitial;
	}

	public void setPrixInitial(int prixInitial) {
		this.prixInitial = prixInitial;
	}

	public int getPrixVente() {
		return prixVente;
	}

	public void setPrixVente(int prixVente) {
		this.prixVente = prixVente;
	}

	public Utilisateur getVendeur() {
		return vendeur;
	}

	public void setVendeur(Utilisateur vendeur) {
		this.vendeur = vendeur;
	}

	public Adresse getAdresseRetrait() {
		return adresseRetrait;
	}

	public void setAdresseRetrait(Adresse adresseRetrait) {
		this.adresseRetrait = adresseRetrait;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArticleAVendre [id=");
		builder.append(id);
		builder.append(", categorie=");
		builder.append(categorie);
		builder.append(", nom=");
		builder.append(nom);
		builder.append(", description=");
		builder.append(description);
		builder.append(", dateDebutEncheres=");
		builder.append(dateDebutEncheres);
		builder.append(", dateFinEncheres=");
		builder.append(dateFinEncheres);
		builder.append(", statut=");
		builder.append(statut);
		builder.append(", prixInitial=");
		builder.append(prixInitial);
		builder.append(", prixVente=");
		builder.append(prixVente);
		builder.append(", vendeur=");
		builder.append(vendeur);
		builder.append(", adresseRetrait=");
		builder.append(adresseRetrait);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(categorie, dateDebutEncheres, dateFinEncheres, id, nom, prixInitial, prixVente, statut,
				vendeur);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArticleAVendre other = (ArticleAVendre) obj;
		return Objects.equals(categorie, other.categorie) && Objects.equals(dateDebutEncheres, other.dateDebutEncheres)
				&& Objects.equals(dateFinEncheres, other.dateFinEncheres) && id == other.id
				&& Objects.equals(nom, other.nom) && prixInitial == other.prixInitial && prixVente == other.prixVente
				&& statut == other.statut && Objects.equals(vendeur, other.vendeur);
	}
	
	
	

}
