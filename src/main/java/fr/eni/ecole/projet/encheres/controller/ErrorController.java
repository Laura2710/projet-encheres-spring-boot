package fr.eni.ecole.projet.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
/*
@Controller
@RequestMapping("/error")
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
	
	@GetMapping
	public String gererErreurs(HttpServletRequest request, Model model) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		
		if (status != null) {
	        Integer statusCode = Integer.valueOf(status.toString());
	    
	        if(statusCode == 403) {
	            model.addAttribute("status", 403);
	        }
	        else if(statusCode == 404) {
	            model.addAttribute("status", 404);
	        }
	        else if(statusCode == 500) {
	            model.addAttribute("status", 500);
	        }
	        else if(statusCode == 503) {
	            model.addAttribute("status", 503);
	        }
	    }
		
		return "view-errors";
	}
}
*/