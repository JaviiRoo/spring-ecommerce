package com.rodsan.ecommerce.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodsan.ecommerce.model.Producto;
import com.rodsan.ecommerce.repository.IProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

	@Autowired
	private IProductoRepository productoRepository;
	
	@Override
	public Producto save(Producto producto) {

		return productoRepository.save(producto);
	}

	@Override
	public Optional<Producto> get(Integer id) {
		
		return productoRepository.findById(id);
	}

	@Override
	public void update(Producto producto) {
		
		productoRepository.save(producto);
		
	}

	@Override
	public void delete(Integer id) {
		
		productoRepository.deleteById(id);
		
	}

	@Override
	public List<Producto> findall() {
		
		return productoRepository.findAll();
	}

}
