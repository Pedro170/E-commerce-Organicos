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
import com.ecommerce.organicos.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuariosController {

	@Autowired
	private UsuarioService service;

	@GetMapping
	public ResponseEntity<List<Usuarios>> listarTodos() {
		return new ResponseEntity<List<Usuarios>>(service.listarTodos(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Optional<Usuarios>> buscarPorId(@PathVariable Long id) {
		return new ResponseEntity<Optional<Usuarios>>(service.buscarPorId(id), HttpStatus.OK);
	}

	@GetMapping("/produtores/{nome}")
	public ResponseEntity<List<Usuarios>> getByName(@RequestParam(defaultValue = "") String nome) {
		return new ResponseEntity<List<Usuarios>>(service.buscarPorNome(nome), HttpStatus.OK);

	}

	@GetMapping("/produtores/{regiao}")
	public ResponseEntity<List<Usuarios>> getByRegiao(@RequestParam(defaultValue = "") String regiao) {
		return new ResponseEntity<List<Usuarios>>(service.buscarPorRegiao(regiao), HttpStatus.OK);
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

	@DeleteMapping("/deletar/{id}")
	public void deletar(Usuarios usuarios) {
		service.deletar(usuarios);
	}
	
	@PostMapping("/produto/novo/{id_Usuario}")
	public ResponseEntity<?> novoProduto(
			@PathVariable(value = "id_Usuario") Long idUsuario,
			@Valid @RequestBody Produtos novoProduto) {
		Produtos cadastro = service.cadastrarProduto(novoProduto, idUsuario);
		if(cadastro == null) {
			return new ResponseEntity<String>("Falha no cadastro", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Produtos>(cadastro, HttpStatus.CREATED);
	}
	
	@PutMapping("/produto/edite/{id_Usuario}")
	public ResponseEntity<?> editarProduto(
			@PathVariable(value = "id_Usuario") Long idUsuario,
			@Valid @RequestBody Produtos produto) {
		Optional<Produtos> alterado = service.editarProduto(idUsuario, produto);
		if(alterado.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto inexistente");
		}else {
			return ResponseEntity.status(HttpStatus.OK).body(alterado.get());
		}
	}
	
	@PutMapping("/produto/compra/{id_Produto}/{id_Usuario}")
	public ResponseEntity<?> novaCompra(
			@PathVariable(value = "id_Produto") Long idProduto,
			@PathVariable(value = "id_Usuario") Long idUsuario,
			@RequestParam(defaultValue = "") int qtdCompras) {
		Usuarios compra = service.comprarProduto(idUsuario, idProduto, qtdCompras);
		if(compra == null) {
			return new ResponseEntity<String>("Produto ou usuário invalido", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Usuarios>(compra, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/produto/delete/{id_Produto}/{id_Usuario}")
	public ResponseEntity<?> removerProduto(
			@PathVariable(value = "id_Produto")Long idProduto,
			@PathVariable(value = "id_Usuario")Long idUsuario){
		Usuarios retorno = service.deletarProduto(idProduto, idUsuario);
		if(retorno == null) {
			return new ResponseEntity<String>("Produto ou usuário invalido", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Usuarios>(retorno, HttpStatus.ACCEPTED);
	}
}
