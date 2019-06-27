package com.vedoveto.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vedoveto.cursomc.domain.ItemPedido;
import com.vedoveto.cursomc.domain.PagamentoComBoleto;
import com.vedoveto.cursomc.domain.Pedido;
import com.vedoveto.cursomc.domain.Produto;
import com.vedoveto.cursomc.domain.enums.EstadoPagamento;
import com.vedoveto.cursomc.repositories.ItemPedidoRepository;
import com.vedoveto.cursomc.repositories.PagamentoRepository;
import com.vedoveto.cursomc.repositories.PedidoRepository;
import com.vedoveto.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
		
	public Pedido findById(Integer id) {
		Optional<Pedido> categoria = pedidoRepository.findById(id);
		return categoria.orElseThrow(
				() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}

	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteService.findById(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = pedidoRepository.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		
		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			Produto produto = produtoService.findById(ip.getProduto().getId());
			ip.setProduto(produto);
			ip.setPreco(produto.getPreco());
			ip.setPedido(obj);
		}
		itemPedidoRepository.saveAll(obj.getItens());
		
		System.out.println(obj);
		
		return obj;
	}
	
}
