package com.rodsan.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodsan.ecommerce.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository <Producto, Integer> {

}
