package com.rodsan.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rodsan.ecommerce.model.Orden;
import com.rodsan.ecommerce.model.Usuario;
import com.rodsan.ecommerce.services.IOrdenService;
import com.rodsan.ecommerce.services.IUsuarioService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping ("/usuario")
public class UsuarioController {
	
	private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
	
	@GetMapping ("/registro")
	public String create () {
		return "usuario/registro";
	}
	
	@PostMapping ("/save")
	public String save(Usuario usuario) {
		
		logger.info("Usuario registro: {}", usuario);
		usuario.setTipo("USER");
		usuario.setPassword(passEncoder.encode(usuario.getPassword()));
		usuarioService.save(usuario);
		
		return "redirect:/";
	}
	
	@GetMapping ("/login")
	public String login () {
		return "usuario/login";
	}
	
	@GetMapping ("/acceder")
	public String acceder (Usuario usuario, HttpSession session) {
		
		logger.info("Accesos: {}", usuario);
		
		Optional <Usuario> user = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString()));
		//logger.info("Usuario de db: {} ", user.get());
		
		if (user.isPresent()) {
			session.setAttribute("idusuario", user.get().getId());
			if (user.get().getTipo().equals("ADMIN")) {
				return "redirect:/administrador";
			} else {
				return "redirect:/";
			}
		} else {
			logger.info("Usuario no existe");
		}
		
		return "redirect:/";
	}
	
	@GetMapping ("/compras")
	public String obtenerCompras(Model model, HttpSession session) {
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		List <Orden> ordenes = ordenService.findByUsuario(usuario);
		
		model.addAttribute("ordenes", ordenes);
		
		return "usuario/compras";
	}
	
	@GetMapping ("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model) {
		
		logger.info("Id de la orden: {}", id);
		Optional <Orden> orden = ordenService.findById(id);
		
		model.addAttribute("orden", orden.get());
		model.addAttribute("detalles", orden.get().getDetalle());
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		return "usuario/detallecompra";
		
	}
	
	@GetMapping ("/cerrar")
	public String cerrarSesion(HttpSession session) {
		
		session.removeAttribute("idusuario");
		return "redirect:/";
	}
	
	@GetMapping("/orden/{id}/descargar")
	public void descargarOrden(@PathVariable Integer id, HttpServletResponse response) throws IOException {
	    Optional<Orden> ordenOpt = ordenService.findById(id);
	    
	    if (ordenOpt.isEmpty()) {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Orden no encontrada");
	        return;
	    }

	    Orden orden = ordenOpt.get();

	    response.setContentType("application/pdf");
	    response.setHeader("Content-Disposition", "attachment; filename=orden_" + orden.getNumero() + ".pdf");

	    try {
	        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
	        com.itextpdf.text.pdf.PdfWriter.getInstance(document, response.getOutputStream());
	        document.open();

	        document.add(new com.itextpdf.text.Paragraph("Orden #" + orden.getNumero()));
	        document.add(new com.itextpdf.text.Paragraph("Fecha: " + orden.getFechaCreacion()));
	        document.add(new com.itextpdf.text.Paragraph("Cliente: " + orden.getUsuario().getNombre()));
	        document.add(new com.itextpdf.text.Paragraph("Total: $" + orden.getTotal()));
	        document.add(new com.itextpdf.text.Paragraph(" "));
	        document.add(new com.itextpdf.text.Paragraph("Detalles de la orden:"));

	        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
	        table.addCell("Producto");
	        table.addCell("Cantidad");
	        table.addCell("Precio Unitario");
	        table.addCell("Subtotal");

	        for (var detalle : orden.getDetalle()) {
	            table.addCell(detalle.getProducto().getNombre());
	            table.addCell(String.valueOf(detalle.getCantidad()));
	            table.addCell(String.format("$%.2f", detalle.getPrecio()));
	            table.addCell(String.format("$%.2f", detalle.getTotal()));
	        }

	        document.add(table);
	        document.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generando el PDF");
	    }
	}

	
	@GetMapping("/orden/{id}/imprimir")
	public String imprimirOrden(@PathVariable Integer id, Model model, HttpSession session) {
	    Optional<Orden> ordenOpt = ordenService.findById(id);

	    if (ordenOpt.isEmpty()) {
	        return "redirect:/usuario/compras";
	    }

	    Orden orden = ordenOpt.get();
	    model.addAttribute("orden", orden);
	    model.addAttribute("detalles", orden.getDetalle());
	    model.addAttribute("sesion", session.getAttribute("idusuario"));

	    return "usuario/orden_imprimir"; // Vista HTML que generaremos a continuación
	}

	@GetMapping("/perfil")
	public String perfil(Model model, HttpSession session) {
	    Object idUsuarioObj = session.getAttribute("idusuario");
	    if (idUsuarioObj == null) {
	        return "redirect:/usuario/login";
	    }

	    Integer idUsuario = Integer.parseInt(idUsuarioObj.toString());
	    Optional<Usuario> usuarioOpt = usuarioService.findById(idUsuario);

	    if (usuarioOpt.isEmpty()) {
	        return "redirect:/";
	    }

	    Usuario usuario = usuarioOpt.get();

	    // Obtener órdenes del usuario
	    List<Orden> ordenes = ordenService.findByUsuario(usuario);

	    // Calcular estadísticas
	    double totalGastado = ordenes.stream().mapToDouble(Orden::getTotal).sum();
	    int totalPedidos = ordenes.size();

	    model.addAttribute("usuario", usuario);
	    model.addAttribute("totalGastado", totalGastado);
	    model.addAttribute("totalPedidos", totalPedidos);
	    model.addAttribute("sesion", idUsuario);

	    return "usuario/perfil";
	}

	@PostMapping("/perfil/actualizar")
	public String actualizarPerfil(Usuario usuarioActualizado, HttpSession session) {
	    Object idUsuarioObj = session.getAttribute("idusuario");
	    if (idUsuarioObj == null) {
	        return "redirect:/usuario/login";
	    }

	    Integer idUsuario = Integer.parseInt(idUsuarioObj.toString());
	    Optional<Usuario> usuarioOpt = usuarioService.findById(idUsuario);

	    if (usuarioOpt.isEmpty()) {
	        return "redirect:/";
	    }

	    Usuario usuario = usuarioOpt.get();
	    // Actualizamos solo campos editables
	    usuario.setNombre(usuarioActualizado.getNombre());
	    usuario.setDireccion(usuarioActualizado.getDireccion());
	    usuario.setTelefono(usuarioActualizado.getTelefono());
	    usuario.setEmail(usuarioActualizado.getEmail());

	    usuarioService.save(usuario);

	    return "redirect:/usuario/perfil?success";
	}


	
}
