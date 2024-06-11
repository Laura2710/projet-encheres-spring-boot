package fr.eni.ecole.projet.encheres.bo;

import java.io.Serializable;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Adresse implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	@NotBlank(message = "La rue doit être renseignée")
	@Size(min = 3, max = 100, message = "Rue doit contenir entre 3 et 100 caractères")
	@Pattern(regexp = "^[0-9A-Za-zÀ-ÿ, °-]+$", message = "La rue contient des caractères interdits")
	private String rue;
	
	@NotBlank(message = "Le code postal doit être renseigné")
    @Pattern(regexp = "^[0-9]{5}$", message = "Le code postal doit contenir exactement 5 chiffres")
	private String codePostal;
	
	@NotBlank(message = "La ville doit être renseignée")
    @Size(min = 3, max = 50, message = "La ville doit contenir entre 3 et 50 caractères")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ]+(?:[ '-][A-Za-zÀ-ÿ]+)*$", message = "La ville doit contenir uniquement des lettres")
	private String ville;
	private boolean isAdresseEni;

	public Adresse() {
	}

	public Adresse(long id, String rue, String codePostal, String ville, boolean isAdresseEni) {
		this.id = id;
		this.rue = rue;
		this.codePostal = codePostal;
		this.ville = ville;
		this.isAdresseEni = isAdresseEni;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRue() {
		return rue;
	}

	public void setRue(String rue) {
		this.rue = rue;
	}

	public String getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public boolean isAdresseEni() {
		return isAdresseEni;
	}

	public void setAdresseEni(boolean isAdresseEni) {
		this.isAdresseEni = isAdresseEni;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Adresse [id=");
		builder.append(id);
		builder.append(", rue=");
		builder.append(rue);
		builder.append(", codePostal=");
		builder.append(codePostal);
		builder.append(", ville=");
		builder.append(ville);
		builder.append(", isAdresseEni=");
		builder.append(isAdresseEni);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(codePostal, id, isAdresseEni, rue, ville);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Adresse other = (Adresse) obj;
		return Objects.equals(codePostal, other.codePostal) && id == other.id && isAdresseEni == other.isAdresseEni
				&& Objects.equals(rue, other.rue) && Objects.equals(ville, other.ville);
	}

}
