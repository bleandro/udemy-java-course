package com.vedoveto.cursomc.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vedoveto.cursomc.domain.Cidade;
import com.vedoveto.cursomc.domain.Estado;
import com.vedoveto.cursomc.dto.CidadeDTO;
import com.vedoveto.cursomc.dto.EstadoDTO;
import com.vedoveto.cursomc.services.CidadeService;
import com.vedoveto.cursomc.services.EstadoService;

@RestController
@RequestMapping("/estados")
public class EstadoResource {
	@Autowired
	private EstadoService service;
	
	@Autowired
	private CidadeService cidadeService;
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<EstadoDTO>> findAll() {
		List<Estado> listEstado= service.findAll();
		List<EstadoDTO> listDTO = listEstado.stream().map(obj -> new EstadoDTO(obj)).collect(Collectors.toList()); 
		
		return ResponseEntity.ok(listDTO);
	}
	
	@RequestMapping(value="/{estadoId}/cidades", method=RequestMethod.GET)
	public ResponseEntity<List<CidadeDTO>> findCidades(@PathVariable Integer estadoId) {
		List<Cidade> listCidade = cidadeService.findByEstado(estadoId);
		List<CidadeDTO> listDTO = listCidade.stream().map(obj -> new CidadeDTO(obj)).collect(Collectors.toList()); 
		
		return ResponseEntity.ok(listDTO);
	}
}
