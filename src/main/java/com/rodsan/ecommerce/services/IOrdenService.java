package com.rodsan.ecommerce.services;

import java.util.List;

import com.rodsan.ecommerce.model.Orden;
import com.rodsan.ecommerce.model.Usuario;

public interface IOrdenService {
	
	List <Orden> findAll();
	
	Orden save (Orden orden);
	
	String generarNumeroOrden();
	
	List <Orden> findByUsuario (Usuario usuario);

}
