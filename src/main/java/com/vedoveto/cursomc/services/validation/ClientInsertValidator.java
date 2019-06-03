package com.vedoveto.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.vedoveto.cursomc.domain.enums.TipoCliente;
import com.vedoveto.cursomc.dto.ClienteNewDTO;
import com.vedoveto.cursomc.resources.exceptions.FieldMessage;
import com.vedoveto.cursomc.services.validation.utils.BR;

public class ClientInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO>
{
	@Override
	public void initialize(ClienteInsert ann)
	{
	}

	@Override
	public boolean isValid(ClienteNewDTO objDto, ConstraintValidatorContext context)
	{
		List<FieldMessage> list = new ArrayList<>();

		if (objDto.getTipo() == TipoCliente.PESSOAFISICA.getCod() && !BR.isValidCPF(objDto.getCpfOuCnpj()))
		{
			list.add(new FieldMessage("cpfOuCnpj", "CPF inválido"));
		}
		else if (objDto.getTipo() == TipoCliente.PESSOAJURIDICA.getCod() && !BR.isValidCNPJ(objDto.getCpfOuCnpj()))
		{
			list.add(new FieldMessage("cpfOuCnpj", "CNPJ inválido"));
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