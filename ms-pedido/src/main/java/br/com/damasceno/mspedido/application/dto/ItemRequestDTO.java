package br.com.damasceno.mspedido.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemRequestDTO(
        @NotNull
        String produtoId,

        String descricao,

        @NotNull @Min(value = 1, message = "A quantidade deve see no mínimo 1")
        Integer quantidade,

        @NotNull(message = "O preço não pode ser nulo")
        BigDecimal precoUnitario) {}
