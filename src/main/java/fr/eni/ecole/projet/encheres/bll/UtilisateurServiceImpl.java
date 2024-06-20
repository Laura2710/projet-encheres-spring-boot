package fr.eni.ecole.projet.encheres.bll;

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

	@Override
	public Utilisateur getByPseudo(String pseudo) {
		return this.utilisateurDAO.getByPseudo(pseudo);
	}

	@Override
	public Adresse getAdresseByID(long id) {
		return this.adresseDAO.getByID(id);
	}

	@Override
	public Utilisateur getInfoUtilisateur(String pseudo) {
		Utilisateur utilisateur= this.getByPseudo(pseudo);
		Adresse adresse= this.getAdresseByID(utilisateur.getAdresse().getId());
		System.out.println(adresse);
		utilisateur.setAdresse(adresse);
		return utilisateur;
	} 
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
				}
				else {
					this.adresseDAO.addAdresse(utilisateur.getAdresse());
					String hashedPassword = passwordEncoder.encode(utilisateur.getMotDePasse());
					utilisateur.setMotDePasse(hashedPassword);
					int nbrLigne = this.utilisateurDAO.addUtilisateur(utilisateur);
					
					if (nbrLigne == 0) {
						be.add(BusinessCode.VALIDATION_DAL_AJOUT_UTILISATEUR);
						throw be;					
					}
				}
			
		}
		else {
			throw be;
		}
		
	}

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

	private boolean verifierTelephone(String telephone, BusinessException be) {
		if (telephone.isBlank()) {
			return true;
		}
		if (telephone.length() < 10 || telephone.length() > 15) {
			be.add(BusinessCode.VALIDATION_TELEPHONE_SIZE);
			return false;
		}
		if(!RegexUtils.isTelephone(telephone)) {
			be.add(BusinessCode.VALIDATION_TELEPHONE_FORMAT);
		}
		return true;
	}

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
			
			//utilisateur.getAdresse().setRue(null);
			count += this.adresseDAO.updateAdresse(utilisateur.getAdresse());
			
			if (count != 2) {
				be.add(BusinessCode.VALIDATION_UPDATE_PROFIL_ERROR);
				throw be;
			}
			
		}
		else {
			throw be;
		}
		
		
	}

	@Override
	public void updateMotDePasse(String ancienMotDePasse, String nouveauMotDePasse, Utilisateur utilisateur) {
		BusinessException be=new BusinessException();
		boolean isValid = true;
		isValid &= verifierMotPasse(nouveauMotDePasse, be);
		isValid &= verifierMotPasse(ancienMotDePasse, be);
		if(isValid) {
			int count= this.utilisateurDAO.findOldPwd(ancienMotDePasse, utilisateur.getPseudo());
			
		}
		else {
			throw be;
		}
		
	}

}
