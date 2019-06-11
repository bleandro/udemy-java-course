package com.vedoveto.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.vedoveto.cursomc.domain.Cliente;
import com.vedoveto.cursomc.dto.ClienteDTO;
import com.vedoveto.cursomc.repositories.ClienteRepository;
import com.vedoveto.cursomc.resources.exceptions.FieldMessage;

public class ClientUpdateValidator implements ConstraintValidator<ClienteUpdate, ClienteDTO>
{
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private ClienteRepository repo;
	
	@Override
	public void initialize(ClienteUpdate ann)
	{
	}

	@Override
	public boolean isValid(ClienteDTO objDto, ConstraintValidatorContext context)
	{
		Map<String, String> map = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Integer uriId = Integer.parseInt(map.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();

		Cliente cliente = repo.findByEmail(objDto.getEmail());
		if (cliente != null && cliente.getId() != uriId)
		{
			list.add(new FieldMessage("email", "Email já existente"));
		}

		for (FieldMessage e : list)
		{
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}

		return list.isEmpty();
	}
}