package fr.eni.ecole.projet.encheres.bll;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.ecole.projet.encheres.bll.utils.RegexUtils;
import fr.eni.ecole.projet.encheres.bo.Adresse;
import fr.eni.ecole.projet.encheres.bo.Utilisateur;
import fr.eni.ecole.projet.encheres.dal.AdresseDAO;
import fr.eni.ecole.projet.encheres.dal.UtilisateurDAO;
import fr.eni.ecole.projet.encheres.exceptions.BusinessCode;
import fr.eni.ecole.projet.encheres.exceptions.BusinessException;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

	PasswordEncoder passwordEncoder;
	UtilisateurDAO utilisateurDAO;
	AdresseDAO adresseDAO;

	public UtilisateurServiceImpl(PasswordEncoder passwordEncoder, UtilisateurDAO utilisateurDAO,
			AdresseDAO adresseDAO) {
		this.passwordEncoder = passwordEncoder;
		this.utilisateurDAO = utilisateurDAO;
		this.adresseDAO = adresseDAO;
	}

	/**
	 * Cette méthode récupère un utilisateur en fonction de son pseudo.
	 *
	 * @param pseudo Le pseudo de l'utilisateur à récupérer.
	 * @return L'utilisateur correspondant au pseudo fourni.
	 */
	@Override
	public Utilisateur getByPseudo(String pseudo) {
		return this.utilisateurDAO.getByPseudo(pseudo);
	}

	/**
	 * Cette méthode récupère une adresse en fonction de son identifiant.
	 *
	 * @param id L'identifiant de l'adresse à récupérer.
	 * @return L'adresse correspondant à l'identifiant fourni.
	 */
	@Override
	public Adresse getAdresseByID(long id) {
		return this.adresseDAO.getByID(id);
	}

	/**
	 * Cette méthode récupère les informations complètes d'un utilisateur, y compris
	 * son adresse, en fonction de son pseudo.
	 *
	 * @param pseudo Le pseudo de l'utilisateur dont les informations doivent être
	 *               récupérées.
	 * @return L'utilisateur avec ses informations complètes, y compris l'adresse.
	 */
	@Override
	public Utilisateur getInfoUtilisateur(String pseudo) {
		Utilisateur utilisateur = this.getByPseudo(pseudo);
		Adresse adresse = this.getAdresseByID(utilisateur.getAdresse().getId());
		utilisateur.setAdresse(adresse);
		return utilisateur;
	}

	/**
	 * Cette méthode permet de créer un compte utilisateur. Elle vérifie les informations de l'utilisateur
	 * avant de les enregistrer dans la base de données. Si les validations échouent ou si le pseudo ou
	 * l'email existe déjà, une BusinessException est levée.
	 *
	 * @param utilisateur L'utilisateur dont le compte doit être créé.
	 * @throws BusinessException Si les informations de l'utilisateur ne sont pas valides ou si le pseudo ou l'email existe déjà.
	 */
	@Transactional
	@Override
	public void creerUnCompte(Utilisateur utilisateur) {
		BusinessException be = new BusinessException();
		boolean isValid = true;

		isValid &= this.verifierPseudo(utilisateur.getPseudo(), be);
		isValid &= this.verifierNom(utilisateur.getNom(), be);
		isValid &= this.verifierPrenom(utilisateur.getPrenom(), be);
		isValid &= this.verifierEmail(utilisateur.getEmail(), be);
		isValid &= this.verifierTelephone(utilisateur.getTelephone(), be);
		isValid &= this.verifierRue(utilisateur.getAdresse().getRue(), be);
		isValid &= this.verifierCodePostal(utilisateur.getAdresse().getCodePostal(), be);
		isValid &= this.verifierVille(utilisateur.getAdresse().getVille(), be);
		isValid &= this.verifierMotPasse(utilisateur.getMotDePasse(), be);
		if (isValid) {
			// Chercher si le pseudo existe déjà
			boolean pseudoExist = this.utilisateurDAO.findPseudo(utilisateur.getPseudo());
			// Chercher si l'email existe déjà
			boolean emailExist = this.utilisateurDAO.findEmail(utilisateur.getEmail());

			if (pseudoExist || emailExist) {
				be.add(BusinessCode.VALIDATION_DAL_AJOUT_UTILISATEUR_EXISTANT);
				throw be;
			} else {
				this.adresseDAO.addAdresse(utilisateur.getAdresse());
				String hashedPassword = passwordEncoder.encode(utilisateur.getMotDePasse());
				utilisateur.setMotDePasse(hashedPassword);
				int nbrLigne = this.utilisateurDAO.addUtilisateur(utilisateur);

				if (nbrLigne == 0) {
					be.add(BusinessCode.VALIDATION_DAL_AJOUT_UTILISATEUR);
					throw be;
				}
			}

		} else {
			throw be;
		}

	}
	
	
	/**
	 * Cette méthode vérifie si un mot de passe est valide. Le mot de passe ne doit pas être vide, 
	 * doit avoir une longueur comprise entre 8 et 20 caractères, contenir au moins un chiffre, 
	 * une majuscule, un caractère spécial et ne doit pas contenir d'espace. Si l'une de ces conditions 
	 * n'est pas remplie, elle ajoute un code d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param motDePasse Le mot de passe à vérifier.
	 * @param be L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le mot de passe est valide, false sinon.
	 */
	private boolean verifierMotPasse(String motDePasse, BusinessException be) {
		if (motDePasse.isBlank()) {
			be.add(BusinessCode.VALIDATION_MDP_BLANK);
			return false;
		}
		if (motDePasse.length() < 8 || motDePasse.length() > 20) {
			be.add(BusinessCode.VALIDATION_MDP_SIZE);
			return false;
		}
		if (!RegexUtils.hasNumber(motDePasse)) {
			be.add(BusinessCode.VALIDATION_MDP_CHIFFRE);
			return false;
		}
		if (!RegexUtils.hasMajuscule(motDePasse)) {
			be.add(BusinessCode.VALIDATION_MDP_MAJUSCULE);
			return false;
		}
		if (!RegexUtils.hasSpecialChar(motDePasse)) {
			be.add(BusinessCode.VALIDATION_MDP_SPECIAL_CHAR);
			return false;
		}
		if (!RegexUtils.hasNoSpace(motDePasse)) {
			be.add(BusinessCode.VALIDATION_MDP_NOESPACE);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si une ville est valide. La ville ne doit pas être vide, doit avoir une 
	 * longueur comprise entre 3 et 50 caractères et doit respecter un format valide. Si l'une de ces 
	 * conditions n'est pas remplie, elle ajoute un code d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param ville Le nom de la ville à vérifier.
	 * @param be L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si la ville est valide, false sinon.
	 */
	private boolean verifierVille(String ville, BusinessException be) {
		if (ville.isBlank()) {
			be.add(BusinessCode.VALIDATION_VILLE_BLANK);
			return false;
		}
		if (ville.length() < 3 || ville.length() > 50) {
			be.add(BusinessCode.VALIDATION_VILLE_SIZE);
			return false;
		}
		if (!RegexUtils.isVille(ville)) {
			be.add(BusinessCode.VALIDATION_VILLE_FORMAT);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si un code postal est valide. Le code postal ne doit pas être vide, 
	 * doit avoir une longueur de 5 caractères et doit respecter un format valide. Si l'une de ces 
	 * conditions n'est pas remplie, elle ajoute un code d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param codePostal Le code postal à vérifier.
	 * @param be L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le code postal est valide, false sinon.
	 */
	private boolean verifierCodePostal(String codePostal, BusinessException be) {
		if (codePostal.isBlank()) {
			be.add(BusinessCode.VALIDATION_CODEPOSTAL_BLANK);
			return false;
		}
		if (codePostal.length() != 5) {
			be.add(BusinessCode.VALIDATION_CODEPOSTAL_SIZE);
			return false;
		}
		if (!RegexUtils.isCodePostal(codePostal)) {
			be.add(BusinessCode.VALIDATION_CODEPOSTAL_FORMAT);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si la rue est valide. La rue ne doit pas être vide, doit avoir une longueur 
	 * comprise entre 3 et 100 caractères et doit respecter un format alphanumérique étendu. Si l'une de ces 
	 * conditions n'est pas remplie, elle ajoute un code d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param rue La rue à vérifier.
	 * @param be L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si la rue est valide, false sinon.
	 */
	private boolean verifierRue(String rue, BusinessException be) {
		if (rue.isBlank()) {
			be.add(BusinessCode.VALIDATION_RUE_BLANK);
			return false;
		}
		if (rue.length() < 3 || rue.length() > 100) {
			be.add(BusinessCode.VALIDATION_RUE_SIZE);
			return false;
		}
		if (!RegexUtils.isAlphanumericExtended(rue)) {
			be.add(BusinessCode.VALIDATION_RUE_FORMAT);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si le numéro de téléphone est valide. Le téléphone peut être vide, mais s'il est 
	 * fourni, il doit avoir une longueur comprise entre 10 et 15 caractères et respecter un format de 
	 * numéro de téléphone. Si l'une de ces conditions n'est pas remplie, elle ajoute un code d'erreur à 
	 * l'objet BusinessException et retourne false.
	 *
	 * @param telephone Le numéro de téléphone à vérifier.
	 * @param be L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le numéro de téléphone est valide ou vide, false sinon.
	 */
	private boolean verifierTelephone(String telephone, BusinessException be) {
		if (telephone.isBlank()) {
			return true;
		}
		if (telephone.length() < 10 || telephone.length() > 15) {
			be.add(BusinessCode.VALIDATION_TELEPHONE_SIZE);
			return false;
		}
		if (!RegexUtils.isTelephone(telephone)) {
			be.add(BusinessCode.VALIDATION_TELEPHONE_FORMAT);
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si l'email est valide. L'email ne doit pas être vide, doit avoir une longueur 
	 * comprise entre 5 et 100 caractères et doit respecter un format d'adresse email. Si l'une de ces 
	 * conditions n'est pas remplie, elle ajoute un code d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param email L'adresse email à vérifier.
	 * @param be L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si l'email est valide, false sinon.
	 */
	private boolean verifierEmail(String email, BusinessException be) {
		if (email.isBlank()) {
			be.add(BusinessCode.VALIDATION_EMAIL_BLANK);
			return false;
		}
		if (email.length() < 5 || email.length() > 100) {
			be.add(BusinessCode.VALIDATION_EMAIL_SIZE);
			return false;
		}
		if (!RegexUtils.isEmail(email)) {
			be.add(BusinessCode.VALIDATION_EMAIL_FORMAT);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si le prénom est valide. Le prénom ne doit pas être vide, doit avoir une longueur 
	 * comprise entre 2 et 50 caractères et ne doit contenir que des lettres. Si l'une de ces conditions n'est 
	 * pas remplie, elle ajoute un code d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param prenom Le prénom à vérifier.
	 * @param be L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le prénom est valide, false sinon.
	 */
	private boolean verifierPrenom(String prenom, BusinessException be) {
		if (prenom.isBlank()) {
			be.add(BusinessCode.VALIDATION_PRENOM_BLANK);
			return false;
		}
		if (prenom.length() < 2 || prenom.length() > 50) {
			be.add(BusinessCode.VALIDATION_PRENOM_SIZE);
			return false;
		}
		if (!RegexUtils.onlyLetters(prenom)) {
			be.add(BusinessCode.VALIDATION_PRENOM_FORMAT);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si le nom est valide. Le nom ne doit pas être vide, doit avoir une longueur 
	 * comprise entre 2 et 40 caractères et ne doit contenir que des lettres. Si l'une de ces conditions n'est 
	 * pas remplie, elle ajoute un code d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param nom Le nom à vérifier.
	 * @param be L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le nom est valide, false sinon.
	 */
	private boolean verifierNom(String nom, BusinessException be) {
		if (nom.isBlank()) {
			be.add(BusinessCode.VALIDATION_NOM_BLANK);
			return false;
		}
		if (nom.length() < 2 || nom.length() > 40) {
			be.add(BusinessCode.VALIDATION_NOM_SIZE);
			return false;
		}
		if (!RegexUtils.onlyLetters(nom)) {
			be.add(BusinessCode.VALIDATION_NOM_FORMAT);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode vérifie si le pseudo est valide. Le pseudo ne doit pas être vide, doit avoir une longueur 
	 * comprise entre 8 et 30 caractères et doit respecter un format alphanumérique. Si l'une de ces conditions 
	 * n'est pas remplie, elle ajoute un code d'erreur à l'objet BusinessException et retourne false.
	 *
	 * @param pseudo Le pseudo à vérifier.
	 * @param be L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si le pseudo est valide, false sinon.
	 */
	private boolean verifierPseudo(String pseudo, BusinessException be) {
		if (pseudo.isBlank()) {
			be.add(BusinessCode.VALIDATION_PSEUDO_BLANK);
			return false;
		}
		if (pseudo.length() < 8 || pseudo.length() > 30) {
			be.add(BusinessCode.VALIDATION_PSEUDO_SIZE);
			return false;
		}
		if (!RegexUtils.isAlphanumeric(pseudo)) {
			be.add(BusinessCode.VALIDATION_PSEUDO_FORMAT);
			return false;
		}
		return true;
	}

	/**
	 * Cette méthode permet de mettre à jour le profil d'un utilisateur. Elle valide les informations
	 * fournies avant de les enregistrer dans la base de données. Si les validations échouent, une 
	 * BusinessException est levée.
	 *
	 * @param utilisateur L'utilisateur dont le profil doit être mis à jour.
	 * @param pseudo Le pseudo de l'utilisateur.
	 * @throws BusinessException Si les informations de l'utilisateur ne sont pas valides ou si une erreur survient lors de la mise à jour.
	 */
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void miseAjourProfil(Utilisateur utilisateur, String pseudo) {
		BusinessException be = new BusinessException();
		boolean isValid = true;

		isValid &= verifierNom(utilisateur.getNom(), be);
		isValid &= verifierPrenom(utilisateur.getPrenom(), be);
		isValid &= verifierEmail(utilisateur.getEmail(), be);
		isValid &= verifierTelephone(utilisateur.getTelephone(), be);
		isValid &= verifierRue(utilisateur.getAdresse().getRue(), be);
		isValid &= verifierCodePostal(utilisateur.getAdresse().getCodePostal(), be);
		isValid &= verifierVille(utilisateur.getAdresse().getVille(), be);

		if (isValid) {

			utilisateur.setPseudo(pseudo);
			int count = this.utilisateurDAO.updateProfil(utilisateur);

			// utilisateur.getAdresse().setRue(null);
			count += this.adresseDAO.updateAdresse(utilisateur.getAdresse());

			if (count != 2) {
				be.add(BusinessCode.VALIDATION_UPDATE_PROFIL_ERROR);
				throw be;
			}

		} else {
			throw be;
		}

	}

	/**
	 * Cette méthode permet de mettre à jour le mot de passe d'un utilisateur. Elle vérifie la validité 
	 * du nouveau mot de passe, la validité de l'ancien mot de passe, et la correspondance de l'ancien 
	 * mot de passe avec celui stocké en base de données. Si les validations échouent, une BusinessException 
	 * est levée.
	 *
	 * @param ancienMotDePasse L'ancien mot de passe de l'utilisateur.
	 * @param nouveauMotDePasse Le nouveau mot de passe de l'utilisateur.
	 * @param utilisateur L'utilisateur dont le mot de passe doit être mis à jour.
	 * @throws BusinessException Si les informations de mot de passe ne sont pas valides ou si une erreur survient lors de la mise à jour.
	 */
	@Override
	public void updateMotDePasse(String ancienMotDePasse, String nouveauMotDePasse, Utilisateur utilisateur) {
		BusinessException be = new BusinessException();
		boolean isValid = true;
		isValid &= verifierMotPasse(nouveauMotDePasse, be);
		isValid &= verifierMotPasse(ancienMotDePasse, be);
		isValid &= verifierCorrespondanceAncienMotPasse(ancienMotDePasse, utilisateur.getPseudo(), be);
		if (isValid) {
			String hashedPassword = passwordEncoder.encode(nouveauMotDePasse);
			this.utilisateurDAO.updateMdp(utilisateur.getPseudo(), hashedPassword);
		} else {
			throw be;
		}

	}

	/**
	 * Cette méthode vérifie si l'ancien mot de passe fourni correspond à celui stocké en base de données 
	 * pour un utilisateur donné. Si les mots de passe ne correspondent pas, elle ajoute un code d'erreur à 
	 * l'objet BusinessException et retourne false.
	 *
	 * @param ancienMotDePasse L'ancien mot de passe à vérifier.
	 * @param pseudo Le pseudo de l'utilisateur.
	 * @param be L'objet BusinessException à compléter en cas d'erreur.
	 * @return true si l'ancien mot de passe correspond, false sinon.
	 */
	private boolean verifierCorrespondanceAncienMotPasse(String ancienMotDePasse, String pseudo, BusinessException be) {
		String hashInBdd = this.utilisateurDAO.findOldPwd(pseudo);
		String hashSanitize = hashInBdd.substring(8);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		boolean isSame = encoder.matches(ancienMotDePasse, hashSanitize);
		if (!isSame) {
			be.add(BusinessCode.VALIDATION_PROFIL_ANCIEN_MDP);
			return false;
		}
		return true;
	}

}
