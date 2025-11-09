package br.com.damasceno.msemail.application.service;

import br.com.damasceno.msemail.domain.event.PedidoCriadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarEmailConfirmacaoPedido(PedidoCriadoEvent event) {
        log.info("Recebido evento para enviar email para o pedido: {}", event.pedidoId());

        // 1. Simulação de Falha (Teste de Resiliência)
        if (event.pedidoId() % 2 == 0) {
            log.warn("Falha simulada! O serviço de email está 'fora'. Pedido ID: {}", event.pedidoId());
            throw new RuntimeException("Simulação de falha no envio de email.");
        }

        // 2. Preparação do Email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nao-responda@pedidos.com");
        message.setTo(event.emailCliente());
        message.setSubject("Confirmação de Pedido #" + event.pedidoId());

        String body = String.format(
                "Olá!\n\nSeu pedido nº %d, no valor de R$ %.2f, foi recebido com sucesso.\n\nObrigado!",
                event.pedidoId(),
                event.total()
        );
        message.setText(body);

        // 3. Envio do Email
        try {
            mailSender.send(message);
            log.info("Email enviado com sucesso para: {}", event.emailCliente());
        } catch (Exception e) {
            log.error("Erro real ao enviar email (verifique credenciais Mailtrap): {}", e.getMessage());
            throw e;
        }
    }
}