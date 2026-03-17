package one.digitalinnovation.gof.proxy;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.observer.ClienteEvent;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;
import one.digitalinnovation.gof.model.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementação do padrão <b>Proxy</b> para {@link ClienteService}.
 *
 * Intercepta as operações de inserção e atualização para aplicar um
 * cache de CEPs em memória, evitando chamadas desnecessárias à API ViaCEP.
 *
 * O Proxy implementa a mesma interface do serviço real ({@link ClienteService}),
 * sendo transparente para o {@link one.digitalinnovation.gof.controller.ClienteRestController}.
 */
@Service
@Primary // Spring injeta este bean onde ClienteService for requerido
public class ClienteServiceProxy implements ClienteService {

    // Cache em memória: CEP -> Endereco já consultado
    private final Map<String, Endereco> cacheViaCep = new HashMap<>();

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ViaCepService viaCepService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCepCacheado(cliente);
        eventPublisher.publishEvent(new ClienteEvent(this, cliente, ClienteEvent.Tipo.INSERIDO));
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        cliente.setId(id);
        salvarClienteComCepCacheado(cliente);
        eventPublisher.publishEvent(new ClienteEvent(this, cliente, ClienteEvent.Tipo.ATUALIZADO));
    }

    @Override
    public Iterable<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    @Override
    public void deletar(Long id) {
        clienteRepository.findById(id).ifPresent(cliente -> {
            clienteRepository.deleteById(id);
            eventPublisher.publishEvent(new ClienteEvent(this, cliente, ClienteEvent.Tipo.DELETADO));
        });
    }

    /**
     * Lógica central do Proxy: cache de CEP
     *
     * Verifica se o CEP do cliente já está no cache.
     * - Se SIM: reutiliza o Endereco cacheado (sem chamar ViaCEP).
     * - Se NÃO: consulta a API ViaCEP, persiste o endereço e armazena no cache.
     */
    private void salvarClienteComCepCacheado(Cliente cliente) {
        String cep = cliente.getEndereco().getCep();

        Endereco endereco = cacheViaCep.computeIfAbsent(cep, this::buscarEPersistirEndereco);

        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
    }

    private Endereco buscarEPersistirEndereco(String cep) {
        // Verifica banco antes de ir à API (persistência entre reinicializações)
        return enderecoRepository.findById(cep).orElseGet(() -> {
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            return enderecoRepository.save(novoEndereco);
        });
    }
}