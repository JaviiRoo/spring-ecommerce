package com.rodsan.ecommerce.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rodsan.ecommerce.model.Orden;
import com.rodsan.ecommerce.model.Producto;
import com.rodsan.ecommerce.services.IOrdenService;
import com.rodsan.ecommerce.services.IUsuarioService;
import com.rodsan.ecommerce.services.ProductoService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	private Logger logg = LoggerFactory.getLogger(AdministradorController.class);
	
	@GetMapping("")
	public String home(Model model) {
		
		List<Producto> productos = productoService.findall();
		
		model.addAttribute("productos", productos);
		
		return "administrador/home";
	}
	
	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		
		model.addAttribute("usuarios", usuarioService.findAll());
		return "administrador/usuarios";
	}
	
	@GetMapping("/ordenes")
	public String ordenes (Model model) {
		
		model.addAttribute("ordenes", ordenService.findAll());
		return "administrador/ordenes";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalle(Model model, @PathVariable Integer id) {
	    logg.info("Id de la orden: {}", id);
	    
	    Orden orden = ordenService.findById(id).orElse(null);

	    if (orden == null) {
	        logg.error("No se encontró la orden con id {}", id);
	        model.addAttribute("error", "No se encontró la orden solicitada.");
	        return "administrador/error"; // o redirige a una página de error si prefieres
	    }

	    // ✅ Agregar tanto la orden como los detalles al modelo
	    model.addAttribute("orden", orden);
	    model.addAttribute("detalles", orden.getDetalle());

	    return "administrador/detalleorden";
	}

}
