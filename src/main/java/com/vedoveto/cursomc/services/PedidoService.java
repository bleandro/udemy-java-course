package com.vedoveto.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vedoveto.cursomc.domain.Categoria;
import com.vedoveto.cursomc.domain.Pedido;
import com.vedoveto.cursomc.repositories.PedidoRepository;
import com.vedoveto.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	public Pedido findById(Integer id) {
		Optional<Pedido> categoria = pedidoRepository.findById(id);
		return categoria.orElseThrow(
				() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}
	
}
