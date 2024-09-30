package com.Ap1.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Ap1.model.Cliente;
import com.Ap1.repository.ClienteRepository;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepositorio;

    public ClienteController(ClienteRepository clienteRepositorio2) {
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return new ResponseEntity<>(clienteRepositorio.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable("id") int id) {
        return this.clienteRepositorio.findById(id)
                                      .map(post -> new ResponseEntity<>(post, HttpStatus.OK))
                                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @PostMapping
    public ResponseEntity<Cliente> salvar(@Valid @RequestBody Cliente cliente) {
        this.clienteRepositorio.save(cliente);
        return new ResponseEntity<>(cliente, HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable("id") int id) {
        Optional<Cliente> optCliente = this.clienteRepositorio.findById(id);

        if (optCliente.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        this.clienteRepositorio.delete(optCliente.get());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}