package br.com.damasceno.mspedido.infrastructure.messaging.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routingkey.name}")
    private String routingKey;

    public void enviarEventoPedidoCriado(PedidoCriadoEvent event) {
        log.info("Enviando evento PedidoCriadoEvent para RabbitMQ. Pedido ID: {}", event.pedidoId());

        try{
            rabbitTemplate.convertAndSend(exchangeName, routingKey, event);

            log.info("Evento enviado com sucesso para a exchange: {}", exchangeName);
        }catch (Exception e){
            log.error("Erro ao enviar evento para RabbitMQ. Pedido ID: {}", event.pedidoId(), e);
        }
    }
}


















