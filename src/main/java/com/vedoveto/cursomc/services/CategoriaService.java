package com.vedoveto.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.vedoveto.cursomc.domain.Categoria;
import com.vedoveto.cursomc.dto.CategoriaDTO;
import com.vedoveto.cursomc.repositories.CategoriaRepository;
import com.vedoveto.cursomc.services.exceptions.DataIntegrityException;
import com.vedoveto.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public Categoria findById(Integer id) {
		Optional<Categoria> categoria = categoriaRepository.findById(id);
		return categoria.orElseThrow(
				() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));
	}

	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return categoriaRepository.save(obj);
	}

	public Categoria update(Categoria obj) {
		findById(obj.getId());		
		
		return categoriaRepository.save(obj);
	}

	public void delete(Integer id) {
		try {
			categoriaRepository.deleteById(id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir uma Categoria que possui produtos");
		}
	}

	public List<Categoria> findAll() {
		return categoriaRepository.findAll();
	}
	
	public Page<Categoria> findPage(Integer pageIndex, Integer linesPerPage, String orderBy, String orderDirection) {
		PageRequest pageRequest = PageRequest.of(pageIndex, linesPerPage, Direction.valueOf(orderDirection), orderBy);
		return categoriaRepository.findAll(pageRequest);
	}
	
	public Categoria fromDTO(CategoriaDTO objDTO) {
		return new Categoria(objDTO.getId(), objDTO.getNome());
	}
}
