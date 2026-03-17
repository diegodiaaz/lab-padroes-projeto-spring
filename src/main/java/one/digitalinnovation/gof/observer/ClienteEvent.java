package one.digitalinnovation.gof.observer;

import one.digitalinnovation.gof.model.Cliente;
import org.springframework.context.ApplicationEvent;

/**
 * Evento publicado sempre que uma operação relevante ocorre sobre um Cliente.
 * Representa o "sinal" que o Subject emite para os Observers.
 */
public class ClienteEvent extends ApplicationEvent {

    public enum Tipo { INSERIDO, ATUALIZADO, DELETADO }

    private final Cliente cliente;
    private final Tipo tipo;

    public ClienteEvent(Object source, Cliente cliente, Tipo tipo) {
        super(source);
        this.cliente = cliente;
        this.tipo = tipo;
    }

    public Cliente getCliente() { return cliente; }
    public Tipo getTipo() { return tipo; }
}