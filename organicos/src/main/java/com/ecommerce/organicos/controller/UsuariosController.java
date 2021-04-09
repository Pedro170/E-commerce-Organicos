package com.ecommerce.organicos.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.organicos.model.Produtos;
import com.ecommerce.organicos.model.Usuarios;
import com.ecommerce.organicos.repository.ProdutosRepository;
import com.ecommerce.organicos.repository.UsuariosRepository;
import com.ecommerce.organicos.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuariosController {

	@Autowired
	private UsuarioService service;
	
	@Autowired
	private UsuariosRepository repository;
	
	@Autowired
	private ProdutosRepository repositoryProduto;

	@GetMapping
	public ResponseEntity<List<Usuarios>> listarTodos() {
		return new ResponseEntity<List<Usuarios>>(service.listarTodos(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Optional<Usuarios>> buscarPorId(@PathVariable Long id) {
		return new ResponseEntity<Optional<Usuarios>>(service.buscarPorId(id), HttpStatus.OK);
	}
	
	
	@GetMapping("/nome")
	public ResponseEntity<List<Usuarios>> buscarPorNome(@RequestParam(defaultValue = "") String nome) {
		return new ResponseEntity<List<Usuarios>>(service.buscarPorNome(nome), HttpStatus.OK);

	}

	@GetMapping("/produtores/regiao")
	public ResponseEntity<List<Usuarios>> buscarPorRegiao(@RequestParam(defaultValue = "") String endereco) {
		return new ResponseEntity<List<Usuarios>>(service.buscarPorRegiao(endereco), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Usuarios> postar(@RequestBody Usuarios usuarios) {
		return new ResponseEntity<Usuarios>(service.postar(usuarios), HttpStatus.CREATED);
	}

	@PutMapping
	public ResponseEntity<?> alterar(@RequestBody Usuarios usuarios) {
		Optional<Usuarios> alterado = service.alterar(usuarios);
		if (alterado.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário Inexistente");
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(alterado.get());
		}
	}

	@PutMapping("/alterar/login")
	public ResponseEntity<?> alterarLogin(@RequestBody Usuarios usuarios) {
		Optional<Usuarios> alterado = service.alterarLogin(usuarios);
		if (alterado.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário Inexistente");
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(alterado.get());
		}
	}
	
	@PostMapping("/produto/novo/{id_usuario}")
	public ResponseEntity<?> novoProduto(
			@PathVariable(value = "id_usuario") Long idUsuario,
			@Valid @RequestBody Produtos novoProduto) {
		Produtos cadastro = service.cadastrarProduto(novoProduto, idUsuario);
		if(cadastro == null) {
			return new ResponseEntity<String>("Falha no cadastro", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Produtos>(cadastro, HttpStatus.CREATED);
	}
	
	@PutMapping("/produto/edite/{id_usuario}")
	public ResponseEntity<?> editarProduto(
			@PathVariable(value = "id_usuario") Long idUsuario,
			@Valid @RequestBody Produtos produto) {
		Optional<Produtos> alterado = service.editarProduto(idUsuario, produto);
		if(alterado.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto inexistente");
		}else {
			return ResponseEntity.status(HttpStatus.OK).body(alterado.get());
		}
	}
	
	@PutMapping("/produto/compra/{id_Produto}/{id_usuario}")
	public ResponseEntity<?> novaCompra(
			@PathVariable(value = "id_Produto") Long idProduto,
			@PathVariable(value = "id_usuario") Long idUsuario,
			@RequestParam(defaultValue = "") int qtdCompras) {
		Optional<Usuarios> usuarioExistente = repository.findById(idUsuario);
		Optional<Produtos> produtoExistente = repositoryProduto.findById(idProduto);
		if(usuarioExistente.get().getIdUsuario() == produtoExistente.get().getCriadoPor().getIdUsuario()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O usuário criador não pode comprar seu próprio produto");
		} else {
			produtoExistente.get().setQtdEstoque(produtoExistente.get().getQtdEstoque()-qtdCompras);
			if(produtoExistente.get().getQtdEstoque() >= produtoExistente.get().getQtdCompras()) {
				Usuarios compra = service.comprarProduto(idUsuario, idProduto, qtdCompras);
				if(compra == null) {
					return new ResponseEntity<String>("Produto ou usuário inválido", HttpStatus.NO_CONTENT);
				}
				return new ResponseEntity<Usuarios>(compra,HttpStatus.CREATED);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estoque insuficiente");
			}
		}
	}
	
	@DeleteMapping("/produto/delete/{id_Produto}/{id_usuario}")
	public ResponseEntity<?> removerProduto(
			@PathVariable(value = "id_Produto")Long idProduto,
			@PathVariable(value = "id_usuario")Long idUsuario){
		Usuarios retorno = service.deletarProduto(idProduto, idUsuario);
		if(retorno == null) {
			return new ResponseEntity<String>("Produto ou usuário invalido", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Usuarios>(retorno, HttpStatus.ACCEPTED);
	}

}
