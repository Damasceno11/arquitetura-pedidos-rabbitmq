package br.com.damasceno.mspedido.infrastructure.messaging.event;

import java.math.BigDecimal;

public record PedidoCriadoEvent(
        Long pedidoId,
        Long ClienteId,
        String emailCliente,
        BigDecimal total
) {}
