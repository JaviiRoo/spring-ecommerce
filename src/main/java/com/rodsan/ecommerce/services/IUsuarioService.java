package com.rodsan.ecommerce.services;

import java.util.Optional;

import com.rodsan.ecommerce.model.Usuario;

public interface IUsuarioService {
	
	Optional <Usuario> findById(Integer id);
	Usuario save (Usuario usuario);

}
