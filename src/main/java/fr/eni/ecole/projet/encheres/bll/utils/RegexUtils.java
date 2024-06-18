package fr.eni.ecole.projet.encheres.bll.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

	public static boolean onlyLetters(String chaine) {
		String regex = "^[a-zA-ZÀ-ÿ-]+$";
		return callMatcher(regex, chaine);
	}

	public static boolean isAlphanumeric(String chaine) {
		String regex = "^[a-zA-Z0-9_]+$";
		return callMatcher(regex, chaine);
	}
	
	public static boolean isCodePostal(String cp) {
		String regex = "^[0-9]{5}$";
		return callMatcher(regex, cp);
	}
	
	public static boolean isVille(String ville) {
		String regex = "^[A-Za-zÀ-ÿ]+(?:[ '-][A-Za-zÀ-ÿ]+)*$";
		return callMatcher(regex, ville);
	}
	
	public static boolean isTelephone(String tel) {
        final String regex = "^0[1-9]([\\. -]?[0-9]{2}){4}$";
        return callMatcher(regex, tel);
	}
	
	public static boolean isEmail(String email) {
        String regex =  "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9\\-]+(\\.[a-zA-Z]{2,})+$";
        return callMatcher(regex, email);
	}
	
	public static boolean isAlphanumericExtended(String chaine) {
        String regex = "^[0-9A-Za-zÀ-ÿ, °-]+$";
		return callMatcher(regex, chaine);
	}
		
	
	public static boolean hasNumber(String chaine) {
		String regex = "\\d";		
		return callMatcher(regex,chaine);
	}

	public static boolean hasMajuscule(String chaine) {
		String regex = "[A-Z]";
		return callMatcher(regex,chaine);
	}

	public static boolean hasSpecialChar(String chaine) {
		String regex = "[!@#$%^&\\-_=+]";
		return callMatcher(regex,chaine);
	}

	public static boolean hasNoSpace(String chaine) {
	    String regex = "^[^\\s]+$";
	    return callMatcher(regex, chaine);
	}
	
	private static boolean callMatcher(String regex, String chaine) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(chaine);
		if (!m.find()) {
			return false;
		}
		return true;	
	}



}
