package com.rodsan.ecommerce.services;

import java.util.List;

import com.rodsan.ecommerce.model.Orden;

public interface IOrdenService {
	
	List <Orden> findAll();
	
	Orden save (Orden orden);
	
	String generarNumeroOrden();

}
