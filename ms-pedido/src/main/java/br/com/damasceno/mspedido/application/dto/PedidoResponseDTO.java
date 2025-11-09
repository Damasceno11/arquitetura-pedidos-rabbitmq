package br.com.damasceno.mspedido.application.dto;

import br.com.damasceno.mspedido.domain.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        Long clienteId,
        LocalDateTime dataCriacao,
        StatusPedido status,
        BigDecimal total,
        List<ItemRequestDTO> itens) {}
