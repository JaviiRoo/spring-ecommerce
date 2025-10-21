package com.rodsan.ecommerce.services;

import java.util.Optional;

import com.rodsan.ecommerce.model.Producto;

public interface ProductoService {

	public Producto save(Producto producto);
	public Optional <Producto> get(Integer id);
	public void update(Producto producto);
	public void delete(Integer id);
}
