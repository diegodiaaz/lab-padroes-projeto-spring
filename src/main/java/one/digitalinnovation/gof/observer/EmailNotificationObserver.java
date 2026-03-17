package one.digitalinnovation.gof.observer;

import one.digitalinnovation.gof.model.Cliente;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observer concreto que escuta eventos de Cliente e simula o envio de e-mail.
 */
@Component
public class EmailNotificationObserver {

    @EventListener
    public void onClienteEvent(ClienteEvent event) {
        Cliente cliente = event.getCliente();

        String mensagem = switch (event.getTipo()) {
            case INSERIDO   -> "Bem-vindo, " + cliente.getNome() + "! Seu cadastro foi realizado.";
            case ATUALIZADO -> "Olá, " + cliente.getNome() + "! Seus dados foram atualizados.";
            case DELETADO   -> "O cadastro de " + cliente.getNome() + " foi removido do sistema.";
        };

        // Simulação — para envio real, injetar JavaMailSender aqui e aplicar as mudanças pertinentes
        // ao model Cliente, que atualmente não recebe nenhum e-mail real.
        System.out.printf("[EMAIL] Para: %s | Mensagem: %s%n", cliente.getNome(), mensagem);
    }
}