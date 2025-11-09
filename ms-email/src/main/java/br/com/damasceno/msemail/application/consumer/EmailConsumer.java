package br.com.damasceno.msemail.application.consumer;

import br.com.damasceno.msemail.application.service.EmailService;
import br.com.damasceno.msemail.domain.event.PedidoCriadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void onPedidoCriado(@Payload PedidoCriadoEvent event) {
        try{
            log.info("Mensagem recebida doRabbitMq: Pedido {}", event.pedidoId());
            emailService.enviarEmailConfirmacaoPedido(event);
        } catch (Exception e){
            log.error("Falha ao processar mensagem do pedido {}. Erro {}", event.pedidoId(), e.getMessage());

            throw e;
        }
    }
}
