package com.rodsan.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rodsan.ecommerce.services.ProductoService;

@Controller
@RequestMapping("/")
public class HomeController {
	
	@Autowired
	private ProductoService productoService;
	
	@GetMapping("")
	public String home(Model model) {
		
		model.addAttribute("productos", productoService.findall());
		
		return "usuario/home";
	}

}
