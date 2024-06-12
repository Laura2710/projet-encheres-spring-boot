package fr.eni.ecole.projet.encheres.exceptions;

public class BusinessCode {
	public static final String VALIDATION_UTILISATEUR_ADMIN = "validation.utilisateur.admin";
	
	public static final String VALIDATION_PSEUDO_BLANK = "validation.utilisateur.pseudo.blank";
	public static final String VALIDATION_PSEUDO_SIZE = "validation.utilisateur.pseudo.size";
	public static final String VALIDATION_PSEUDO_FORMAT = "validation.utilisateur.pseudo.format";
	
	public static final String VALIDATION_NOM_BLANK = "validation.utilisateur.nom.blank";
	public static final String VALIDATION_NOM_SIZE = "validation.utilisateur.nom.size";
	public static final String VALIDATION_NOM_FORMAT = "validation.utilisateur.nom.format";
	
	public static final String VALIDATION_PRENOM_BLANK = "validation.utilisateur.prenom.blank";
	public static final String VALIDATION_PRENOM_SIZE = "validation.utilisateur.prenom.size";
	public static final String VALIDATION_PRENOM_FORMAT = "validation.utilisateur.prenom.format";
	
	public static final String VALIDATION_EMAIL_BLANK = "validation.utilisateur.email.blank";
	public static final String VALIDATION_EMAIL_SIZE = "validation.utilisateur.email.size";
	public static final String VALIDATION_EMAIL_FORMAT = "validation.utilisateur.email.format";
	
	public static final String VALIDATION_TELEPHONE_SIZE = "validation.utilisateur.telephone.size";
	public static final String VALIDATION_TELEPHONE_FORMAT = "validation.utilisateur.telephone.format";
	
	public static final String VALIDATION_RUE_BLANK = "validation.utilisateur.rue.blank";
	public static final String VALIDATION_RUE_SIZE = "validation.utilisateur.rue.size";
	public static final String VALIDATION_RUE_FORMAT =  "validation.utilisateur.rue.format";
	
	public static final String VALIDATION_CODEPOSTAL_BLANK = "validation.utilisateur.cp.blank";
	public static final String VALIDATION_CODEPOSTAL_SIZE = "validation.utilisateur.cp.size";
	public static final String VALIDATION_CODEPOSTAL_FORMAT = "validation.utilisateur.cp.format";
	
	public static final String VALIDATION_VILLE_BLANK = "validation.utilisateur.ville.blank";
	public static final String VALIDATION_VILLE_SIZE = "validation.utilisateur.ville.size";
	public static final String VALIDATION_VILLE_FORMAT = "validation.utilisateur.ville.format";
	
	public static final String VALIDATION_MDP_BLANK = "validation.utilisateur.mdp.blank";
	public static final String VALIDATION_MDP_SIZE = "validation.utilisateur.mdp.size";
	public static final String VALIDATION_MDP_CHIFFRE = "validation.utilisateur.mdp.chiffre";
	public static final String VALIDATION_MDP_MAJUSCULE = "validation.utilisateur.mdp.majuscule";
	public static final String VALIDATION_MDP_SPECIAL_CHAR = "validation.utilisateur.mdp.special.char";
	public static final String VALIDATION_MDP_NOESPACE =  "validation.utilisateur.mdp.nospace";
	
	public static final String VALIDATION_DAL_AJOUT_UTILISATEUR = "validation.dal.ajout_utilisateur";
	
	public static final String VALIDATION_ARTICLE_A_VENDRE_NULL = "validation.article_a_vendre.null";
	public static final String VALIDATION_ARTICLE_A_VENDRE_NOM_NULL = "validation.article_a_vendre.nom.blank";
	public static final String VALIDATION_ARTICLE_A_VENDRE_DESCRIPTION_BLANK = "validation.article_a_vendre.description.blank";
	public static final String VALIDATION_ARTICLE_A_VENDRE_DESCRIPTION_LENGTH = "validation.article_a_vendre.description.blank";
	public static final String VALIDATION_ARTICLE_A_VENDRE_DATE_DEBUT_NULL = "validation.article_a_vendre.date_debut.null";
	public static final String VALIDATION_ARTICLE_A_VENDRE_DATE_DEBUT_PASSE = "validation.article_a_vendre.date_debut.passe";
	public static final String VALIDATION_ARTICLE_A_VENDRE_DATE_FIN_NULL = "validation.article_a_vendre.date_fin.null";
	public static final String VALIDATION_ARTICLE_A_VENDRE_DATE_FIN_PASSE = "validation.article_a_vendre.date_fin.passe";
	public static final String VALIDATION_ARTICLE_A_VENDRE_PRIX_INITIAL = "validation.article_a_vendre.prix_initial";
	public static final String VALIDATION_ARTICLE_A_VENDRE_ADRESSE_NULL = "validation.article_a_vendre.adresse_retrait.null";
	public static final String VALIDATION_ARTICLE_A_VENDRE_ADRESSE_INCONNU = "validation.article_a_vendre.adresse_retrait.id_inconnu";
	public static final String VALIDATION_ARTICLE_A_VENDRE_CATEGORIE_NULL = "validation.article_a_vendre.categorie.null";
	public static final String VALIDATION_ARTICLE_A_VENDRE_CATEGORIE_INCONNU = "validation.article_a_vendre.categorie.id_inconnu";
	
	public static final String VALIDATION_DAL_AJOUT_UTILISATEUR_EXISTANT = "validation.dal.ajout.utilisateur.existant";
	
	public static final String VALIDATION_OFFRE_DATE_DEBUT = "validation.offre.date.debut";
	public static final String VALIDATION_OFFRE_DATE_FIN = "validation.offre.date.fin";
	public static final String VALIDATION_OFFRE_MONTANT = "validation.offre.montant";
	public static final String VALIDATION_OFFRE_CREDIT = "validation.offre.credit";
	public static final String VALIDATION_OFFRE_AJOUT_ENCHERE = "validation.offre.ajout.enchere";
}
