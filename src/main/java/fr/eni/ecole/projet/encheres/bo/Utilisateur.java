package fr.eni.ecole.projet.encheres.bo;

import java.io.Serializable;
import java.util.Objects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Utilisateur implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank(message = "Le pseudo doit être renseigné")
	@Size(min = 8, max = 30, message = "Le pseudo doit contenir entre 8 et 30 caractères")
	@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Le pseudo ne peut contenir que des caractères alphanumériques et des underscores ('_')")
	private String pseudo;
	
	@NotBlank(message = "Le nom doit être renseigné")
    @Size(min = 2, max = 40, message = "Le nom doit contenir entre 2 et 40 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ]+$", message = "Le nom doit contenir uniquement des lettres")
	private String nom;
	
	@NotBlank(message = "Le prénom doit être renseigné")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ]+$", message = "Le prénom doit contenir uniquement des lettres")
	private String prenom;
	
	@NotBlank(message = "L'email doit être renseigné")
    @Size(min = 5, max = 100, message = "L'email doit c entre 5 et 100 caractères")
    @Email(message = "L'email n'a pas un format valide")
	private String email;
	
	private String telephone;
	
	@NotBlank(message = "Le mot de passe doit être renseigné")
	@Size(min = 8, max = 20, message = "Le mot de passe doit contenir entre 8 et 20 caractères")
	private String motDePasse;
	private int credit;
	private boolean administrateur;

	@Valid
	private Adresse adresse;

	public Utilisateur() {
	}

	public Utilisateur(String pseudo, String nom, String prenom, String email, String telephone, String motDePasse,
			int credit, boolean administrateur, Adresse adresse) {
		this.pseudo = pseudo;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.telephone = telephone;
		this.motDePasse = motDePasse;
		this.credit = credit;
		this.administrateur = administrateur;
		this.adresse = adresse;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public boolean isAdministrateur() {
		return administrateur;
	}

	public void setAdministrateur(boolean administrateur) {
		this.administrateur = administrateur;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Utilisateur [pseudo=");
		builder.append(pseudo);
		builder.append(", nom=");
		builder.append(nom);
		builder.append(", prenom=");
		builder.append(prenom);
		builder.append(", email=");
		builder.append(email);
		builder.append(", telephone=");
		builder.append(telephone);
		builder.append(", credit=");
		builder.append(credit);
		builder.append(", administrateur=");
		builder.append(administrateur);
		builder.append(", adresse=");
		builder.append(adresse);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, motDePasse, pseudo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Utilisateur other = (Utilisateur) obj;
		return Objects.equals(email, other.email) && Objects.equals(motDePasse, other.motDePasse)
				&& Objects.equals(pseudo, other.pseudo);
	}

}
