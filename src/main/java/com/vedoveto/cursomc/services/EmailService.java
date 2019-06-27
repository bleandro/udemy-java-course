package com.vedoveto.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.vedoveto.cursomc.domain.Pedido;

public interface EmailService {
	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
}
