package br.com.damasceno.msemail.domain.event;

import java.math.BigDecimal;

public record PedidoCriadoEvent(
        Long pedidoId,
        Long clientId,
        String emailCliente,
        BigDecimal total
) {}
