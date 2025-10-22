package com.rodsan.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rodsan.ecommerce.model.Producto;
import com.rodsan.ecommerce.services.ProductoService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

	@Autowired
	private ProductoService productoService;
	
	@GetMapping("")
	public String home(Model model) {
		
		List<Producto> productos = productoService.findall();
		
		model.addAttribute("productos", productos);
		
		return "administrador/home";
	}
}
