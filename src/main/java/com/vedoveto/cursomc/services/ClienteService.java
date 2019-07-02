package com.vedoveto.cursomc.services;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vedoveto.cursomc.domain.Cidade;
import com.vedoveto.cursomc.domain.Cliente;
import com.vedoveto.cursomc.domain.Endereco;
import com.vedoveto.cursomc.domain.enums.Perfil;
import com.vedoveto.cursomc.domain.enums.TipoCliente;
import com.vedoveto.cursomc.dto.ClienteDTO;
import com.vedoveto.cursomc.dto.ClienteNewDTO;
import com.vedoveto.cursomc.repositories.ClienteRepository;
import com.vedoveto.cursomc.repositories.EnderecoRepository;
import com.vedoveto.cursomc.security.UserSS;
import com.vedoveto.cursomc.services.exceptions.AuthorizationException;
import com.vedoveto.cursomc.services.exceptions.DataIntegrityException;
import com.vedoveto.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private BCryptPasswordEncoder pe; 
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private S3Service s3Service;
	
	public Cliente findById(Integer id) {
		
		UserSS user = UserService.authenticated();
		if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId()))
			throw new AuthorizationException("Acesso negado");
		
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.orElseThrow(
				() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = clienteRepository.save(obj);
		
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}
	
	public Cliente update(Cliente obj) {
		Cliente newObj = findById(obj.getId());
		updateData(newObj, obj);		
		return clienteRepository.save(newObj);
	}

	public void delete(Integer id) {
		try {
			clienteRepository.deleteById(id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas");
		}
	}

	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}
	
	public Page<Cliente> findPage(Integer pageIndex, Integer linesPerPage, String orderBy, String orderDirection) {
		PageRequest pageRequest = PageRequest.of(pageIndex, linesPerPage, Direction.valueOf(orderDirection), orderBy);
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDTO) {
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null, null);
	}
	
	public Cliente fromDTO(@Valid ClienteNewDTO objDTO) {
		Cliente cli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj(), TipoCliente.toEnum(objDTO.getTipo()), pe.encode(objDTO.getSenha()));
		Cidade cid = new Cidade(objDTO.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDTO.getLogradouro(), objDTO.getNumero(), objDTO.getComplemento(), objDTO.getBairro(), objDTO.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDTO.getTelefone1());
		
		if (objDTO.getTelefone2() != null)
			cli.getTelefones().add(objDTO.getTelefone2());
		
		if (objDTO.getTelefone3() != null)
			cli.getTelefones().add(objDTO.getTelefone3());
		
		return cli;
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		UserSS user = UserService.authenticated();
		if (user == null)
			throw new AuthorizationException("Acesso negado");		
		
		URI uri = s3Service.uploadFile(multipartFile);
		
		Cliente cli = findById(user.getId());
		cli.setImageUrl(uri.toString());
		clienteRepository.save(cli);
		
		return uri;
	}
}
