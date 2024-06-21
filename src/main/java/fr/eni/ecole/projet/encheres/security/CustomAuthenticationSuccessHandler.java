package fr.eni.ecole.projet.encheres.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                  Authentication authentication) throws IOException, ServletException, ServletException {
        request.getSession().setMaxInactiveInterval(300); // Set timeout to 5 minutes (in seconds)
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
