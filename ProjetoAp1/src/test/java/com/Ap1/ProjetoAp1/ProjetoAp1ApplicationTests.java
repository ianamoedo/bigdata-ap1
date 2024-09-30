package com.Ap1.ProjetoAp1;

import com.Ap1.model.Cliente;
import com.Ap1.model.Endereco;
import com.Ap1.repository.ClienteRepository;
import com.Ap1.repository.EnderecoRepository;
import com.Ap1.service.ClienteService;
import com.Ap1.service.EnderecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class GerenciamentoDeClientesTest {

    @Mock
    private ClienteRepository clienteRepositorio;

    @Mock
    private EnderecoRepository enderecoRepositorio;

    @InjectMocks
    private ClienteService clienteServico;

    @InjectMocks
    private EnderecoService enderecoServico;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Testes de Cliente

    @Test
    public void deveFalharSeNomeNaoForPreenchido() {
        Cliente cliente = new Cliente();
        cliente.setNome("");  // Nome obrigatório, mas está vazio
        cliente.setEmail("cliente@mail.com");
        cliente.setCpf("123.456.789-10");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setTelefone("(11) 91234-5678");

        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Nome é obrigatório")),
                "Deve falhar pois o nome não foi preenchido");
        verify(clienteRepositorio, never()).save(any(Cliente.class));
    }

    @Test
    public void deveFalharSeEmailForInvalido() {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setEmail("email_invalido");  // Email em formato inválido
        cliente.setCpf("123.456.789-10");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setTelefone("(11) 91234-5678");

        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email deve ser um email válido")),
                "Deve falhar pois o email está em formato inválido");
        verify(clienteRepositorio, never()).save(any(Cliente.class));
    }

    @Test
    public void deveFalharSeCpfForInvalido() {
        Cliente cliente = new Cliente();
        cliente.setNome("João Pereira");
        cliente.setEmail("joao.pereira@mail.com");
        cliente.setCpf("11111111111");  // CPF sem formatação correta
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setTelefone("(11) 91234-5678");

        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("CPF deve seguir o formato XXX.XXX.XXX-XX")),
                "Deve falhar pois o CPF não segue o formato correto");
        verify(clienteRepositorio, never()).save(any(Cliente.class));
    }

    @Test
    public void deveFalharSeClienteForMenorDe18Anos() {
        Cliente cliente = new Cliente();
        cliente.setNome("Pedro Júnior");
        cliente.setEmail("pedro.junior@mail.com");
        cliente.setCpf("123.456.789-10");
        cliente.setDataNascimento(LocalDate.of(2010, 1, 1));  // Menor de 18 anos
        cliente.setTelefone("(11) 91234-5678");

        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Cliente deve ter no mínimo 18 anos")),
                "Deve falhar pois o cliente tem menos de 18 anos");
        verify(clienteRepositorio, never()).save(any(Cliente.class));
    }

    @Test
    public void deveSalvarClienteValido() {
        Cliente cliente = new Cliente();
        cliente.setNome("Ana Maria");
        cliente.setEmail("ana.maria@mail.com");
        cliente.setCpf("123.456.789-10");
        cliente.setDataNascimento(LocalDate.of(1985, 1, 1));  // Cliente maior de idade
        cliente.setTelefone("(11) 91234-5678");

        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);
        assertTrue(violations.isEmpty(), "Cliente válido não deve ter violações");
        when(clienteRepositorio.save(any(Cliente.class))).thenReturn(cliente);
        clienteServico.salvar(cliente);
        verify(clienteRepositorio, times(1)).save(cliente);
    }

    // Testes de Endereço

    @Test
    public void deveFalharSeRuaNaoForPreenchida() {
        Endereco endereco = new Endereco();
        endereco.setRua("");  // Rua obrigatória, mas está vazia
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345-678");

        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Rua é obrigatória")),
                "Deve falhar pois a rua não foi preenchida");
        verify(enderecoRepositorio, never()).save(any(Endereco.class));
    }

    @Test
    public void deveFalharSeCepForInvalido() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua Principal");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("1234567");  // CEP inválido

        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("CEP deve seguir o formato XXXXX-XXX")),
                "Deve falhar pois o CEP não segue o formato correto");
        verify(enderecoRepositorio, never()).save(any(Endereco.class));
    }

    @Test
    public void deveFalharSeEstadoForInvalido() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua Principal");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("XY");  // Estado inválido
        endereco.setCep("12345-678");

        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Estado inválido")),
                "Deve falhar pois o estado é inválido");
        verify(enderecoRepositorio, never()).save(any(Endereco.class));
    }

    @Test
    public void deveSalvarEnderecoValido() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua J");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345-678");

        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertTrue(violations.isEmpty(), "Endereço válido não deve ter violações");
        when(enderecoRepositorio.save(any(Endereco.class))).thenReturn(endereco);
        enderecoServico.salvar(endereco);
        verify(enderecoRepositorio, times(1)).save(endereco);
    }
}