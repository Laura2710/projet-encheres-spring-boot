package fr.eni.ecole.projet.encheres.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class EncheresSecurity {
	
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	// Authentification
	@Bean
	UserDetailsManager getUsersAndRoles(DataSource datasource) {
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(datasource);
		// Selectionner l'utilisateur
		String usersByUsernameQuery = "SELECT pseudo, mot_de_passe, 1 FROM utilisateurs WHERE pseudo = ?";
		// Selectionner le(s) role(s)
		String authoritiesByUsernameQuery = "SELECT pseudo, role FROM roles r INNER JOIN utilisateurs u ON u.administrateur = r.is_admin WHERE u.pseudo = ?";
		jdbcUserDetailsManager.setUsersByUsernameQuery(usersByUsernameQuery);
		jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(authoritiesByUsernameQuery);
		return jdbcUserDetailsManager;
	}

	// Autorisations : acces url
	@Bean
	SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests(auth -> {
			auth.requestMatchers("/").permitAll()
				.requestMatchers("/utilisateur/creer-compte").permitAll()
				.requestMatchers("/utilisateur/profil").hasRole("USER")
				.requestMatchers("/encheres/detail").hasRole("USER")
				.requestMatchers("/css/*").permitAll()
				.requestMatchers("/js/*").permitAll()
				.requestMatchers("/images/*").permitAll()
				.requestMatchers("/vendre").hasRole("USER")
				.anyRequest().authenticated();
		});
		httpSecurity.formLogin(form -> {
			form.loginPage("/login").permitAll();
			form.defaultSuccessUrl("/", true).permitAll();
            form.failureUrl("/login?error=true"); 	
		});

		httpSecurity.logout(logout -> logout.invalidateHttpSession(true)
											.clearAuthentication(true)
											.deleteCookies("JSESSIONID")
											.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
											.logoutSuccessUrl("/")
											.permitAll());
		return httpSecurity.build();
	}
}
