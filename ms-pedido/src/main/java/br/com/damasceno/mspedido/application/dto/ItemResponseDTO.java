package br.com.damasceno.mspedido.application.dto;

import java.math.BigDecimal;

public record ItemResponseDTO(
        Long id,
        String produtoId,
        String descricao,
        Integer quantidade,
        BigDecimal precoUnitario) {}
